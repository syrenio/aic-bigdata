package aic.bigdata.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import twitter4j.User;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

public class GraphDatabase {

	private static GraphDatabase instance;

	public static GraphDatabase getInstance() {

		if (instance == null) {
			ServerConfigBuilder b = new ServerConfigBuilder();
			instance = new GraphDatabase(b.getConfig());
		}
		return instance;
	}

	public static void closeInstance() {
		if (instance != null) {
			instance.close();
			instance = null;
		}
	}

	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private Index<Node> topicIndex;
	private Index<Relationship> retweetsIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;

	final static private String createRetweetsRelationshipQ = "MATCH (a:user),(b:user) WHERE a.id = {aUserId} AND b.id = {bUserId} CREATE (a)-[r:retweets { count: 1 } ]->(b) RETURN r";
	// final static private String getRetweetsCountQ =
	// "MATCH (a:user)-[r:retweets]->(b:user) WHERE a.id = {aUserId} AND b.id = {bUserId} RETURN r.count";
	// final static private String updateRetweetsCountQ =
	// "MATCH (a:user)-[r:retweets]->(b:user) WHERE a.id = {aUserId} AND b.id = {bUserId} SET r.count = r.count + 1 RETURN r.count";

	final static private String getRetweetedQ = "MATCH (a:user)-[r:retweets]->(b:user) WHERE a.id = {userId} return b.id";
	final static private String getRetweetersQ = "MATCH (b:user)-[r:retweets]->(a:user) WHERE a.id = {userId} return b.id";

	final static private String getMentionedTopicsQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.id = {userId} return t.topic";
	final static private String getUsersMentioningQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE t.id = {topic} return u.id";

	final static private String createMentionsRelationshipQ = "MATCH (u:user),(t:topic) WHERE u.id = {userId} AND t.topic = {topic} CREATE (u)-[r:mentions { count: 1 } ]->(t) RETURN r";
	final static private String getMentionsCountQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.id = {userId} AND t.topic = {topic} RETURN r.count";
	final static private String updateMentionsCountQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.id = {userId} AND t.topic = {topic} SET r.count = r.count + 1 RETURN r.count";

	final static private String mostMentionedTopicsQ = "match p = (a:user)-[m:mentions]->(t:topic) where a.id = {id} with p, a, t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc;";// limit {limit}";
	final static private String[] mostMentionedTopicsIndirectQ = {
/*		"match p = (a:user)-[:retweets*0..1]->(f:user)-[m:mentions]->(t:topic) where a.id = {id} with p, a, t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = (a:user)-[:retweets*0..2]->(f:user)-[m:mentions]->(t:topic) where a.id = {id} with p, a, t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = (a:user)-[:retweets*0..3]->(f:user)-[m:mentions]->(t:topic) where a.id = {id} with p, a, t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = (a:user)-[:retweets*0..4]->(f:user)-[m:mentions]->(t:topic) where a.id = {id} with p, a, t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc"
	};
*/
/*
		"match p = (a:user)-[:retweets]->(b:user), (b:user)-[m:mentions]->(t:topic) where a.id = {id} and b.id <> a.id with t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = shortestPath((a:user)-[:retweets*..2]->(b:user)), (b:user)-[m:mentions]->(t:topic) where a.id = {id} and b.id <> a.id with t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = shortestPath((a:user)-[:retweets*..3]->(b:user)), (b:user)-[m:mentions]->(t:topic) where a.id = {id} and b.id <> a.id with t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc",
		"match p = shortestPath((a:user)-[:retweets*..4]->(b:user)), (b:user)-[m:mentions]->(t:topic) where a.id = {id} and b.id <> a.id with t, m.count * (1.0/length(p)) as weightedCount return t.id, sum(weightedCount) order by sum(weightedCount) desc"
*/
		"match p = (a:user)-[:retweets*1..1]->(b:user), (b:user)-[m:mentions]->(t:topic) WHERE a.id = {id} and ALL(n in nodes(p) where 1 = length(filter(m in nodes(p) where m = n))) with t, m.count * (1.0/length(p)) as weightedCount return t.id, weightedCount",
		"match p = (a:user)-[:retweets*1..2]->(b:user), (b:user)-[m:mentions]->(t:topic) WHERE a.id = {id} and ALL(n in nodes(p) where 1 = length(filter(m in nodes(p) where m = n))) with t, m.count * (1.0/length(p)) as weightedCount return t.id, weightedCount",
		"match p = (a:user)-[:retweets*1..3]->(b:user), (b:user)-[m:mentions]->(t:topic) WHERE a.id = {id} and ALL(n in nodes(p) where 1 = length(filter(m in nodes(p) where m = n))) with t, m.count * (1.0/length(p)) as weightedCount return t.id, weightedCount",
		"match p = (a:user)-[:retweets*1..4]->(b:user), (b:user)-[m:mentions]->(t:topic) WHERE a.id = {id} and ALL(n in nodes(p) where 1 = length(filter(m in nodes(p) where m = n))) with t, m.count * (1.0/length(p)) as weightedCount return t.id, weightedCount"
	};
	final static private String mostInfluentalUsersQ = "match (a:user)-[r:retweets]-(b:user) with a, sum(r.count)*{retweetsFactor} + a.followersCount*{followersFactor} + a.favouritesCount*{favouritesFactor} as rank return a.id, a.name, rank order by rank desc limit {limit}";
	//final static private String mostInfluentalUsersQ = "match (a:user)-[r:retweets]-(b:user) with a, sum(r.count) + a.followersCount + a.favouritesCount as rank return a.id, a.name, rank order by rank desc limit 10;";

	final static private String getUserCountByTopicsQ = "match (u:user)-[m:mentions]-(t:topic) return t.id as topic, count(m) as count";

	public GraphDatabase(ServerConfig config) {
		this.config = config;

		try {
			createDb(config.getNeo4jFullDbName());
		} catch (IOException ioe) {
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

		try (Transaction tx = graphDb.beginTx()) {
			retweetsIndex = indexManager.forRelationships("retweets");

			tx.success();
		}

		cypherEngine = new ExecutionEngine(graphDb);
	}

	private void close() {
		this.graphDb.shutdown();
	}

	private void createDb(String name) throws IOException {
		System.out.println("Creating Neo4J database at '" + name + "'");
		// graphDb = new
		// GraphDatabaseFactory().newEmbeddedDatabaseBuilder(fullName).newGraphDatabase();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(name);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public Set<String> getMentionedTopics(User user) {
		return getMentionedTopics(user.getId());
	}

	public Set<String> getMentionedTopics(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		ExecutionResult result;

		HashSet<String> topics = new HashSet<String>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getMentionedTopicsQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				topics.add((String) map.get("t.topic"));
			}
		}

		return topics;
	}

	public Map<String, Long> getUserCountByTopics() {
		Map<String, Object> params = new HashMap<String, Object>();

		ExecutionResult result;

		Map<String, Long> topics = new HashMap<String, Long>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getUserCountByTopicsQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			//System.out.println(result.dumpToString());
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				topics.put((String) map.get("topic"), (Long) map.get("count"));
			}
		}

		return topics;
	}

	public Set<Long> getUsersMentioning(String topic) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topic", topic);

		ExecutionResult result;

		HashSet<Long> ids = new HashSet<Long>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getUsersMentioningQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				ids.add((Long) map.get("u.id"));
			}
		}

		return ids;
	}

	public void addTopic(String topic) {
		if (!nodeForTopicExists(topic)) {
			try (Transaction tx = graphDb.beginTx()) {
				Node topicNode = graphDb.createNode();
				topicNode.setProperty("topic", topic);
				topicNode.addLabel(DynamicLabel.label("topic"));

				topicIndex.add(topicNode, "topic", topicNode.getProperty("topic"));

				tx.success();
			}
		} else {
			// System.out.println("TweetToNeo4JHandler: Topic " + topic +
			// " already exists (it is okay to see this message once in a while)");
		}
	}

	private boolean nodeForTopicExists(String topic) {
		boolean exists = false;

		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> hits = topicIndex.get("topic", topic);

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

	private Relationship getRetweetsRelationship(long retweeterId, long originalId) {
		Relationship found = null;

		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Relationship> hits = retweetsIndex.get("retweeterId", retweeterId);

			for (Relationship r : hits) {
				Node endNode = r.getEndNode();
				Long endId = (Long) endNode.getProperty("userId");
				if (endId.equals(originalId)) {
					found = r;
				}
			}
			tx.success();
		}

		return found;
	}

	private boolean nodeForUserExists(User user) {
		return this.nodeForUserExists(user.getId());
	}

	public void addMentionsRelationship(long userId, String topic) {

		if (!nodeForUserExists(userId) && !nodeForTopicExists(topic)) {
			System.err.println("TweetToNeo4JHandler: Cannot create relationship (user " + userId
					+ ")-[mentions]->(topic " + topic
					+ ") because either the user or the topic does not exist in the graph");
			return;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("topic", topic);

		ExecutionResult result;

		// why do we need a Transaction object we then ignore? ask the neo4j
		// docs, good luck!
		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getMentionsCountQ, params);
		}

		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Map<String, Object>> iterator = result.iterator();

			if (iterator.hasNext()) { // TODO SLOW
				Map<String, Object> map = iterator.next(); // TODO SLOW

				result = cypherEngine.execute(updateMentionsCountQ, params); // TODO
																				// SLOW
				if (iterator.hasNext()) {
					System.err.println("TweetToNeo4JHandler: Warning: More than one relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();
				iterator = result.iterator();
				if (iterator.hasNext()) {
					Map<String, Object> updatedMap = iterator.next();
					System.out.println("TweetToNeo4JHandler: Updating relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ") from count = " + map.get("r.count") + " to count = "
							+ updatedMap.get("r.count"));
				} else {
					System.err.println("TweetToNeo4JHandler: Could not update relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ") in Neo4J DB");
				}
				iterator.close();
			} else {
				result = cypherEngine.execute(createMentionsRelationshipQ, params);
				int numCreated = result.getQueryStatistics().getRelationshipsCreated();
				if (numCreated == 1) {
					System.out.println("TweetToNeo4JHandler: Created relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ")");
				} else if (numCreated > 1) {
					System.err.println("TweetToNeo4JHandler: Created morer than one relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ")!");
				} else {
					System.out.println("TweetToNeo4JHandler: Failed to create relationship (user " + userId
							+ ")-[mentions]->(topic " + topic + ")");
				}
			}

			System.out.println("TweetToNeo4JHandler: Relationship between topic " + topic + " and user " + userId
					+ " added to Neo4J.");
			tx.success();
		}
	}

	public void addUser(User user) {
		if (!nodeForUserExists(user)) {
			try (Transaction tx = graphDb.beginTx()) {
				Node userNode = graphDb.createNode();

				userNode.setProperty("userId", user.getId());
				userNode.setProperty("userName", user.getName());
				userNode.setProperty("friendsCount", user.getFriendsCount());
				userNode.setProperty("followersCount", user.getFollowersCount());
				userNode.addLabel(DynamicLabel.label("user"));

				// System.out.println("TweetToNeo4JHandler: Adding user \"" +
				// user.getName() + "\" (" + user.getId() + ")");

				userIndex.add(userNode, "userId", userNode.getProperty("userId"));

				tx.success();
			}
		} else {
			// turns out this happens a lot. but it is not an error, no need to
			// print this
			// System.out.println("TweetToNeo4JHandler: User " + user.getId() +
			// " already exists (it is okay to see this message once in a while)");
		}
	}

	public void addRetweetsRelationship(User retweeter, User original) {
		Relationship relationship = getRetweetsRelationship(retweeter.getId(), original.getId());
		if (relationship == null) {
			createRetweetsRelationship(retweeter, original);
		} else {
			// System.out.println("TweetToNeo4JHandler: Updating relationship (user "
			// + retweeter.getId() + ")-[retweets]->(user " + original.getId() +
			// ") in Neo4J DB");
			try (Transaction tx = graphDb.beginTx()) {
				Long count = (Long) relationship.getProperty("count");
				// System.out.println("TweetToNeo4JHandler: Count was " +
				// count);
				relationship.setProperty("count", count + 1);

				tx.success();
			}
		}
	}

	private void createRetweetsRelationship(User retweeter, User original) {
		try (Transaction tx = graphDb.beginTx()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("aUserId", retweeter.getId());
			params.put("bUserId", original.getId());

			ExecutionResult result = cypherEngine.execute(createRetweetsRelationshipQ, params);

			ResourceIterator<Map<String, Object>> resourceIterator = result.iterator();
			if (!resourceIterator.hasNext()) {
				System.err.println("TweetToNeo4JHandler: Failed to create relationship (user " + retweeter.getId()
						+ ")-[retweets]->(user " + original.getId() + ") in Neo4J DB");
			} else {
				Map<String, Object> map = resourceIterator.next();
				Relationship relationship = (Relationship) map.get("r");
				retweetsIndex.add(relationship, "retweeterId", retweeter.getId());
			}

			/*
			 * int numCreated =
			 * result.getQueryStatistics().getRelationshipsCreated(); if
			 * (numCreated == 1) {
			 * System.out.println("TweetToNeo4JHandler: Created relationship (user "
			 * + retweeter.getId() + ")-[retweets]->(user " + original.getId() +
			 * ") in Neo4J DB"); } else if (numCreated > 1) {
			 * System.err.println(
			 * "TweetToNeo4JHandler: Created morer than one relationship (user "
			 * + retweeter.getId() + ")-[retweets]->(user " + original.getId() +
			 * ") in Neo4J DB"); } else { System.err.println(
			 * "TweetToNeo4JHandler: Failed to create relationship (user " +
			 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
			 * ") in Neo4J DB"); }
			 */

			tx.success();
		}
	}

	/*
	 * public void addRetweetsRelationshipOLD(User retweeter, User original) {
	 * //System.out.println("TweetToNeo4JHandler: User \"" + retweeter.getName()
	 * + "\" retweeted User \"" + original.getName() + "\"");
	 * 
	 * if (!nodeForUserExists(retweeter) && !nodeForUserExists(original)) {
	 * System
	 * .err.println("TweetToNeo4JHandler: Cannot create relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") because one of the users does not exist in the graph"); return; }
	 * 
	 * Map<String, Object> params = new HashMap<String, Object>();
	 * params.put("aUserId", retweeter.getId()); params.put("bUserId",
	 * original.getId());
	 * 
	 * ExecutionResult result;
	 * 
	 * // why do we need a Transaction object we then ignore? ask the neo4j
	 * docs, good luck! try (Transaction ignoreMe = graphDb.beginTx()) { result
	 * = cypherEngine.execute(getRetweetsCountQ, params); //TODO SLOW }
	 * 
	 * try (Transaction tx = graphDb.beginTx()) { ResourceIterator<Map<String,
	 * Object>> iterator = result.iterator();
	 * 
	 * if (iterator.hasNext()) { //TODO SLOW Map<String, Object> map =
	 * iterator.next(); //TODO SLOW //params.put("currentCount",
	 * map.get("r.count")); result = cypherEngine.execute(updateRetweetsCountQ,
	 * params); //TODO SLOW if (iterator.hasNext()) { System.err.println(
	 * "TweetToNeo4JHandler: Warning: More than one relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } iterator.close(); iterator = result.iterator(); if
	 * (iterator.hasNext()) { Map<String, Object> updatedMap = iterator.next();
	 * System.out.println("TweetToNeo4JHandler: Updating relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") from count = " + map.get("r.count") + " to count = " +
	 * updatedMap.get("r.count")); } else { System.err.println(
	 * "TweetToNeo4JHandler: Could not update relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } iterator.close(); } else {
	 * //System.out.println("TweetToNeo4JHandler: Creating relationship (user "
	 * + retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); result =
	 * cypherEngine.execute(createRetweetsRelationshipQ, params);
	 * 
	 * ResourceIterator<Map<String, Object>> resourceIterator =
	 * result.iterator(); if (!resourceIterator.hasNext()) { System.err.println(
	 * "TweetToNeo4JHandler: Failed to create relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } else { Map<String, Object> map =
	 * resourceIterator.next(); Relationship relationship = (Relationship)
	 * map.get("r"); retweetsIndex.add(relationship, "userId",
	 * retweeter.getId()); }
	 * 
	 * int numCreated = result.getQueryStatistics().getRelationshipsCreated();
	 * if (numCreated == 1) {
	 * System.out.println("TweetToNeo4JHandler: Created relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } else if (numCreated > 1) { System.err.println(
	 * "TweetToNeo4JHandler: Created morer than one relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } else { System.out.println(
	 * "TweetToNeo4JHandler: Failed to create relationship (user " +
	 * retweeter.getId() + ")-[retweets]->(user " + original.getId() +
	 * ") in Neo4J DB"); } }
	 * 
	 * tx.success(); } }
	 */

	public int getCount() {
		return this.tweetsLogged;
	}

	public Set<Long> getRetweeted(User user) {
		return getRetweeted(user.getId());
	}

	public Set<Long> getRetweeted(long userId) {
		return getIdsForQuery(userId, getRetweetedQ);
	}

	public Set<Long> getRetweeters(User user) {
		return getRetweeters(user.getId());
	}

	public Set<Long> getRetweeters(long userId) {
		return getIdsForQuery(userId, getRetweetersQ);
	}

	private Set<Long> getIdsForQuery(long userId, String query) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		ExecutionResult result;

		HashSet<Long> ids = new HashSet<Long>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(query, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				ids.add((Long) map.get("b.id"));
			}
		}

		return ids;
	}


	public List<String> getMostMentionedTopics(long userId) {
		return getMostMentionedTopics(userId, false);
	}

	public List<String> getMostMentionedTopics(long userId, boolean potIntr) {
		return getMostMentionedTopics(userId, potIntr, 4);
	}

	public List<String> getMostMentionedTopics(long userId, boolean potIntr, int indirectness) {
		String cypherQ = mostMentionedTopicsQ;
		if (potIntr) {
			if (indirectness < 1)
				indirectness = 1;
			if (indirectness > 4)
				indirectness = 4;
			cypherQ = mostMentionedTopicsIndirectQ[indirectness-1];
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", userId);
		//params.put("indirectness", 4);
		//params.put("limit", 10);

		ExecutionResult result;

		List<String> topics = new ArrayList<String>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(cypherQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				topics.add((String) map.get("t.id"));
			}
		}

		return topics;
	}

	public List<Long> getMostInfluentalUserIDs() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("retweetsFactor", new Double(1.0));
		params.put("followersFactor", new Double(1.0));
		params.put("favouritesFactor", new Double(1.0));
		params.put("limit", 10);

		ExecutionResult result;

		List<Long> ids = new ArrayList<Long>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(mostInfluentalUsersQ, params);
			//System.out.println(result.dumpToString());

			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				ids.add((Long) map.get("a.id"));
			}
		}

		return ids;
	}
}
