qmApp.controller("Page2Ctrl",['$scope', 'time',function($scope,time){
$scope.yourname="qweic1";	
$scope.time = time;
Page2Ctrl.scrop=$scope;
$scope.Page2Ctrl=Page2Ctrl;
}]);
Page2Ctrl={
	clickMe:function(){				
		var picDiv=document.getElementById("picDiv");
		picDiv.innerHTML=this.scrop.yourname; 
	}, 
	clickMe2:function(){
		 //var $injector=	angular.injector("qmApp");
		//var result=injector.get("Page1Ctrl")===injector.get("Page1Ctrl");
		//console.log('the result is:"+result);
		//GAJavaScript.performSelector("loadImage","http://img0.bdstatic.com/img/image/cd9e399f3aa075d48358ea4d19e758711414636895.jpg","Page2Ctrl.callback","");
		//var img1=document.getElementById("img1");
		//img1.src="http://img0.bdstatic.com/img/image/shouye/hjxnz-11723371136.jpg"
		//var myDiv=document.getElementById("myDiv");
		//myDiv.innerHTML=img1.src;
	},
	jumpTo:function (){
		GAJavaScript.performSelector("jumpTo","{page:'login',callback:'callback'}");    		
	},
	callback:function (result,tag){
		var myDiv=document.getElementById("myDiv");
		myDiv.innerHTML="result:"+result;
		var img1=document.getElementById("img1");
		img1.src=result;
	}
}
