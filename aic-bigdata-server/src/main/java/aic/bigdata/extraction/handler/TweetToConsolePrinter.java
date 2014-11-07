package aic.bigdata.extraction.handler;

import aic.bigdata.extraction.TweetHandler;
import twitter4j.Status;

public class TweetToConsolePrinter implements TweetHandler {

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		System.out.println(tweet);
		System.out.print(status.getUser().getName() + " : ");
		System.out.print(status.isRetweet() ? " RT " : "");
		System.out.print(status.isFavorited() ? " FV " : "");
		System.out.println(status.getInReplyToScreenName() != null ? " InReplyToUserId: " + status.getInReplyToScreenName()
				: " ");

	}

	@Override
	public void HandleTweet(String tweet) {
		System.out.println(tweet);
	}

}
