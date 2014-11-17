package aic.bigdata.extraction;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.enrichment.AdsTopicsToMongoDBFiller;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.UserToMongoDBHandler;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
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

	private static TweetHandler CreateUserToMongoDBHandler() {
		MongoDatabase b = new MongoDatabase(config);
		TweetHandler handler = new UserToMongoDBHandler(b);
		return handler;
	}

	private static TweetHandler CreateTweetToNeo4JHandler() {
	        TweetHandler handler = new TweetToNeo4JHandler(config);
		return handler;
	}
	
	private static void FillAdsTopicDatabase() {
		AdsTopicsToMongoDBFiller filler = new AdsTopicsToMongoDBFiller(new MongoDatabase(config));
        try {
			filler.fillDatabase();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		FillAdsTopicDatabase();
		
		TweetProvider p = CreateTweetProviderForTwitterExtraction();
		// TweetProvider p = CreateMongoDbTweetProvier();
		// p.addTweetHandler(new TweetToFileHandler(config.getOutputFile()));
		// p.addTweetHandler(new TweetToJSONHandler(config.getOutputJSON()));
		
		TweetHandler handler = CreateUserToMongoDBHandler();
		TweetHandler handler2 = CreateTweetToMongoDBHandler();
		TweetHandler neo4jHandler = CreateTweetToNeo4JHandler();
		p.addTweetHandler(handler);
		p.addTweetHandler(handler2);
		p.addTweetHandler(neo4jHandler);

		p.run();

		// System.out.print("Logged Tweets: "+ handler.getCount());
	}

}
