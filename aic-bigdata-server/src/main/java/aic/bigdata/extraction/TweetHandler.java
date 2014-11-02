package aic.bigdata.extraction;

import twitter4j.Status;

public interface TweetHandler {

	public void HandleStatusTweet(Status status, String tweet);

	public void HandleTweet(String tweet);
}
