qmApp.controller("Page1Ctrl",['$scope', 'time','$q',function($scope,time,$q){
$scope.yourname="qweic1";	
$scope.time = time;
$scope.userInfo=Qm.userInfo;//$rootScope.userInfo;

 var defer = $q.defer();
  var p = defer.promise;
	var p2=  p.then(
    function(data){
    		return data;
    },
    function(data){
    		console.log('1111: '+data);
    		return 'success:'+data;
    	  //return $q.reject(data+'add');
    }
  )
 	p2.then(
   function(data){
   		console.log('ok, ' + data);
   },
   function(data){
   		console.log('error, ' + data);
   }
  )
  defer.reject('123');
}]);
 
