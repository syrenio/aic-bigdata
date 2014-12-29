package aic.bigdata.extraction;

import java.sql.SQLException;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.extraction.handler.TweetToConsolePrinter;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.UserToDBHandler;
import aic.bigdata.extraction.provider.MongoDbTweetProvider;
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

	private static TweetHandler CreateTweetToMongoDBHandler() {
		MongoDatabase b = new MongoDatabase(config);
		TweetHandler handler = new TweetToMongoDBHandler(b);
		return handler;
	}

	private static TweetHandler CreateUserToDBHandler() {
		SqlDatabase db;
		TweetHandler handler = null;
		try {
			db = new SqlDatabase(config);
			handler = new UserToDBHandler(db);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error creating UserToDBHandler");
		}
		return handler;
	}

	public static void main(String[] args) {
		TweetProvider p = CreateTweetProviderForTwitterExtraction();
		// TweetProvider p = CreateMongoDbTweetProvier();
		// p.addTweetHandler(new TweetToFileHandler(config.getOutputFile()));
		// p.addTweetHandler(new TweetToJSONHandler(config.getOutputJSON()));

		TweetHandler handler = CreateUserToDBHandler();
		TweetHandler handler2 = CreateTweetToMongoDBHandler();
		p.addTweetHandler(handler);
		p.addTweetHandler(handler2);

		TweetHandler con = new TweetToConsolePrinter();
		p.addTweetHandler(con);

		p.run();

		// System.out.print("Logged Tweets: "+ handler.getCount());
	}

}
