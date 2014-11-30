package aic.bigdata.extraction.handler;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.server.ServerConfig;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.IDs;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;




//import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.schema.IndexDefinition;
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
	private GraphDatabase graph;

	public TweetToNeo4JHandler(ServerConfig config, GraphDatabase graph) {
		this.config = config;
		this.graph = graph;
	}
	
	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		//System.out.println("TweetToNeo4JHandler: got tweet");
		User user = status.getUser();
		graph.addUser(user);
		//addFriends(user);
		if (status.isRetweet()) {
			Status retweeted = status.getRetweetedStatus();
			graph.addUser(retweeted.getUser());
			graph.addRetweetsRelationship(user, retweeted.getUser()); //TODO SLOW
			//searchTweetForTopics(status); // ?
			HandleStatusTweet(retweeted, retweeted.getSource());
		}

		tweetsLogged++;
	}


	@Override
	public void HandleTweet(String tweet) {
		System.out.println("TweetToNeo4JHandler: Warning: Somebody called HandleTweet on me, but I won't do anything with the tweet you gave me");
	}

}
