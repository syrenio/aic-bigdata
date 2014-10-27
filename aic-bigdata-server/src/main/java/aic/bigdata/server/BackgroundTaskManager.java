package aic.bigdata.server;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class BackgroundTaskManager {

	private static enum Singleton {
		INSTANCE;

		private Scheduler scheduler;

		private Singleton() {
			System.out.println("Singleton BackgroundTaskManager instance is created: " + System.currentTimeMillis());

			try {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}

		public Scheduler getScheduler() {
			return scheduler;
		}

	}

	private BackgroundTaskManager() {
	}

	public static void startServices(ServerConfig config) {

		try {
			Scheduler scheduler = Singleton.INSTANCE.getScheduler();

			scheduler.start();

			JobDataMap map = new JobDataMap();
			map.put("config", config);

			JobDetail job = JobBuilder.newJob(TwitterStreamJob.class).withIdentity(TwitterStreamJob.class.getName(), "group")
					.setJobData(map).build();

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "group").startNow().build();
			// .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(4).repeatForever()).build();

			scheduler.scheduleJob(job, trigger);

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	public static void stopServices() {
		try {
			Singleton.INSTANCE.getScheduler().shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static String getStatus() {
		boolean b = false;
		try {
			b = Singleton.INSTANCE.getScheduler().isStarted();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return "Services started: " + b;
	}

	public static Object getRunningJobs() throws SchedulerException {
		return Singleton.INSTANCE.getScheduler().getJobGroupNames();
	}

}
