package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import java.lang.ClassCastException;

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
					System.out.println("Current Retweeting Info Count: " + (counter * stepSize)
							+ " Minutes:" + diff.getStandardMinutes());
				}

				if (!(c.containsField("_id") && c.containsField("value.arr"))) {
					; // TODO: panic
				}

				// this is pretty ugly
				Long id = null;
				try {
					id = ((Double) c.get("_id")).longValue();
				}
				catch (ClassCastException cce) {
					try {
						id = (Long) c.get("_id");
					}
					catch (ClassCastException cce2) {
						System.err.println("_id is neither Long nor Double?");
					}
				}

				// and it gets worse
				BasicDBList l = (BasicDBList) ((DBObject) c.get("value")).get("arr");
				List<Long> originalAuthors = new ArrayList<Long>(l.size());
				for (Object o : l) {
					try {
						originalAuthors.add((Long) o);
					}
					catch (ClassCastException cce) {
						try {
							originalAuthors.add(((Double) o).longValue());
						}
						catch(ClassCastException cce2) {
							System.err.println("I tried, but this thing is neither Long nor Double. I give up.");
						}
					}
				}

				if (id != null) {
					for (RetweetingInfoHandler t : this.handler) {
						t.HandleOriginalAuthors(id, originalAuthors);
					}
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
