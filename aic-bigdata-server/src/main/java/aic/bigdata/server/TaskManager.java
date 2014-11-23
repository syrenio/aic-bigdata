package aic.bigdata.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aic.bigdata.extraction.handler.TweetToConsolePrinter;

public class TaskManager {

	private TwitterStreamJob job;
	public ThreadPoolExecutor executor;

	public void startService(ServerConfig config) {
		job = new TwitterStreamJob(config);
		job.addTweetHandler(new TweetToConsolePrinter());

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
