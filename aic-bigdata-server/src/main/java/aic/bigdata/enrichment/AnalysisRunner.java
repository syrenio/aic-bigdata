package aic.bigdata.enrichment;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.extraction.MongoDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

public class AnalysisRunner {
	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}
	
	private static void FillAdsTopicDatabase() {
		AdsTopicsToDatabaseFiller filler = new AdsTopicsToDatabaseFiller(config);
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
	
	private static void AnalyzeTweets() {
		TopicAnalyzer analyzer = new TopicAnalyzer(config);
		try {
			analyzer.analyzeTweets();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DeleteSampleAdsTopics();
		
		FillAdsTopicDatabase();

		AnalyzeTweets();
	}
}
