/**
 * 
 */

var app = angular.module("bigdataApp");

app.directive("aicSpinner",function(){
	return {
		template: '<div class="spinner"><div class="rect1"></div><div class="rect2"></div><div class="rect3"></div><div class="rect4"></div><div class="rect5"></div></div>'
	};
});

app.directive("aicUserTable",function(){
	return {
		restrict: "EA",
		controller : "UsersCtrl",
		templateUrl: "partials/users.html",
		scope : {
			onSelect : "&onSelect"
		},
		link: function(scope,element,attrs,ctrl){
			ctrl.addSelectFunction(function(u){
				console.log("test");
				if(scope.onSelect){
					scope.onSelect({user:u});
				}
			});
		}
	};
});
