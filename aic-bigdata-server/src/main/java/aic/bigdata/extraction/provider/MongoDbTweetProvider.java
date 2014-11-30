package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.extraction.TweetProvider;

import com.mongodb.DBObject;
import com.mongodb.DBCursor;

public class MongoDbTweetProvider implements TweetProvider {

	private MongoDatabase db;
	private List<TweetHandler> handler = new ArrayList<TweetHandler>();
	private boolean running;

	public MongoDbTweetProvider(MongoDatabase db) {
		this.db = db;
	}

	@Override
	public void run() {
		DateTime begin = new DateTime();
		this.running = true;
		long counter = 0;
		long stepCounter = 0;
		long stepSize = 1000;

		try {
			for (DBObject c : db.getCursorForTweets()) {
				if (!running)
					break;

				if (stepCounter >= stepSize) {
					DateTime end = new DateTime();
					stepCounter = 0;
					counter++;
					Duration diff = new Duration(begin, end);
					System.out.println("Current Count: " + (counter * stepSize)
							+ " Minutes:" + diff.getStandardMinutes());
				}

				String message = c.toString();
				Status status = null;
				try {
					status = TwitterObjectFactory.createStatus(message);
				} catch (TwitterException e) {
					continue;
				}
				for (TweetHandler t : this.handler) {
					t.HandleStatusTweet(status, message);
				}
				stepCounter++;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopProvider() {
		this.running = false;
	}

	@Override
	public void addTweetHandler(TweetHandler t) {
		this.handler.add(t);
	}

}
