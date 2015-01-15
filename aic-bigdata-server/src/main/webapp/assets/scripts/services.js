/**
 * 
 */
var app = angular.module("bigdataApp");

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

app.factory("AdsService", function($http) {
	return {
		getAds : function() {
			return $http.get("api/ads").then(function(resp) {
				console.log(resp.data);
				return resp.data;
			});
		},
		getTopics : function() {
			return $http.get("api/ads/topics").then(function(resp) {
				console.log("getTopics:", resp.data);
				return resp.data;
			});
		}
	};
});

app.factory("QueryService", function($http) {
	return {
		getMostInflPerson : function() {
			return $http.get("api/queries/inflUser").then(function(resp) {
				console.log(resp.data);
				return resp.data;
			});
		},
		getPersonsWithTopics : function(topics){
			console.log("getPersonWithTopics",topics);
			return $http.get("api/queries/usersWithInterests",{params : {
				topics : topics
			}}).then(function(resp) {
				console.log(resp.data);
				return resp.data;
			});
		},
		getSuggestAds : function(userId,potInt){
			console.log("getSuggestAds",userId,potInt);
			return $http.get("api/queries/suggestAdsForUser",{params : {
				userId : userId,
				potentialInterests : potInt
			}}).then(function(resp) {
				console.log(resp.data);
				return resp.data;
			});
		}
	};
});