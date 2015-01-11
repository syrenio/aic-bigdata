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
			controller: 'QueryCtrl'
		}).
		otherwise({
			redirectTo: '/dashboard'
		});
}]);
