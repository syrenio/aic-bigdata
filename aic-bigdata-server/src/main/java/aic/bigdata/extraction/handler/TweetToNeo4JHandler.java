package aic.bigdata.extraction.handler;

import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.server.ServerConfig;
import twitter4j.Status;

public class TweetToNeo4JHandler implements TweetHandler {

	private int tweetsLogged = 0;
        final private String dbName;

	public TweetToNeo4JHandler(ServerConfig config) {
	        dbName = config.getNeo4JDbName();
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
	        System.out.println("TweetToNeo4JHandler: tweets so far: " + getCount());
		tweetsLogged++;
	}

	@Override
	public void HandleTweet(String tweet) {
		;
	}

	public int getCount() {
		return this.tweetsLogged;
	}
}
