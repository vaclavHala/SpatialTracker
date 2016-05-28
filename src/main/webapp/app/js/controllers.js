var spatialTrackerControllers = angular.module('spatialTrackerControllers', []);
var categoryOptions = { 'ADD': 'Add', 'REMOVE': 'Remove', 'REPAIR': 'Repair', 'COMPLAINT': 'Complaint' };
var statusOptions = { 'REPORTED': 'Reported', 'ACCEPTED': 'Accepted', 'REJECTED': 'Rejected', 'FIXED': 'Fixed' };
var priorityOptions = { 'WANT_TO_HAVE': 'Want to have', 'CAN_HAVE': 'Can have', 'SHOULD_HAVE': 'Should have', 'MUST_HAVE': 'Must have' };

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
        $scope.categoryOptions = categoryOptions;
        $scope.statusOptions = statusOptions;
        $scope.priorityOptions = priorityOptions;
        $scope.newIssue = { coords: { lat: 49.1952, lon: 16.6079 } };
        $scope.newIssueCoords = $scope.newIssue.coords;
        $scope.filter = [
            { '@type': 'category', 'in': [], isEmpty: function () { return this.in.length === 0; } },
            { '@type': 'status', 'in': [], isEmpty: function () { return this.in.length === 0; } },
            { '@type': 'priority', isEmpty: function () { return !this.min || !this.max; } },
            { '@type': 'spatial', isEmpty: function () { return isNaN(parseFloat(this.min_lat)) || isNaN(parseFloat(this.max_lat)) || isNaN(parseFloat(this.min_lon)) || isNaN(parseFloat(this.max_lon)); } }
        ];
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
            });
        };
        
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
