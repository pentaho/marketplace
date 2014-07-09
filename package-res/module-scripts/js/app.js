'use strict';

var app = angular.module('marketplace', ['ngResource', 'ngRoute']);

app.config(['$routeProvider', function( $routeProvider ) {

        $routeProvider.when('/',
        {
            controller: 'appController'
        });

    $routeProvider.when('/plugins',
        {
            templateUrl: 'partials/plugin-list.html',
            controller: 'appController'
        });

        $routeProvider.when('/plugin/:pluginId',
        {
            templateUrl: 'partials/plugin-detail.html',
            controller: 'appController'
        });

    $routeProvider.otherwise(
        {
            redirectTo: '/'
        });
/*
    $routeProvider.when("/", {
        templateUrl: "partials/testView.html",
        controller: "TestController"
    });*/
}]);

//enable CORS in Angular http requests
/*app.config(['$httpProvider', function($httpProvider) {
 $httpProvider.defaults.useXDomain = true;
 delete $httpProvider.defaults.headers.common['X-Requested-With'];
 }]);*/