package aic.bigdata.extraction;

import twitter4j.Status;

public class TweetToConsolePrinter implements TweetHandler {

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		System.out.println(tweet);
		System.out.println(status.getUser().getName());
	}

	@Override
	public void HandleTweet(String tweet) {
		System.out.println(tweet);
	}

}
