package aic.bigdata.extraction;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.extraction.provider.MongoDbTweetProvider;
import aic.bigdata.server.ServerConfig;

public class Neo4JExtractionRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}

	private static TweetProvider CreateMongoDbTweetProvider() {
		MongoDatabase b = new MongoDatabase(config);
		MongoDbTweetProvider provider = new MongoDbTweetProvider(b);
		return provider;
	}

	private static TweetHandler CreateTweetToNeo4JHandler() {
		TweetHandler handler = new TweetToNeo4JHandler(config, GraphDatabase.getInstance());
		return handler;
	}

	public static void main(String[] args) {
		TweetProvider p = CreateMongoDbTweetProvider();

		TweetHandler neo4jHandler = CreateTweetToNeo4JHandler();
		p.addTweetHandler(neo4jHandler);

		p.run();
		// System.out.print("Logged Tweets: "+ handler.getCount());
	}

}
