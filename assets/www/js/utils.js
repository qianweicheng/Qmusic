(function(window) {
	"use strict";
	window.isEmptyStr = function(str) {
		return (!str || /^\s*$/.test(str));
	};
	
	window.isEmptyObj = function(obj){
		   for (var name in obj){
			   if(obj.hasOwnProperty(name)){
			   		return false;
			 		}
		   }
		   return true;
	};
	Object.prototype.length=function(){
			var count=0;
			for (var name in this){
			   if(this.hasOwnProperty(name)){
			   		//console.log('property:'+name+';value:'+this[name]);
			   		count++;
			  }
		  }
		  return count;	
	};
})(this);
