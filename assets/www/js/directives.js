var myDirective=angular.module("qmDirective",[]);
/*full demo*/ 
myDirective.directive('hello', function() {
    return {
        restrict: 'E',
        template: '<div>Hi there <span ng-transclude></span>End</div>',
        transclude: true,//replace the body inside the element
        replace:true//replace the tag
    }});

myDirective.directive('test1', function(){
    var link = function($scope, $element, $attrs, $ctrl){
      $scope.do = function(){
        //$ctrl.$setDirty();
        console.log($ctrl.$pristine);  
        console.log($ctrl.$dirty);  
        console.log($ctrl.$valid);  
        console.log($ctrl.$invalid);
        console.log($ctrl.$error);  
      }
    }
  
    return {compile: function(){return link},
            require: 'form',
            restrict: 'A'}
  });
  
/*draggable*/
myDirective.directive('draggable',function($document){
	var startX=0,startY=0,x=0,y=0;
	return function(scrop,element,attr){
		 				element.css({
						position: 'relative',
						border: '1px solid red', backgroundColor: 'lightgrey', cursor: 'pointer'
						});
						element.bind('mousedown', function(event) {
						startX = event.screenX - x;
						startY = event.screenY - y; 
						$document.bind('mousemove', mousemove); 
						$document.bind('mouseup', mouseup);
						});
						function mousemove(event) {
							y = event.screenY - startY;
							x = event.screenX - startX; 
							element.css({
								top: y + 'px',
								left: x + 'px' });
						}
						function mouseup() {
					 		$document.unbind('mousemove', mousemove); 
					 		$document.unbind('mouseup', mouseup);
						}
		}
	
	});