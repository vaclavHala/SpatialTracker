var spatialTrackerApp = angular.module('spatialTrackerApp', ['ngRoute', 'spatialTrackerControllers']);

var ngCompareTo = function() {
    return {
        require: "ngModel",
        scope: { otherModelValue: "=ngCompareTo" },
        link: function(scope, element, attributes, ngModel) {
             
            ngModel.$validators.compareTo = function(modelValue) {
                return modelValue == scope.otherModelValue;
            };
 
            scope.$watch("otherModelValue", function() {
                ngModel.$validate();
            });
        }
    };
};
 
spatialTrackerApp.directive("ngCompareTo", ngCompareTo);

spatialTrackerApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'partials/register.html',
                controller: 'RegisterController'
            })
            .when('/issues', {
                templateUrl: 'partials/issues.html',
                controller: 'IssueController'
            })
            .when('/logout', {
                templateUrl: 'partials/register.html',
                controller: 'LogoutController'
            })
            .otherwise({
                redirectTo: '/'
            })
        ;
    }
]);