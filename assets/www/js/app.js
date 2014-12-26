(function(window) {
	"use strict";
	var qmApp = angular.module('qmApp', [ 'qmDirective', 'qmFilter',
			'timeService', 'ngRoute' ]);
	qmApp.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/', {
			templateUrl : '../html/template/page1.html',
			controller : 'Page1Ctrl'
		}).when('/page1', {
			templateUrl : '../html/template/page1.html',
			controller : 'Page1Ctrl'
		}).when('/page2', {
			templateUrl : '../html/template/page2.html',
			controller : 'Page2Ctrl'
		}).when('/page3/123', {
			templateUrl : '../html/template/page-other.html',
			controller : 'PageOtherCtrl'
		}).when('/page3/:id', {
			templateUrl : '../html/template/page3.html',
			controller : 'Page3Ctrl'
		}).otherwise({
			// redirectTo : '/'
			templateUrl : '../html/template/page-other.html',
		});
	} ]);

	qmApp.run([ '$rootScope', function($rootScope) {
		console.log("qmApp run");
		$rootScope.Qm = window.Qm;
		window.Qm.rootScope = $rootScope;
	} ]);/**/
	window.qmApp = qmApp;
})(this);