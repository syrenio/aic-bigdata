package aic.bigdata.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.enrichment.TopicAnalyzer;
import aic.bigdata.extraction.handler.TweetToConsolePrinter;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToMongoDBHandler;

public class TaskManager {

	private TwitterStreamJob job;
	private TopicAnalyzer analyseJob;
	public ThreadPoolExecutor executor = null;

	public TaskManager() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	}

	public void startService(ServerConfig config) {
		
		MongoDatabase mongo = new MongoDatabase(config);

		job = new TwitterStreamJob(config);
		job.addTweetHandler(new TweetToConsolePrinter());
		job.addTweetHandler(new TweetToMongoDBHandler(mongo));
		job.addTweetHandler(new UserToMongoDBHandler(mongo));
		// job.addTweetHandler(new TweetToNeo4JHandler());

		if (config.getStreamOnStartup()) {
			if(executor.isShutdown())
				executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
			executor.execute(job);
		}

	}

	public void stopService() {
		if (job != null)
			job.stopTwitterJob();
		if(analyseJob != null)
			analyseJob.stopAnalyze();
		executor.shutdown();
	}

	public String getStatus() {
		return "Service running: " + (executor.getActiveCount() != 0);
	}

	public String getTweetCount() {
		return "Tweets found: " + job.getCounter();
	}

	public void startAnalyse(ServerConfig cf) {
		analyseJob = new TopicAnalyzer(cf, GraphDatabase.getInstance());

		if(executor.isShutdown())
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		executor.execute(analyseJob);
	}

}
