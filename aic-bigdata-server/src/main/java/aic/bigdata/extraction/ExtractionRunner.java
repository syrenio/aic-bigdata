package aic.bigdata.extraction;

import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TwitterStreamJob;

public class ExtractionRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}

	private static TweetProvider CreateTweetProviderForTwitterExtraction() {
		TwitterStreamJob j = new TwitterStreamJob(config);
		return j;
	}

	private static TweetProvider CreateMongoDbTweetProvier() {
		MongoDatabase b = new MongoDatabase(config);
		MongoDbTweetProvider provider = new MongoDbTweetProvider(b);
		return provider;
	}

	private static TweetoToMongoDBHandler CreateTweetToMongoDBHandler() {
		MongoDatabase b = new MongoDatabase(config);
		TweetoToMongoDBHandler handler = new TweetoToMongoDBHandler(b);
		return handler;
	}

	public static void main(String[] args) {

		TweetProvider p = CreateTweetProviderForTwitterExtraction();
		// TweetProvider p = CreateMongoDbTweetProvier();
		p.addTweetHandler(new TweetToConsolePrinter());

		// TweetoToMongoDBHandler handler = CreateTweetToMongoDBHandler();
		// p.addTweetHandler(handler);

		p.run();
		// System.out.print("Logged Tweets: "+ handler.getCount());
	}

}
