qmApp.controller("PageOtherCtrl",['$scope','$routeParams','$http','$location',function($scope,$routeParams,$http,$location){	
	var obj = $routeParams;
	$scope.msg=angular.toJson(obj);
}]);
