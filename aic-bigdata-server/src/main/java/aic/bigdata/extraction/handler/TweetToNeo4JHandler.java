package aic.bigdata.extraction.handler;

import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.server.ServerConfig;

import twitter4j.Status;
import twitter4j.User;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

//import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Transaction;

public class TweetToNeo4JHandler implements TweetHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;

/*
	private static enum RelTypes implements RelationshipType {
		FOLLOWS,
		RETWEETS
	}
*/

	public TweetToNeo4JHandler(ServerConfig config) {
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
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		User user = status.getUser();

		addUser(user);

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
				System.out.println("TweetToNeo4JHandler: more than one user with userId " + user.getId() + " in Neo4J DB");
			}
			// if hits.size() was 1 we already had a node for that user

			tx.success();
		}
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
