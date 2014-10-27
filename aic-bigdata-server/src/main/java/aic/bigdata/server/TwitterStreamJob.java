package aic.bigdata.server;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterStreamJob implements Job {

	private ServerConfig config;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub

		setConfig((ServerConfig) context.getJobDetail().getJobDataMap().get("config"));
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

		Client client = createStreamClient(msgQueue);
		client.connect();

		// on a different thread, or multiple different threads....
		while (!client.isDone()) {
			String msg;
			try {
				// FIXME TWEET CODE!
				msg = msgQueue.take();
				System.out.println(msg);
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		client.stop();
	}

	private Client createStreamClient(BlockingQueue<String> msgQueue) {

		/**
		 * Set up your blocking queues: Be sure to size these properly based on
		 * expected TPS of your stream
		 */

		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<Long> followings = Lists.newArrayList(1234L, 566788L);
		List<String> terms = Lists.newArrayList("twitter", "api");
		hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = createOAuth();

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01")
				// optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue)).eventMessageQueue(eventQueue);

		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

	private OAuth1 createOAuth() {
		return new OAuth1(config.getTwitter().getProperty("oauth.consumerKey"), config.getTwitter().getProperty(
				"oauth.consumerSecret"), config.getTwitter().getProperty("oauth.accessToken"), config.getTwitter().getProperty(
				"oauth.accessTokenSecret"));
	}

	public ServerConfig getConfig() {
		return config;
	}

	public void setConfig(ServerConfig config) {
		this.config = config;
	}

}
