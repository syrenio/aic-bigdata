package aic.bigdata.extraction;

import java.net.UnknownHostException;

import twitter4j.Status;

public class TweetoToMongoDBHandler implements TweetHandler {

	private MongoDatabase mongodb;

	public TweetoToMongoDBHandler(MongoDatabase b) {
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

	private void internalHandleTweet(String tweet) {
		try {
			this.mongodb.writeTweet(tweet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
