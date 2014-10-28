package aic.bigdata.server;

import java.util.Properties;

public class ServerConfig {

	private Properties twitter;
	private Properties server;

	public Properties getTwitter() {
		return twitter;
	}

	public void setTwitter(Properties twitter) {
		this.twitter = twitter;
	}

	public Properties getServer() {
		return server;
	}

	public void setServer(Properties server) {
		this.server = server;
	}

	// aic.bigdata.stream.onStartup
	public Boolean getStreamOnStartup() {
		return new Boolean(server.getProperty("aic.bigdata.stream.onStartup"));
	}

	// aic.bigdata.stream.maxTweetCount
	public Integer getMaxTweetCount() {
		return new Integer(server.getProperty("aic.bigdata.stream.maxTweetCount"));
	}
}
