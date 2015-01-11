var app = angular.module("bigdataApp", ["ngRoute","ngResource", "angular-loading-bar","ngTable" ]);

app.config(["$routeProvider",function($routeProvider){
	$routeProvider.
		when('/dashboard', {
			templateUrl: 'partials/dashboard.html'
		}).
		when('/users', {
			templateUrl: 'partials/users.html',
			controller: 'UsersCtrl'
		}).
		when("/campaigns", {
			templateUrl: 'partials/campaigns.html',
			controller: 'CampaignsCtrl'
		}).
		when("/query", {
			templateUrl: 'partials/query.html',
			controller: 'QueryCtrl',
			controllerAs: "qc"
		}).
		otherwise({
			redirectTo: '/dashboard'
		});
}]);


app.filter("empty", function(){
	return function(input,output){
		output = output || "--empty--";
		if(angular.isUndefined(input)){
			return output;
		}
		input = input || "";
		if(angular.isString(input) && input === ""){
			return output;
		}
		return input;
	};
});