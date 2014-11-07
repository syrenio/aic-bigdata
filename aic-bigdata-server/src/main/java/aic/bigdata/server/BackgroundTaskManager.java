package aic.bigdata.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aic.bigdata.extraction.handler.TweetToConsolePrinter;

public class BackgroundTaskManager {

	private static TwitterStreamJob job;

	private static enum Singleton {
		INSTANCE;

		public ThreadPoolExecutor executor;

		private Singleton() {
			System.out.println("Singleton BackgroundTaskManager instance is created: " + System.currentTimeMillis());
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		}

	}

	private BackgroundTaskManager() {
	}

	public static void startServices(ServerConfig config) {
		job = new TwitterStreamJob(config);
		job.addTweetHandler(new TweetToConsolePrinter());

		if (config.getStreamOnStartup()) {
			Singleton.INSTANCE.executor.execute(job);
		}

	}

	public static void stopServices() {
		job.stopTwitterJob();
		Singleton.INSTANCE.executor.shutdown();
	}

	public static String getStatus() {
		return "Service running: " + (Singleton.INSTANCE.executor.getActiveCount() != 0);
	}

	public static String getTweetCount() {
		return "Tweets found: " + job.getCounter();
	}

}
