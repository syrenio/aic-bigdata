package aic.bigdata.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.enrichment.AdsTopicsToDatabaseFiller;
import aic.bigdata.enrichment.TopicAnalyzer;
import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToDBHandler;
import aic.bigdata.extraction.provider.MongoDbTweetProvider;

public class TaskManager {

	private TwitterStreamJob streamJob;
	private TopicAnalyzer analyseJob;
	private MongoDbTweetProvider extractionJob;
	public ThreadPoolExecutor executor = null;

	public TaskManager() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	}

	public void startService(ServerConfig config) {

		MongoDatabase mongo = new MongoDatabase(config);
		SqlDatabase db;
		try {
			db = new SqlDatabase(config);

			streamJob = new TwitterStreamJob(config);
			// streamJob.addTweetHandler(new TweetToConsolePrinter());
			streamJob.addTweetHandler(new TweetToMongoDBHandler(mongo));
			streamJob.addTweetHandler(new UserToDBHandler(db));
			// job.addTweetHandler(new TweetToNeo4JHandler());

			if (config.getStreamOnStartup()) {
				if (executor.isShutdown())
					executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
				executor.execute(streamJob);
			}

		} catch (SQLException e) {
			System.err.println("Error with SqlDb!");
			e.printStackTrace();
		}
	}

	public void stopService() {
		if (streamJob != null)
			streamJob.stopProvider();
		if (analyseJob != null)
			analyseJob.stopAnalyze();
		if (extractionJob != null)
			extractionJob.stopProvider();
		executor.shutdown();
	}

	public String getTweetCount() {
		return "Tweets found: " + streamJob.getCounter();
	}

	public void startExtraction(ServerConfig cf) {

		MongoDatabase b = new MongoDatabase(cf);
		extractionJob = new MongoDbTweetProvider(b);

		TweetHandler handler = new TweetToNeo4JHandler(cf, GraphDatabase.getInstance());
		extractionJob.addTweetHandler(handler);

		if (executor.isShutdown())
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		executor.execute(extractionJob);
	}

	public void startAnalyse(ServerConfig cf) {

		MongoDatabase b = new MongoDatabase(cf);
		try {
			b.removeAdsTopics();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		AdsTopicsToDatabaseFiller filler = new AdsTopicsToDatabaseFiller(cf, GraphDatabase.getInstance());
		try {
			filler.fillDatabase();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		analyseJob = new TopicAnalyzer(cf, GraphDatabase.getInstance());

		if (executor.isShutdown())
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		executor.execute(analyseJob);
	}

	public boolean isActive() {
		return !executor.isShutdown() && !executor.isTerminated();
	}

}
