package aic.bigdata.extraction;

import java.net.UnknownHostException;

import twitter4j.Status;

public interface TweetHandler {

	public void HandleStatusTweet(Status status, String tweet) throws UnknownHostException;

	public void HandleTweet(String tweet);
}
