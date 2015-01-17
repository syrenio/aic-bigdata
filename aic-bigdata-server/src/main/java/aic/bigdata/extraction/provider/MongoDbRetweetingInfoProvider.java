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
import aic.bigdata.extraction.RetweetingInfoHandler;
import aic.bigdata.extraction.RetweetingInfoProvider;

import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.BasicDBList;

public class MongoDbRetweetingInfoProvider implements RetweetingInfoProvider {

	private MongoDatabase db;
	private List<RetweetingInfoHandler> handler = new ArrayList<RetweetingInfoHandler>();
	private boolean running;

	public MongoDbRetweetingInfoProvider(MongoDatabase db) {
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
			for (DBObject c : db.getCursorForRetweeterOriginalAuthors()) {
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

				if (!(c.containsField("_id") && c.containsField("value.arr"))) {
					; // TODO: panic
				}

				Long id = (Long) c.get("_id");
				BasicDBList l = (BasicDBList) c.get("value.arr");
				List<Long> originalAuthors = (List<Long>) (List<?>) l; // casting TWICE is better than casting once...

				for (RetweetingInfoHandler t : this.handler) {
					t.HandleOriginalAuthors(id, originalAuthors);
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
	public void addHandler(RetweetingInfoHandler t) {
		this.handler.add(t);
	}

}
