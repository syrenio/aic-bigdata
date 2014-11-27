var app = angular.module("bigdataApp", [ 'angular-loading-bar' ]);

app.controller("ServiceCtrl", function($scope, $http) {
	$scope.startService = function() {
		$http.get("api/service?command=start").success(function(data) {
			$scope.result = data;
		});
	};
	$scope.stopService = function() {
		$http.get("api/service?command=stop").success(function(data) {
			$scope.result = data;
		});
	};
	$scope.getStatus = function() {
		$http.get("api/service/status", {}).success(function(data) {
			$scope.result = data;
		});
	};
});

app.controller("UserCtrl", function($scope, $http) {
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
	$scope.updateUsers = function() {
		getUsers();
	}

	_init();
});