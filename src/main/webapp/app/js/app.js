var spatialTrackerApp = angular.module('spatialTrackerApp', ['ngRoute', 'spatialTrackerControllers', 'ngResource']);

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
spatialTrackerApp.filter('reverse', function() { return function(items) { return items.slice().reverse(); }; });
spatialTrackerApp.filter('optionTitle', function() { return function(value, options) {
    var option = options.filter(function (option) { return option.value === value; })[0];
    
    return option ? option.title : null;
}; });

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
            .when('/issues/:issueId', {
                templateUrl: 'partials/issue.html',
                controller: 'IssueDetailController'
            })
            .when('/heat_map', {
                templateUrl: 'partials/heat_map.html',
                controller: 'HeatMapController'
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