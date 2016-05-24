(function($) {
    
    $.widget('spatialtracker.spatialtrackermap', {
        _map: null,
        _marker: null,
        _searchBox: null,
        
        _$latField: null,
        _$lonField: null,
        
        options: {
            latField: '#lat',
            lonField: '#lon',
            geolocationElement: '#deviceLocationBtn',
            editation: false,
            lat: null,
            lon: null,
            zoom: 14,
            
            init: null
        },
        
        setCoordinates: function(lat, lon) {
            var coords = new google.maps.LatLng(lat, lon);
            
            this._setMarker(this._marker, coords);
            
            if (this._$latField !== null && this._$lonField !== null) {
                this._setCoordinates(coords);
            }
        },
        
        _create: function() {
            this.element.append('<div class="map" style="height: 400px;"></div>');
            
            this._$latField = $(this.options.latField);
            this._$lonField = $(this.options.lonField);

            this._map = new google.maps.Map(this.element.find('.map')[0], {
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                center: new google.maps.LatLng(49.1952, 16.6079),
                zoom: 14
            });

            this._marker = new google.maps.Marker({
                map: this._map,
                draggable: this.options.editation,
                visible: false
            });
            
            if (this.options.editation) {
                this._initEditation();
            }
            
            if (this.options.lat !== null && this.options.lon !== null) {
                this._setMarker(this._marker, new google.maps.LatLng(this.options.lat, this.options.lon));
            }
            
            this._trigger('init', null, { map: this._map, marker: this._marker });
        },
        
        _initEditation: function () {
            this.element.append('<input class="search-place form-control" style="width: 60%; margin-top: 0.5em;">');
            
            var input = this.element.find('.search-place')[0];
            var that = this;
            
            this._searchBox = new google.maps.places.SearchBox(input);
            this._map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

            google.maps.event.addListener(this._searchBox, 'places_changed', function() {
                var places = that._searchBox.getPlaces();

                if (places.length === 1) {
                    that._setMarker(that._marker, places[0].geometry.location);
                }
            });

            google.maps.event.addListener(this._marker, 'position_changed', function() {
                that._setCoordinates(that._marker.getPosition());
            });
            
            $(input).keypress(function (event) { if (event.keyCode === 13) { event.preventDefault(); } });
            
            if (this.options.geolocationElement) {
                this._initBrowserGeolocation();
            }
            
            if (this._getCoordinates()) {
                this._updateMarker();
            }

            this._$latField.change(function () { that._updateMarker(); });
            this._$lonField.change(function () { that._updateMarker(); });
        },
        
        _initBrowserGeolocation: function() {
            var that = this;
            
            $(this.options.geolocationElement).click(function (e) {
                e.preventDefault();

                navigator.geolocation.getCurrentPosition(function (position) {
                    that._setMarker(that._marker, new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
                });
            });
        },
        
        _getCoordinates: function() {
            var lat = this._$latField.val() ? this._$latField.val().replace(/,/, '.') : null;
            var long = this._$lonField.val() ? this._$lonField.val().replace(/,/, '.') : null;

            if (!lat || !long) {
                return null;
            }

            return new google.maps.LatLng(parseFloat(lat), parseFloat(long));
        },
        
        _updateMarker: function() {
            this._setMarker(this._marker, this._getCoordinates());
        },
        
        _setCoordinates: function (latLng) {
            this._$latField.val(latLng.lat());
            this._$lonField.val(latLng.lng());
        },
        
        _setMarker: function(marker, location) {
            marker.map.setZoom(this.options.zoom);
            marker.map.panTo(location);
            marker.setPosition(location);
            marker.setVisible(true);
        }
    });
})(jQuery);