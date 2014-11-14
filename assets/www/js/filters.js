var myFilter=angular.module("qmFilter",[]);
myFilter.filter("hello",function(){
						        		return function(name){
						        							return 'hello, '+name+'!';
						        						}
												}
							);