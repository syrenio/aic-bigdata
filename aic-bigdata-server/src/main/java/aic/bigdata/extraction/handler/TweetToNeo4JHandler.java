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

public class TweetToNeo4JHandler implements TweetHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;

/*
	private static enum RelTypes implements RelationshipType {
		FOLLOWS,
		RETWEETS
	}
*/

	// cypher query strings
	final static private String createRetweetsRelationshipQ = "MATCH (a:user),(b:user) WHERE a.userId = {aUserId} AND b.userId = {bUserId} CREATE (a)-[r:retweets { count: 1 } ]->(b) RETURN r";
	final static private String getRetweetsCountQ = "MATCH (a)-[r:retweets]->(b) WHERE a.userId = {aUserId} AND b.userId = {bUserId} RETURN r.count";
	final static private String updateRetweetsCountQ = "MATCH (a)-[r:retweets]->(b) WHERE a.userId = {aUserId} AND b.userId = {bUserId} SET r.count = r.count + {currentCount}";

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
			HandleStatusTweet(retweeted, retweeted.getSource());
		}

		tweetsLogged++;
	}

	private void addUser(User user) {
		try (Transaction tx = graphDb.beginTx()) {
			IndexHits<Node> hits = userIndex.get("userId", user.getId());

			if (hits.size() == 0) {
				Node userNode = graphDb.createNode();
				userNode.setProperty("userId", user.getId());
				userNode.setProperty("userName", user.getName());
				userNode.addLabel(DynamicLabel.label("user"));

				//System.out.println("TweetToNeo4JHandler: Adding user \"" + user.getName() + "\" (" + user.getId() + ")");

				userIndex.add(userNode, "userId", userNode.getProperty("userId"));
			}
			else if (hits.size() > 1) {
				System.err.println("TweetToNeo4JHandler: more than one user with userId " + user.getId() + " in Neo4J DB");
			}
			// if hits.size() was 1 we already had a node for that user

			tx.success();
		}
	}

	private void addRetweetsRelationship(User retweeter, User original) {
		//System.out.println("TweetToNeo4JHandler: User \"" + retweeter.getName() + "\" retweeted User \"" + original.getName() + "\"");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aUserId", retweeter.getId());
		params.put("bUserId", original.getId());

		ExecutionResult result = cypherEngine.execute(getRetweetsCountQ, params);
		List<String> cols = result.columns();
		if (cols.size() == 1) {
			System.out.println("TweetToNeo4JHandler: Creating relationship \"retweets\" user " + retweeter.getId() + " -> user " + original.getId());
			result = cypherEngine.execute(createRetweetsRelationshipQ, params);
		}
		else if (cols.size() > 2) {
			System.err.println("TweetToNeo4JHandler: More than one relationship \"retweets\" user " + retweeter.getId() + " -> user " + original.getId() + " in Neo4J DB");
		}
		else { // retweets relationship exists, needs to be updated
			System.out.println("TweetToNeo4JHandler: Updating relationship \"retweets\" user " + retweeter.getId() + " -> user " + original.getId());
			//System.out.println(cols.get(1));
			params.put("currentCount", cols.get(1));
			result = cypherEngine.execute(updateRetweetsCountQ, params);
		}

		//System.out.println("TweetToNeo4JHandler: result: \"" + result.dumpToString() + "\"");
	}

/*
	private void addFriends(User user) {
		try {
			IDs ids = config.getTwitter4JInstance().getFriendsIDs(user.getId(), -1);
			addFriendsFromIDs(user, ids.getIDs());
			while (ids.hasNext()) {
				ids = config.getTwitter4JInstance().getFriendsIDs(user.getId(), ids.getNextCursor());
				addFriendsFromIDs(user, ids.getIDs());
			}
		}
		catch (TwitterException te) {
			te.printStackTrace();
		}
	}
*/

	private void addFriendsFromIDs(User user, long[] ids) {
		System.out.println("TweetToNeo4JHandler: Adding " + ids.length + " friends for user \"" + user.getName() + "\"");
	}

	@Override
	public void HandleTweet(String tweet) {
		System.out.println("TweetToNeo4JHandler: Warning: Somebody called HandleTweet on me, but I won't do anything with the tweet you gave me.");
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
