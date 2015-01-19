package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.MongoDatabaseHelper;
import aic.bigdata.extraction.TopicHandler;
import aic.bigdata.extraction.TopicProvider;

import com.mongodb.DBObject;

public class MongoDbTopicProvider implements TopicProvider {

	private MongoDatabase db;
	private List<TopicHandler> handler = new ArrayList<TopicHandler>();
	private boolean running;

	public MongoDbTopicProvider(MongoDatabase db) {
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
			for (DBObject c : db.getCursorForTopics()) {
				if (!running)
					break;

				if (stepCounter >= stepSize) {
					DateTime end = new DateTime();
					stepCounter = 0;
					counter++;
					Duration diff = new Duration(begin, end);
					System.out.println("Current Topic Count: " + (counter * stepSize) + " Minutes:"
							+ diff.getStandardMinutes());
				}

				String topic = (String) c.get("id");

				for (TopicHandler t : this.handler) {
					t.HandleTopic(topic);
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
	public void addHandler(TopicHandler t) {
		this.handler.add(t);
	}

}
