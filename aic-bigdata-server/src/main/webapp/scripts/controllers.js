/**
 * 
 */
var app = angular.module("bigdataApp");

app.controller("ServiceCtrl", function($scope, $http) {
	$scope.status = {
		stream : false,
		extraction : false,
		analyse : false,
		active: false
	};

	function exCommand(cmd){
		$http.get("api/service?command="+cmd).success(function(data) {
			console.log(data);
			$scope.status = data;
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
			$scope.status = data;
		});
	};
});


app.controller("CampaignsCtrl", function($scope,AdsService){
	$scope.ads = [];
	AdsService.getAds().then(function(data){
		$scope.ads = data;
	});
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

app.controller("UsersCtrl", function($scope, $http, UserService, ngTableParams) {
	$scope.pageSize = 100;
	$scope.pageNumber = 0;
	var users = [{
		id: 1,
		name: "test1"
	},{
		id: 2,
		name: "test2"
	}];

	function _init() {
		getUsers($scope.pageSize,$scope.pageNumber);
	}

	function getUsers(size,page) {
		var url = "api/users?size=" + size + "&page="
				+ page;
		return $http.get(url).success(function(data) {
			users = data;
			$scope.users = users;
		});
	}

	$scope.tableParams = new ngTableParams({
		page: 1,            // show first page
		count: 10           // count per page
	}, {
		total: 0, // length of data
		getData: function($defer, params) {
			getUsers(params.count(),params.page()).then(function(data){
				params.total(users.totalSize);
				$defer.resolve(users.result);
			});
		}
	});

	$scope.selectUser = function(user) {
		UserService.getConnections(user.id).then(function(data) {
			console.log(data);
		});
	};

	_init();
});