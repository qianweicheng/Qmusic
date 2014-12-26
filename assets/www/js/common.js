(function(window) {
	"use strict";
	var Qm = {
		onStart : function() {
			console.log("onStart");
		},
		onResume : function() {
			console.log("onResume");
		},
		onPause : function() {
			console.log("onPause");
		},
		onStop : function() {
			console.log("onStop");
		},
		onDestory : function() {
			console.log("onDestory");
		},
		userInfo : {
			username : 'qweic',
			word : 'Life is Good'
		}
	};

	// =========start of Qm global properties==============
	Qm.rootScope = null;
	Qm.activeScope = null;
	// =========end of Qm global properties================

	Qm.nativeEvent = function(event, args) {
		if (Qm.rootScope) {
			console.log(event + ':' + args);
			Qm.rootScope.$broadcast(event, args);
			// Qm.rootScope.$emit('to-parent', 'parent');
		} else {
			console.log('root scope is null');
		}
	};
	Qm.callback = function(func, result) {
		if (Qm.activeScope && Qm.activeScope[func]) {
			try {
				Qm.activeScope[func](result);
			} catch (e) {
				console.log(e.name + ':' + e.message);
			}
		}
	};

	window.Qm = Qm;
})(this);
