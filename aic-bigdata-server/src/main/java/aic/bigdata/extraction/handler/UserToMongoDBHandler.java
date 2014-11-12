package aic.bigdata.extraction.handler;

import java.net.UnknownHostException;

import twitter4j.Status;
import twitter4j.User;
import aic.bigdata.extraction.MongoDatabase;
import aic.bigdata.extraction.TweetHandler;

public class UserToMongoDBHandler implements TweetHandler {

	private MongoDatabase mongodb;

	public UserToMongoDBHandler(MongoDatabase b) {
		mongodb = b;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet)
			throws UnknownHostException {

		if (!mongodb.checkUserExists(status.getUser())) {
			internalHandleTweet(status.getUser());
		}
	}

	@Override
	public void HandleTweet(String tweet) {
		// TODO Auto-generated method stub

	}

	private void internalHandleTweet(User user) {
		try {
			this.mongodb.writeUser(user);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
