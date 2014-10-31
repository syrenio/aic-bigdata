package aic.bigdata.server;

import java.util.Properties;

public class ServerConfig {

	private Properties twitter;
	private Properties server;
	private Properties mongo;

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

	public void setMongo(Properties propsMongo) {
		this.mongo = propsMongo;
		
	}
	public String getMongoDbName() {
		return mongo.getProperty("mongo.database");
	}
	
	public String getMongoCollection() {
		return mongo.getProperty("mongo.collection");
	}
}
