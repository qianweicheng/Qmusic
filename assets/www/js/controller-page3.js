qmApp.controller("Page3Ctrl",['$scope','$routeParams','$http',function($scope,$routeParams,$http){	
	$scope.id =$routeParams.id;
    $http.get("http://www.w3schools.com/website/Customers_JSON.php")
    .success(function(response) {$scope.names = response;});
}]);
