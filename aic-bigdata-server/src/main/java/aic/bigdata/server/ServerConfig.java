package aic.bigdata.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;

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

	public List<Long> getFollowers() {
		String[] strlist = server.getProperty("aic.bigdata.stream.followers").split(",");
		List<Long> longlist = new ArrayList<Long>();
		for (int i = 0; i < strlist.length; i++) {
			longlist.add(Long.valueOf(strlist[i]));
		}
		return longlist;
	}

	public List<String> getTerms() {
		return Lists.newArrayList(server.getProperty("aic.bigdata.stream.terms").split(","));
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
