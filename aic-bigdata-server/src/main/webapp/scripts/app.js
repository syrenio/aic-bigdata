var app = angular.module("bigdataApp", ["ngRoute","ngResource", "angular-loading-bar" ]);

app.config(["$routeProvider",function($routeProvider){
	$routeProvider.
		when('/dashboard', {
			templateUrl: 'partials/dashboard.html'
		}).
		when('/users', {
			templateUrl: 'partials/users.html',
			controller: 'UsersCtrl'
		}).
		otherwise({
			redirectTo: '/dashboard'
		});
}]);

app.controller("ServiceCtrl", function($scope, $http) {
	function exCommand(cmd){
		$http.get("api/service?command="+cmd).success(function(data) {
			$scope.result = data;
		});
	}

	$scope.startService = function() {
		exCommand("start");
	};
	$scope.stopService = function() {
		exCommand("stop");
	};
	$scope.startExtraction = function() {
		exCommand("extraction");
	};
	$scope.startAnalyse = function() {
		exCommand("analyse");
	};
	$scope.getStatus = function() {
		$http.get("api/service/status", {}).success(function(data) {
			$scope.result = data;
		});
	};
});

app.factory("ConnectionService", function($http) {
	var srv = {};

	srv.getAllTopics = function() {
		return $http.get("api/connections/topics", {}).then(function(resp) {
			return resp.data;
		});
	};

	srv.findUsersByTopic = function(topic) {
		console.info("Topic selected: " + topic);
		return $http.get("api/connections/topics/" + topic + "/users", {})
				.then(function(resp) {
					return resp.data;
				});
	};

	return srv;
});

app.factory("UserService", function($http) {
	var srv = {};

	srv.getConnections = function(userId) {
		return $http.get("api/users/" + userId + "/connections", {}).then(
				function(resp) {
					return resp.data;
				});
	};

	return srv;
});

app.controller("ConnectionCtrl", function($scope, ConnectionService) {
	$scope.topics = [];
	$scope.selTopic;
	$scope.connectedUsers = [];
	
	var sig = null;
	var g = null;

	ConnectionService.getAllTopics().then(function(data) {
		$scope.topics = data;
	});

	$scope.findUsers = function() {
		ConnectionService.findUsersByTopic($scope.selTopic).then(
				function(data) {
					console.log(data);
					$scope.connectedUsers = data.nodes;

					
					if(sig != null){
						sig.kill();
					}
					sig = new sigma({
						graph: data,
						container: "graphContainer",
						settings : {
							defaultNodeColor : '#ec5148'
						}
					});	
				});
	}
});

app.controller("UsersCtrl", function($scope, $http, UserService) {
	$scope.pageSize = 100;
	$scope.pageNumber = 0;

	function _init() {
		getUsers();
	}

	function getUsers() {
		var url = "api/users?size=" + $scope.pageSize + "&page="
				+ $scope.pageNumber;
		$http.get(url).success(function(data) {
			$scope.users = data;
		});
	}

	$scope.selectUser = function(user) {
		UserService.getConnections(user.id).then(function(data) {
			console.log(data);
		});
	}

	$scope.updateUsers = function() {
		getUsers();
	}

	_init();
});


app.directive("aicSpinner",function(){
	return {
		template: '<div class="spinner"><div class="rect1"></div><div class="rect2"></div><div class="rect3"></div><div class="rect4"></div><div class="rect5"></div></div>'
	};
});