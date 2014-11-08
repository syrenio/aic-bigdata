package aic.bigdata.extraction.handler;

import java.net.UnknownHostException;

import aic.bigdata.extraction.MongoDatabase;
import aic.bigdata.extraction.TweetHandler;
import twitter4j.Status;

public class TweetToMongoDBHandler implements TweetHandler {

	private MongoDatabase mongodb;
	private int tweetsLogged = 0;

	public TweetToMongoDBHandler(MongoDatabase b) {
		mongodb = b;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		internalHandleTweet(tweet);
	}

	@Override
	public void HandleTweet(String tweet) {
		internalHandleTweet(tweet);
	}
	
	public int getCount()
	{
		return this.tweetsLogged;
	}
	private void internalHandleTweet(String tweet) {
		try {
			this.mongodb.writeTweet(tweet);
			tweetsLogged++;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
