package aic.bigdata.extraction;

import java.net.UnknownHostException;

public class TweetoToMongoDBHandler implements TweetHandler {

	private MongoDatabase mongodb;
	public TweetoToMongoDBHandler(MongoDatabase b) {
		mongodb = b;
	}
	
	@Override
	public void HandleTweet(String Tweet) {
		try {
			this.mongodb.writeTweet(Tweet);
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		}

	}

}
