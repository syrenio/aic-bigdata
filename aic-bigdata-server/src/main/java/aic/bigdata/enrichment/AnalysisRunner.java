package aic.bigdata.enrichment;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.server.ServerConfig;

public class AnalysisRunner {
	private static ServerConfig config;
	
	static {
		config = new ServerConfigBuilder().getConfig();
	}
	
	private static void FillAdsTopicDatabase(TweetToNeo4JHandler neo4jHandler) {
		AdsTopicsToDatabaseFiller filler = new AdsTopicsToDatabaseFiller(config, neo4jHandler);
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
	
	private static void AnalyzeTweets(TweetToNeo4JHandler neo4jHandler) {
		TopicAnalyzer analyzer = new TopicAnalyzer(config, neo4jHandler);
		try {
			analyzer.analyzeTweets();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TweetToNeo4JHandler neo4jHandler = new TweetToNeo4JHandler(config);
		DeleteSampleAdsTopics();
		
		FillAdsTopicDatabase(neo4jHandler);

		AnalyzeTweets(neo4jHandler);
	}
}
