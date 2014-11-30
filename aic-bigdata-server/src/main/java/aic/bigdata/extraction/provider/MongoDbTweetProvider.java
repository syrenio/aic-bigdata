package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.TweetHandler;
import aic.bigdata.extraction.TweetProvider;

import com.mongodb.DBObject;
import com.mongodb.DBCursor;

public class MongoDbTweetProvider implements TweetProvider{

	private MongoDatabase db;
	private List<TweetHandler> handler = new ArrayList<TweetHandler>();
	private boolean running;
	
	public MongoDbTweetProvider(MongoDatabase db) {
		this.db=db;
	}
	
	@Override
	public void run() {
		this.running = true;
		try {
			for(DBObject c : db.getCursorForTweets())
			{
				if(!running)
					break;
				
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
