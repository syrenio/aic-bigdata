/**
 * 
 */
var app = angular.module("bigdataApp");

app.controller("HeadCtrl", function($scope, $location) {
	$scope.isActive = function(viewLocation) {
		return viewLocation === $location.path();
	};
});

app.controller("ServiceCtrl", function($scope, $http) {
	$scope.status = {
		stream : false,
		extraction : false,
		analyse : false,
		active : false
	};
	
	$scope.test = function(u){
		console.log("new-test",u);
	};
	
	function _init() {
		$scope.getStatus();
	}

	function exCommand(cmd) {
		$http.get("api/service?command=" + cmd).success(function(data) {
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

	_init();
});

app.controller("CampaignsCtrl", function($scope, AdsService) {
	$scope.ads = [];
	AdsService.getAds().then(function(data) {
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

					if (sig != null) {
						sig.kill();
					}
					sig = new sigma({
						graph : data,
						container : "graphContainer",
						settings : {
							defaultNodeColor : '#ec5148'
						}
					});
				});
	}
});

app.controller("UsersCtrl",
		function($scope, $http, $filter, $timeout, UserService, ngTableParams) {
			var onSelectFunctions = [];
	
			$scope.pageSize = 10;
			$scope.pageNumber = 0;
			var users = [ {
				id : 1,
				name : "test1"
			}, {
				id : 2,
				name : "test2"
			} ];

			function _init() {
				getUsers($scope.pageSize, $scope.pageNumber);
			}

			function getUsers(size, page, filterName) {
				var p = {
					params : {
						size : size,
						page : page,
						fname : filterName || ""
					}
				};
				return $http.get("api/users",p).success(function(data) {
					users = data;
					$scope.users = users;
				});
			}
			
			this.addSelectFunction = function(f){
				onSelectFunctions.push(f);
			};

			$scope.tableParams = new ngTableParams({
				page : 1, // show first page
				count : 10,
				filter :  { name : ''}
			// count per page
			}, {
				total : 0, // length of data
				getData : function($defer, params) {
					getUsers(params.count(), params.page(), params.filter().name).then(
						function(data) {
							$timeout(function() {
								params.total(users.totalSize);
								$defer.resolve(users.result);
							},500);
						});
				}
			});

			$scope.selectUser = function(user) {
				console.log("selected user:",user);

				/*
				UserService.getConnections(user.id).then(function(data) {
					console.log(data);
				});
				*/
				
				for ( var idx in onSelectFunctions) {
					var fx = onSelectFunctions[idx];
					fx(user);
				}
			};

			_init();
		});

app.controller("QueryCtrl", function($scope, $http, QueryService, AdsService) {
	var self = this;
	var selectedTopics = [];

	self.mostInflPersons = [];
	self.topics = [];
	self.resultSizes = [10,20,30,40,50,100];
	self.friends = [{
		text : "Friends",
		value: 1
	},{
		text : "Friends of Friends",
		value: 2
	},{
		text : "I know that guy",
		value: 3
	},{
		text : "Facebook friend",
		value: 4
	}];
	
	self.sugResultSize = 10;
	self.sugPotResultSize = 10;
	self.selectedFriendOption = 1;
	

	self.selectTopics = function(x) {
		var idx = selectedTopics.indexOf(x);
		if (idx === -1) {
			selectedTopics.push(x);
		} else {
			selectedTopics.splice(idx, 1);
		}
		console.log(selectedTopics);
	};

	self.queryMostInflPersons = function() {
		QueryService.getMostInflPerson().then(function(data) {
			self.mostInflPersons = data;
		});
	};

	self.queryPersonsWithTopics = function() {
		QueryService.getPersonsWithTopics().then(function(data) {
			self.personsWithTopics = data;
		});
	};
	
	self.querySuggestAds = function(userId){
		QueryService.getSuggestAds(userId,false,0).then(function(data){
			self.suggAds = data;
		});
	};
	self.querySuggestAdsPotInt = function(userId,jumps){
		QueryService.getSuggestAds(userId,true,jumps).then(function(data){
			self.suggAdsPot = data;
		});
	};

	function _init() {
		AdsService.getTopics().then(function(data) {
			console.info("_init topics:", data);
			self.topics = data;
		});
	}

	_init();
});