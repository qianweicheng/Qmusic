(function(window) {
	qmApp.controller("Page1Ctrl", [ '$scope', 'time', '$q',
			function($scope, time, $q) {
				$scope.$on('$viewContentLoaded', function() {
					console.log('Page1Ctrl==============');
					Qm.activeScope = $scope;
				});
				$scope.$on('back-clicked', function(data) {
					console.log('Page1Ctrl back-clicked:' + data);
				});
				$scope.yourname = "qweic1";
				$scope.time = time;
				$scope.isActive = false;
				var defer = $q.defer();
				var p = defer.promise;
				var p2 = p.then(function(data) {
					return data;
				}, function(data) {
					console.log('1111: ' + data);
					return 'success:' + data;
					// return $q.reject(data+'add');
				})
				p2.then(function(data) {
					console.log('ok, ' + data);
				}, function(data) {
					console.log('error, ' + data);
				})
				defer.reject('123');
			} ]);

	qmApp.controller("Page2Ctrl", [
			'$scope',
			'time',
			'$routeParams',
			function($scope, time, $routeParams) {
				$scope.$on('$viewContentLoaded', function() {
					console.log('Page2Ctrl==============');
					Qm.activeScope = $scope;
				});
				$scope.yourname = "qweic1";
				$scope.time = time;
				$scope.id = $routeParams.id;
				$scope.clickMe = function() {
					var picDiv = document.getElementById("picDiv");
					picDiv.innerHTML = this.scrop.yourname;
				};
				$scope.clickMe2 = function() {
					// var $injector= angular.injector("qmApp");
					// var
					// result=injector.get("Page1Ctrl")===injector.get("Page1Ctrl");
					// console.log('the result is:"+result);
					// GAJavaScript.performSelector("loadImage","http://img0.bdstatic.com/img/image/cd9e399f3aa075d48358ea4d19e758711414636895.jpg","Page2Ctrl.callback","");
					// var img1=document.getElementById("img1");
					// img1.src="http://img0.bdstatic.com/img/image/shouye/hjxnz-11723371136.jpg"
					// var myDiv=document.getElementById("myDiv");
					// myDiv.innerHTML=img1.src;
				};
				$scope.clickMe3 = function() {
					alert('this is from js');
				};
				$scope.clickMe4 = function() {
					var r = confirm('this is from js');
					console.log('selected:' + r);
				};
				$scope.jumpTo = function() {
					GAJavaScript.performSelector("jumpTo",
							"{page:'login',callback:'callback'}");
				};
				$scope.callback = function(result, tag) {
					var myDiv = document.getElementById("myDiv");
					myDiv.innerHTML = "result:" + result;
					var img1 = document.getElementById("img1");
					img1.src = result;
				}

			} ]);
	qmApp
			.controller(
					"Page3Ctrl",
					[
							'$scope',
							'$routeParams',
							'$http',
							function($scope, $routeParams, $http) {
								$scope.$on('$viewContentLoaded', function() {
									console.log('Page3Ctrl==============');
									Qm.activeScope = $scope;
								});
								$scope.id = $routeParams.id;
								$http
										.get(
												"http://www.w3schools.com/website/Customers_JSON.php")
										.success(function(response) {
											$scope.names = response;
										});
							} ]);

	qmApp.controller("PageOtherCtrl", [ '$scope', '$routeParams', '$http',
			'$location', function($scope, $routeParams, $http, $location) {
				$scope.$on('$viewContentLoaded', function() {
					console.log('PageOtherCtrl==============');
					Qm.activeScope = $scope;
				});
				var obj = $routeParams;
				$scope.msg = angular.toJson(obj);
			} ]);

})(this);