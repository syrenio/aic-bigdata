package aic.bigdata.extraction;

import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.UserToMongoDBHandler;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.extraction.provider.MongoDbTweetProvider;
import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TwitterStreamJob;

public class Neo4JExtractionRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}
/*
	private static TweetProvider CreateTweetProviderForTwitterExtraction() {
		TwitterStreamJob j = new TwitterStreamJob(config);
		return j;
	}
*/
	private static TweetProvider CreateMongoDbTweetProvider() {
		MongoDatabase b = new MongoDatabase(config);
		MongoDbTweetProvider provider = new MongoDbTweetProvider(b);
		return provider;
	}
/*
	private static TweetHandler CreateTweetToMongoDBHandler() {
		MongoDatabase b = new MongoDatabase(config);
		TweetHandler handler = new TweetToMongoDBHandler(b);
		return handler;
	}

	private static TweetHandler CreateUserToMongoDBHandler() {
		MongoDatabase b = new MongoDatabase(config);
		TweetHandler handler = new UserToMongoDBHandler(b);
		return handler;
	}
*/
	private static TweetHandler CreateTweetToNeo4JHandler() {
	        TweetHandler handler = new TweetToNeo4JHandler(config);
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
