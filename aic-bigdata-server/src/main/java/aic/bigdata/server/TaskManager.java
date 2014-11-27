package aic.bigdata.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.handler.TweetToConsolePrinter;
import aic.bigdata.extraction.handler.TweetToMongoDBHandler;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToMongoDBHandler;

public class TaskManager {

	private TwitterStreamJob job;
	public ThreadPoolExecutor executor;

	public void startService(ServerConfig config) {
		MongoDatabase mongo = new MongoDatabase(config);
		
		job = new TwitterStreamJob(config);
		job.addTweetHandler(new TweetToConsolePrinter());
		job.addTweetHandler(new TweetToMongoDBHandler(mongo));
		job.addTweetHandler(new UserToMongoDBHandler(mongo));
		job.addTweetHandler(new TweetToNeo4JHandler(config));

		if (config.getStreamOnStartup()) {
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
			executor.execute(job);
		}

	}

	public void stopService() {
		job.stopTwitterJob();
		executor.shutdown();
	}

	public String getStatus() {
		return "Service running: " + (executor.getActiveCount() != 0);
	}

	public String getTweetCount() {
		return "Tweets found: " + job.getCounter();
	}

}
