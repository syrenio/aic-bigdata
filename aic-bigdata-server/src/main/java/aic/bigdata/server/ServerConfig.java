package aic.bigdata.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.collect.Lists;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class ServerConfig {

	private Properties twitter;
	private Properties server;
        private Properties mongo;
        private Properties neo4j;

	private final String OUTPUTFILE = "default_output.log";
	private final String OUTPUTJSON = "default_output.json";

	private Twitter tw = null;

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

	// aic.bigdata.stream.addDBUsers
	public Boolean isAddDBUsers() {
		return new Boolean(server.getProperty("aic.bigdata.stream.addDBUsers"));
	}

	// aic.bigdata.stream.maxFollowersFromDB
	public Integer getMaxFollowersFromDB() {
		return new Integer(server.getProperty("aic.bigdata.stream.maxFollowersFromDB"));
	}

	private Long getTwitterUserId(String name) throws TwitterException {
		if (tw == null) {
			TwitterFactory twf = new TwitterFactory(getConfigForTwitter4J());
			tw = twf.getInstance();
		}
		Query query = new Query("from:" + name).count(1);
		QueryResult res = tw.search(query);
		if (res.getTweets().size() > 0) {
			Long id = res.getTweets().get(0).getUser().getId();
			System.out.println("User found " + name + ": " + id);
			return id;
		}
		System.out.println("User not found " + name + "!");
		return null;
	}

	public List<Long> getFollowers() {

		String[] strlist = server.getProperty("aic.bigdata.stream.followers").split(",");
		List<Long> longlist = new ArrayList<Long>();
		for (int i = 0; i < strlist.length; i++) {
			String entry = strlist[i];
			if ("".equals(entry))
				continue;

			if (StringUtils.isNumeric(entry)) {
				longlist.add(Long.valueOf(entry));
			} else {
				try {
					Long id = getTwitterUserId(entry);
					if (id != null)
						longlist.add(id);
				} catch (TwitterException e) {
					System.err.println("error with twitter-username: " + entry);
				}
			}
		}
		return longlist;
	}

	public List<String> getTerms() {
		return Lists.newArrayList(server.getProperty("aic.bigdata.stream.terms").split(","));
	}

	// aic.bigdata.stream.languages
	public List<String> getLanguages() {
		return Lists.newArrayList(server.getProperty("aic.bigdata.stream.languages").split(","));
	}

	// aic.bigdata.stream.outputFile
	public String getOutputFile() {
		String tmp = server.getProperty("aic.bigdata.stream.outputFile");
		if (StringUtils.isEmpty(tmp))
			return OUTPUTFILE;
		return tmp;
	}

	// aic.bigdata.stream.outputJSON
	public String getOutputJSON() {
		String tmp = server.getProperty("aic.bigdata.stream.outputJSON");
		if (StringUtils.isEmpty(tmp))
			return OUTPUTJSON;
		return tmp;
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

	public String getMongoCollectionUsers() {
		return mongo.getProperty("mongo.collection.users");
	}

	private Configuration getConfigForTwitter4J() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(twitter.getProperty("oauth.consumerKey"));
		builder.setOAuthConsumerSecret(twitter.getProperty("oauth.consumerSecret"));
		builder.setOAuthAccessToken(twitter.getProperty("oauth.accessToken"));
		builder.setOAuthAccessTokenSecret(twitter.getProperty("oauth.accessTokenSecret"));
		return builder.build();
	}

	public OAuth1 createOAuth() {
		return new OAuth1(twitter.getProperty("oauth.consumerKey"), twitter.getProperty("oauth.consumerSecret"),
				twitter.getProperty("oauth.accessToken"), twitter.getProperty("oauth.accessTokenSecret"));
	}

	public Properties getNeo4J() {
		return neo4j;
	}

	public void setNeo4J(Properties neo4j) {
		this.neo4j = neo4j;
	}

	public String getNeo4JDbName() {
		return neo4j.getProperty("neo4j.database");
	}

}
