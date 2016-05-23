var spatialTrackerControllers = angular.module('spatialTrackerControllers', []);

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
                    $scope.success = true;
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

spatialTrackerControllers.controller('ProjectController', ['$scope', '$http',
    function ($scope, $http) {
        $scope.newProject = {};
        $scope.createProject = function () {
            delete $scope.success;
            delete $scope.errors;
            
            $http.post('rest/project', $scope.newProject)
                .success(function() {
                    $scope.success = true;
                })
                .error(function (data) {
                    $scope.errors = data.errors;
                })
            ;
        };
    }
]);