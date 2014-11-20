package aic.bigdata.extraction;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.enrichment.AdsTopicsToMongoDBFiller;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.UserToMongoDBHandler;
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
	
	private static void FillAdsTopicDatabase() {
		AdsTopicsToMongoDBFiller filler = new AdsTopicsToMongoDBFiller(new MongoDatabase(config));
        try {
			filler.fillDatabase();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void DeleteSampleAdsTopics() {
		MongoDatabase b = new MongoDatabase(config);
		try {
			b.removeAdsTopics();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//for testing purposes
		//DeleteSampleAdsTopics();
		
		FillAdsTopicDatabase();
		
		TweetProvider p = CreateTweetProviderForTwitterExtraction();
		// TweetProvider p = CreateMongoDbTweetProvier();
		// p.addTweetHandler(new TweetToFileHandler(config.getOutputFile()));
		// p.addTweetHandler(new TweetToJSONHandler(config.getOutputJSON()));
		
		TweetHandler handler = CreateUserToMongoDBHandler();
		TweetHandler handler2 = CreateTweetToMongoDBHandler();
		p.addTweetHandler(handler);
		p.addTweetHandler(handler2);

		p.run();

		// System.out.print("Logged Tweets: "+ handler.getCount());
	}

}
