package aic.bigdata.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTaskManager {

	private static enum Singleton {
		INSTANCE;

		public ExecutorService executor;

		private Singleton() {
			System.out.println("Singleton BackgroundTaskManager instance is created: " + System.currentTimeMillis());
			executor = Executors.newFixedThreadPool(5);
		}

	}

	private BackgroundTaskManager() {
	}

	public static void startServices(ServerConfig config) {

		TwitterStreamJob job = new TwitterStreamJob(config);
		Singleton.INSTANCE.executor.execute(job);

	}

	public static void stopServices() {
		Singleton.INSTANCE.executor.shutdown();
	}

	public static String getStatus() {
		return "Service running: " + !Singleton.INSTANCE.executor.isTerminated();
	}

}
