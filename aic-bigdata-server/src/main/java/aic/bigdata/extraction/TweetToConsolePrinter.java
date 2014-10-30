package aic.bigdata.extraction;

public class TweetToConsolePrinter implements TweetHandler {

	@Override
	public void HandleTweet(String Tweet) {
	   System.out.println(Tweet);
	}

}
