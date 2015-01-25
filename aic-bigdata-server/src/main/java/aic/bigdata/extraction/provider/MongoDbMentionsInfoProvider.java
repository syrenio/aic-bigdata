package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.MentionsInfoHandler;
import aic.bigdata.extraction.MentionsInfoProvider;

import com.mongodb.DBObject;

public class MongoDbMentionsInfoProvider implements MentionsInfoProvider {

	private MongoDatabase db;
	private List<MentionsInfoHandler> handler = new ArrayList<MentionsInfoHandler>();
	private boolean running;

	public MongoDbMentionsInfoProvider(MongoDatabase db) {
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
//			for (DBObject c : db.getCursorForUserMentionedTopics()) {
			for (DBObject c : db.getIterableForUserMentionedTopics()) {
				if (!running)
					break;

				if (stepCounter >= stepSize) {
					DateTime end = new DateTime();
					stepCounter = 0;
					counter++;
					Duration diff = new Duration(begin, end);
					System.out.println("Current Mentions Info Count: " + (counter * stepSize)
							+ " Minutes:" + diff.getStandardMinutes());
				}

				//Long id = ((Double) c.get("_id")).longValue();
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

				DBObject topics = (DBObject) c.get("value");
				//System.out.println(c.toString());
				for (String field : topics.keySet()) {
					Integer count = ((Double) topics.get(field)).intValue();
					//System.out.println("MongoDBMentionsInfoProvider: count " + count);
					if (count > 0) {
						//System.out.println("MongoDBMentionsInfoProvider: Providing topic '" + field + "' with count " + count);
						for (MentionsInfoHandler t : this.handler) {
							t.HandleTopic(id, field, count);
						}
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
	public void addHandler(MentionsInfoHandler t) {
		this.handler.add(t);
	}

}
