package aic.bigdata.extraction.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import twitter4j.Status;
import aic.bigdata.extraction.TweetHandler;

public class TweetToFileHandler implements TweetHandler {

	private String path;

	public TweetToFileHandler(String path) {
		this.path = path;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(path, true));

			out.println(tweet);
			out.print(status.getUser().getName() + " : ");
			out.print(status.isRetweet() ? " RT " : "");
			out.print(status.isFavorited() ? " FV " : "");
			out.println(status.getInReplyToScreenName() != null ? " InReplyToUserId: " + status.getInReplyToScreenName()
					: " ");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void HandleTweet(String tweet) {
	}

}
