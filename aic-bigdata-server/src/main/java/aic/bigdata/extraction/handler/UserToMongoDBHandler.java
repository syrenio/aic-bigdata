package aic.bigdata.extraction.handler;

import java.net.UnknownHostException;

import twitter4j.Status;
import twitter4j.User;
import aic.bigdata.extraction.MongoDatabase;
import aic.bigdata.extraction.TweetHandler;

import com.google.gson.Gson;

public class UserToMongoDBHandler implements TweetHandler {

	private MongoDatabase mongodb;
	private Gson gson;

	public UserToMongoDBHandler(MongoDatabase b) {
		this.gson = new Gson();
		mongodb = b;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		internalHandleTweet(status.getUser());
	}

	@Override
	public void HandleTweet(String tweet) {
		// TODO Auto-generated method stub

	}

	private void internalHandleTweet(User user) {
		try {
			String json = gson.toJson(user);
			this.mongodb.writeUser(json);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
