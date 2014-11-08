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
import org.neo4j.cypher.javacompat.ExecutionEngine;

public class TweetToNeo4JHandler implements TweetHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private ExecutionEngine cypherEngine;

	// cypher queries
	private final static String createUserQ = "CREATE ({props})";

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
		cypherEngine = new ExecutionEngine(graphDb);
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		//System.out.println("TweetToNeo4JHandler: tweets so far: " + getCount());

		// create user
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> props = new HashMap<String, Object>();

		User user = status.getUser();
		props.put("userId", user.getId());
		props.put("userName", user.getName());
		params.put("props", props);

		System.out.println("TweetToNeo4JHandler: Adding user \"" + user.getName() + "\" (" + user.getId() + ")");
		cypherEngine.execute(createUserQ, params);

		tweetsLogged++;
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
