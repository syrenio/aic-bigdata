package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import twitter4j.TwitterException;
import twitter4j.User;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.MongoDatabaseHelper;
import aic.bigdata.extraction.UserHandler;
import aic.bigdata.extraction.UserProvider;

import com.mongodb.DBObject;

public class MongoDbUserProvider implements UserProvider {

	private MongoDatabase db;
	private List<UserHandler> handler = new ArrayList<UserHandler>();
	private boolean running;

	public MongoDbUserProvider(MongoDatabase db) {
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
			MongoDatabaseHelper help = new MongoDatabaseHelper();
			for (DBObject c : db.getCursorForUsers()) {
				if (!running)
					break;

				if (stepCounter >= stepSize) {
					DateTime end = new DateTime();
					stepCounter = 0;
					counter++;
					Duration diff = new Duration(begin, end);
					System.out.println("Current User Count: " + (counter * stepSize) + " Minutes:"
							+ diff.getStandardMinutes());
				}

				String message = c.toString();
				User user = null;
				try {
					user = help.convertToUser(c);
					// user = TwitterObjectFactory.createUser(message);
				} catch (TwitterException e) {
					continue; // TODO: error message
				}

				for (UserHandler t : this.handler) {
					t.HandleUser(user);
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
	public void addHandler(UserHandler t) {
		this.handler.add(t);
	}

}
