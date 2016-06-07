var spatialTrackerControllers = angular.module('spatialTrackerControllers', []);

var categoryOptions = [{ value: 'ADD', title: 'Add' }, { value: 'REMOVE', title: 'Remove' }, {value: 'REPAIR', title: 'Repair'}, { value: 'COMPLAINT', title: 'Complaint' }];
var statusOptions = [{ value: 'REPORTED', title: 'Reported' }, { value: 'ACCEPTED', title: 'Accepted'}, { value: 'REJECTED', title: 'Rejected' }, { value: 'FIXED', title: 'Fixed' }];
var priorityOptions = [{ value: 'WANT_TO_HAVE', title: 'Want to have' }, { value:  'CAN_HAVE', title: 'Can have' }, { value: 'SHOULD_HAVE', title: 'Should have' }, { value: 'MUST_HAVE', title: 'Must have' }];

function getBounds(issues) {
    var sw = { lat: Number.MAX_SAFE_INTEGER, lon: Number.MAX_SAFE_INTEGER };
    var ne = { lat: Number.MIN_SAFE_INTEGER, lon: Number.MIN_SAFE_INTEGER };

    for (var i = 0; i < issues.length; i++) {
        var issue = issues[i];
        
        sw.lat = Math.min(issue.coords.lat, sw.lat);
        sw.lon = Math.min(issue.coords.lon, sw.lon);
        ne.lat = Math.max(issue.coords.lat, ne.lat);
        ne.lon = Math.max(issue.coords.lon, ne.lon);
    }
    
    return issues.length > 0 ? new google.maps.LatLngBounds(new google.maps.LatLng(sw.lat, sw.lon), new google.maps.LatLng(ne.lat, ne.lon)) : null;
};

spatialTrackerControllers.controller('LoginController', ['$scope', '$http', 
    function($scope, $http) {
        $http.get('rest/user/me').success(function(data) {
            $scope.loggedUser = data;
        });
        
        function getToken(login, password) {
            return btoa(login + ':' + password);
        };
        
        function getCookie(name) {
            var value = "; " + document.cookie;
            var parts = value.split("; " + name + "=");
            
            return (parts.length === 2) ? parts.pop().split(";").shift() : null;
        };
        
        $http.defaults.transformRequest.push(function (data, headersGetter) {
            var headers = headersGetter();
            
            if (getCookie('token') !== null && !headers.Authorization && (!data || (!data.login && !data.pass))) {
                headers.Authorization = 'Basic ' + getCookie('token');
            }

            return data;
        });
        
        $scope.loginData = {};
        $scope.login = function () {
            delete $scope.loginSuccess;
            delete $scope.loginError;
            
            $http.post('rest/user/login', $scope.loginData)
                .success(function(data) {
                    document.cookie = 'token='+getToken($scope.loginData.login, $scope.loginData.pass);
                    
                    $scope.loginSuccess = true;
                    $scope.loggedUser = data;
                })
                .error(function () {
                    $scope.loginError = true;
                })
            ;
        };
    }
]);

spatialTrackerControllers.controller('RegisterController', ['$scope', '$http',
    function($scope, $http) {
        $scope.user = {};
        $scope.register = function () {
            if ($scope.user.password !== $scope.user.passwordRepeat) {
                alert('Passwords don\'t match');
                
                return false;
            }
            
            delete $scope.user.passwordRepeat;
            delete $scope.success;
            delete $scope.errors;
            
            $http.post('rest/user', $scope.user)
                .success(function() {
                    $scope.success = 'Registration was successful. You can now login.';
                })
                .error(function (data) {
                    $scope.errors = data.errors;
                })
            ;
        };
    }
]);

spatialTrackerControllers.controller('LogoutController', ['$scope', '$http', 
    function($scope, $http) {
        document.cookie = 'token=';
        
        window.location.hash = '';
        window.location.reload();
    }
]);

spatialTrackerControllers.controller('IssueController', ['$scope', '$http', 
    function($scope, $http) {
        var markers = [];
        var map = new google.maps.Map(document.getElementById('issuesMap'), {
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            center: new google.maps.LatLng(49.1952, 16.6079),
            zoom: 14
        });
        
        $scope.categoryOptions = categoryOptions;
        $scope.statusOptions = statusOptions;
        $scope.priorityOptions = priorityOptions;
        $scope.newIssue = { coords: { lat: 49.1952, lon: 16.6079 } };
        $scope.newIssueCoords = $scope.newIssue.coords;
        $scope.createIssue = function () {
            delete $scope.success;
            delete $scope.errors;
            
            $http.post('rest/issue', $scope.newIssue)
                .success(function() {
                    $scope.success = 'Issue was successfuly created';
                    $scope.displayIssues();
                })
                .error(function (data) {
                    $scope.errors = data.errors;
                })
            ;
        };
        $scope.displayIssues = function () {
            var filter = $scope.filter.filter(function (filter) { return !filter.isEmpty(); });
            
            $http.get('rest/issue', { params: { filter: JSON.stringify(filter) } }).success(function (issues) {
                $scope.issues = issues;
                $scope.updateMarkers();
            });
        };
        $scope.resetFilter = function() {
            $scope.filter = [
                { '@type': 'category', 'in': [], isEmpty: function () { return this.in.length === 0; } },
                { '@type': 'status', 'in': [], isEmpty: function () { return this.in.length === 0; } },
                { '@type': 'priority', isEmpty: function () { return !this.min || !this.max; } },
                { '@type': 'spatial', isEmpty: function () { return isNaN(parseFloat(this.lat_min)) || isNaN(parseFloat(this.lat_max)) || isNaN(parseFloat(this.lon_min)) || isNaN(parseFloat(this.lon_max)); } },
                { '@type': 'date', isEmpty: function () { return !this.from || !this.to; } }
            ];
            
            $('select[multiple].select2-hidden-accessible').select2('destroy');
            setTimeout(function () { $('select[multiple]').select2(); }, 10);
        };
        $scope.updateMarkers = function () {
            markers.forEach(function (marker) { marker.setMap(null); });
            markers = [];
            
            for (var i = 0; i < $scope.issues.length; i++) {
                var issue = $scope.issues[i];
                var marker = new google.maps.Marker({ position: new google.maps.LatLng(issue.coords.lat, issue.coords.lon), map: map, title: issue.subject });
            
                markers.push(marker);
            }
            
            if (markers.length > 0) {
                map.fitBounds(getBounds($scope.issues));
            }
        };
        
        $scope.resetFilter();
        $scope.displayIssues();
        $('#map').spatialtrackermap({ editation: true });
        $('select[multiple]').css('width', '100%').select2();
    }
]);

spatialTrackerControllers.controller('IssueDetailController', ['$scope', '$http', '$routeParams', '$resource', 
    function($scope, $http, $routeParams, $resource) {
        $http.get('rest/issue/' + $routeParams.issueId).success(function (issue) {
            $scope.issue = issue;
            
            $('#map').spatialtrackermap({ coords: issue.coords });
        });
        $scope.statusOptions = statusOptions;
        $scope.categoryOptions = categoryOptions;
        $scope.priorityOptions = priorityOptions;
        $scope.updateStatus = function (status) {
            delete $scope.success;
            delete $scope.errors;
            
            $http.post('rest/issue/' + $routeParams.issueId, { status: status }).success(function () {
                $scope.success = 'Status successfully updated';
                $scope.issue.status = status;
            }).error(function () {
                $scope.errors = [ 'Error while updating status' ];
            });
        };
        
        var roomName = 'issue' + $routeParams.issueId;
        var Message = $resource('rest/messages/' + roomName);

        $scope.messages = [];
        $scope.addMessage = function() {
            var message = new Message();
            message.text = $scope.messageText;
            message.$save();

            $scope.messageText = '';
        };

        Socket.subscribe('/socket/messages/' + roomName, function(message) {
            $scope.messages.push(message);
            $scope.$digest();
        });

        Message.query(function(messages) {
            $scope.messages = messages;
        });
    }
]);

spatialTrackerControllers.controller('HeatMapController', ['$scope', '$http', 
    function($scope, $http) {
        var areas = [];
        var map = new google.maps.Map(document.getElementById('heatMap'), {
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            center: new google.maps.LatLng(49.1952, 16.6079),
            zoom: 14
        });
        
        $http.get('rest/issue', { params: { filter: JSON.stringify([]) } }).success(function (issues) {
            if (issues.length > 0) {
                var bounds = getBounds(issues);

                map.fitBounds(bounds);

                $scope.drawAreas();
            }
        });
        
        var timeout;
        
        $scope.drawAreasDelayed = function () {
            clearTimeout(timeout);
            
            timeout = setTimeout(function () { $scope.drawAreas(); }, 500);
        };
        
        $scope.drawAreas = function () {
            var bounds = map.getBounds();
            
            $http.get('rest/heat_map', { params: { filter: JSON.stringify({ '@type': 'spatial', lat_min: bounds.getSouthWest().lat(), lon_min: bounds.getSouthWest().lng(), lat_max: bounds.getNorthEast().lat(), lon_max: bounds.getNorthEast().lng() }) } }).success(function (data) {
                areas.forEach(function (area) { area.setMap(null); });
                areas = [];
                
                data.forEach(function (coords) {
                    var rectangle = new google.maps.Rectangle({
                        strokeWeight: 0,
                        fillColor: 'red',
                        fillOpacity: 0.1 + (Math.min(coords.value, 20) / 20) * 0.5,
                        map: map,
                        bounds: { south: coords.coordinates1.lat, west: coords.coordinates1.lon, north: coords.coordinates2.lat, east: coords.coordinates2.lon }
                    });
                    
                    areas.push(rectangle);
                });
            });
        };
        
        map.addListener('bounds_changed', function () { $scope.drawAreasDelayed(); });
    }
]);