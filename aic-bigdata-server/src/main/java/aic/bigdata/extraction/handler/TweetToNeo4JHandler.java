package aic.bigdata.extraction.handler;

import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.server.ServerConfig;

import twitter4j.Status;
import twitter4j.User;
import twitter4j.IDs;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

//import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Transaction;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.ResourceIterator;

public class TweetToNeo4JHandler implements TweetHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private Index<Node> topicIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;

	// cypher query strings
	final static private String createRetweetsRelationshipQ = "MATCH (a:user),(b:user) WHERE a.userId = {aUserId} AND b.userId = {bUserId} CREATE (a)-[r:retweets { count: 1 } ]->(b) RETURN r";
	final static private String getRetweetsCountQ = "MATCH (a:user)-[r:retweets]->(b:user) WHERE a.userId = {aUserId} AND b.userId = {bUserId} RETURN r.count";
	final static private String updateRetweetsCountQ = "MATCH (a:user)-[r:retweets]->(b:user) WHERE a.userId = {aUserId} AND b.userId = {bUserId} SET r.count = r.count + 1 RETURN r.count";

	final static private String createMentionsRelationshipQ = "MATCH (u:user),(t:topic) WHERE u.userId = {userId} AND t.topic = {topic} CREATE (u)-[r:mentions { count: 1 } ]->(t) RETURN r";
	final static private String getMentionsCountQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.userId = {userId} AND t.topic = {topic} RETURN r.count";
	final static private String updateMentionsCountQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.userId = {userId} AND t.topic = {topic} SET r.count = r.count + 1 RETURN r.count";

	public TweetToNeo4JHandler(ServerConfig config) {
		this.config = config;

		try {
			createDb(config.getNeo4JDbName());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		IndexManager indexManager = graphDb.index();

		try (Transaction tx = graphDb.beginTx()) {
			userIndex = indexManager.forNodes("users");

			tx.success();
		}

		try (Transaction tx = graphDb.beginTx()) {
			topicIndex = indexManager.forNodes("topics");

			tx.success();
		}

		cypherEngine = new ExecutionEngine(graphDb);
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		//System.out.println("TweetToNeo4JHandler: got tweet");
		User user = status.getUser();
		addUser(user);
		//addFriends(user);
		if (status.isRetweet()) {
			Status retweeted = status.getRetweetedStatus();
			addUser(retweeted.getUser());
			addRetweetsRelationship(user, retweeted.getUser());
			//searchTweetForTopics(status); // ?
			HandleStatusTweet(retweeted, retweeted.getSource());
		}

		tweetsLogged++;
	}

/*
	private void searchTweetForTopics(Status status) {
		for topic in topics
			if topic in tweet
				addMentionsRelationship(status.getUser(), topic);
	}
*/

	private void addUser(User user) {
		if (!nodeForUserExists(user)) {
			try (Transaction tx = graphDb.beginTx()) {
				Node userNode = graphDb.createNode();

				userNode.setProperty("userId", user.getId());
				userNode.setProperty("userName", user.getName());
				userNode.setProperty("friendsCount", user.getFriendsCount());
				userNode.setProperty("followersCount", user.getFollowersCount());
				userNode.addLabel(DynamicLabel.label("user"));

				//System.out.println("TweetToNeo4JHandler: Adding user \"" + user.getName() + "\" (" + user.getId() + ")");

				userIndex.add(userNode, "userId", userNode.getProperty("userId"));

				tx.success();
			}
		}
		else {
			// turns out this happens a lot. but it is not an error, no need to print this
			//System.out.println("TweetToNeo4JHandler: User " + user.getId() + " already exists (it is okay to see this message once in a while)");
		}
	}

	private void addRetweetsRelationship(User retweeter, User original) {
		//System.out.println("TweetToNeo4JHandler: User \"" + retweeter.getName() + "\" retweeted User \"" + original.getName() + "\"");

		if (!nodeForUserExists(retweeter) && !nodeForUserExists(original)) {
			System.err.println("TweetToNeo4JHandler: Cannot create relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") because one of the users does not exist in the graph");
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aUserId", retweeter.getId());
		params.put("bUserId", original.getId());

		ExecutionResult result;

		// why do we need a Transaction object we then ignore? ask the neo4j docs, good luck!
		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getRetweetsCountQ, params);
		}

		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Map<String, Object>> iterator = result.iterator();

			if (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				//params.put("currentCount", map.get("r.count"));
				result = cypherEngine.execute(updateRetweetsCountQ, params);
				if (iterator.hasNext()) {
					System.err.println("TweetToNeo4JHandler: Warning: More than one relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				}
				iterator.close();	
				iterator = result.iterator();
				if (iterator.hasNext()) {
					Map<String, Object> updatedMap = iterator.next();
					System.out.println("TweetToNeo4JHandler: Updating relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") from count = " + map.get("r.count") + " to count = " + updatedMap.get("r.count"));
				}
				else {
					System.err.println("TweetToNeo4JHandler: Could not update relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				}
				iterator.close();
			}
			else {
				//System.out.println("TweetToNeo4JHandler: Creating relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				result = cypherEngine.execute(createRetweetsRelationshipQ, params);
				int numCreated = result.getQueryStatistics().getRelationshipsCreated();
				if (numCreated == 1) {
					System.out.println("TweetToNeo4JHandler: Created relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				}
				else if (numCreated > 1) {
					System.err.println("TweetToNeo4JHandler: Created morer than one relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				}
				else {
					System.out.println("TweetToNeo4JHandler: Failed to create relationship (user " + retweeter.getId() + ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
				}
			}

			tx.success();
		}
	}

	private boolean nodeForTopicExists(String topic) {
		boolean exists = false;

		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> hits = topicIndex.get("topic", topic.toLowerCase());

			exists = hits.size() == 1;
			tx.success();
		}

		return exists;
	}

	private boolean nodeForUserExists(long userId) {
		boolean exists = false;

		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> hits = userIndex.get("userId", userId);

			exists = hits.size() == 1;
			tx.success();
		}

		return exists;
	}
	
	private boolean nodeForUserExists(User user) {
		return this.nodeForUserExists(user.getId());
	}

	public void addTopic(String topic) {
		if (!nodeForTopicExists(topic)) {
			try (Transaction tx = graphDb.beginTx()) {
				Node topicNode = graphDb.createNode();
				topicNode.setProperty("topic", topic.toLowerCase());
				topicNode.addLabel(DynamicLabel.label("topic"));

				topicIndex.add(topicNode, "topic", topicNode.getProperty("topic"));

				tx.success();
			}
		}
		else {
			//System.out.println("TweetToNeo4JHandler: Topic " + topic + " already exists (it is okay to see this message once in a while)");
		}
	}

	public void addMentionsRelationship(long userId, String topic) {
		if (!nodeForUserExists(userId) && !nodeForTopicExists(topic)) {
			System.err.println("TweetToNeo4JHandler: Cannot create relationship (user " +userId + ")-[mentions]->(topic " + topic + ") because either the user or the topic does not exist in the graph");
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("topic", topic.toLowerCase());

		ExecutionResult result;

		// why do we need a Transaction object we then ignore? ask the neo4j docs, good luck!
		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getMentionsCountQ, params);
		}

		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Map<String, Object>> iterator = result.iterator();

			if (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();

				result = cypherEngine.execute(updateMentionsCountQ, params);
				if (iterator.hasNext()) {
					System.err.println("TweetToNeo4JHandler: Warning: More than one relationship (user " + userId + ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();	
				iterator = result.iterator();
				if (iterator.hasNext()) {
					Map<String, Object> updatedMap = iterator.next();
					System.out.println("TweetToNeo4JHandler: Updating relationship (user " + userId + ")-[mentions]->(topic " + topic + ") from count = " + map.get("r.count") + " to count = " + updatedMap.get("r.count"));
				}
				else {
					System.err.println("TweetToNeo4JHandler: Could not update relationship (user " + userId + ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();
			}
			else {
				result = cypherEngine.execute(createMentionsRelationshipQ, params);
				int numCreated = result.getQueryStatistics().getRelationshipsCreated();
				if (numCreated == 1) {
					System.out.println("TweetToNeo4JHandler: Created relationship (user " + userId + ")-[mentions]->(topic " + topic + ")");
				}
				else if (numCreated > 1) {
					System.err.println("TweetToNeo4JHandler: Created morer than one relationship (user " + userId + ")-[mentions]->(topic " + topic + ")!");
				}
				else {
					System.out.println("TweetToNeo4JHandler: Failed to create relationship (user " + userId + ")-[mentions]->(topic " + topic + ")");
				}
			}

			System.out.println("TweetToNeo4JHandler: Relationship between topic " + topic + " and user " + userId + " added to Neo4J.");
			tx.success();
		}
	}
	
	/*public void addMentionsRelationship(User user, String topic) {
		if (!nodeForUserExists(user) && !nodeForTopicExists(topic)) {
			System.err.println("TweetToNeo4JHandler: Cannot create relationship (user " + user.getId() + ")-[mentions]->(topic " + topic + ") because either the user or the topic does not exist in the graph");
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", user.getId());
		params.put("topic", topic);

		ExecutionResult result;

		// why do we need a Transaction object we then ignore? ask the neo4j docs, good luck!
		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getMentionsCountQ, params);
		}

		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Map<String, Object>> iterator = result.iterator();

			if (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();

				result = cypherEngine.execute(updateMentionsCountQ, params);
				if (iterator.hasNext()) {
					System.err.println("TweetToNeo4JHandler: Warning: More than one relationship (user " + user.getId() + ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();	
				iterator = result.iterator();
				if (iterator.hasNext()) {
					Map<String, Object> updatedMap = iterator.next();
				}
				else {
					System.err.println("TweetToNeo4JHandler: Could not update relationship (user " + user.getId() + ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();
			}
			else {
				result = cypherEngine.execute(createMentionsRelationshipQ, params);
			}

			tx.success();
		}
	}*/


	@Override
	public void HandleTweet(String tweet) {
		System.out.println("TweetToNeo4JHandler: Warning: Somebody called HandleTweet on me, but I won't do anything with the tweet you gave me");
	}

	public int getCount() {
		return this.tweetsLogged;
	}

	private void createDb(String name) throws IOException {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(name);

		Runtime.getRuntime().addShutdownHook(new Thread () {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
