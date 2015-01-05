package aic.bigdata.database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import aic.bigdata.enrichment.AdObject;
import aic.bigdata.server.ServerConfig;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDatabase {

	private ServerConfig cfg;
	private MongoClient mongoclient;
	private DB database;
	private DBCollection tweets;
	private boolean init = false;
	private DBCollection users;
	private DBCollection ads;
	private DBCollection topics;

	public MongoDatabase(ServerConfig cfg) {
		this.cfg = cfg;
	}

	public void writeTweet(String tweet) throws UnknownHostException {
		if (!init)
			intialize();
		DBObject o = (DBObject) JSON.parse(tweet);
		this.tweets.insert(o);
	}

	public DBCursor getCursorForTweets() throws UnknownHostException {
		if (!init)
			intialize();
		DBCursor c = tweets.find();
		return c;
	}

	public DBCursor getCursorForUsers() throws UnknownHostException {
		if (!init)
			intialize();
		DBCursor c = users.find();
		return c;
	}

	public boolean checkTweetExists(Status status) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject f = new BasicDBObject();
		f.put("id", status.getId());
		DBObject o = this.tweets.findOne(f);
		return o != null;
	}

	public String readLatestTweetsAsOneString(Long userId, Integer latest) throws UnknownHostException {
		if (!init)
			intialize();

		BasicDBObject usr = new BasicDBObject();
		usr.put("user.id", userId);

		BasicDBObject sort = new BasicDBObject();
		sort.put("timestamp_ms", -1);

		DBCursor c = this.tweets.find(usr).sort(sort).limit(latest);
		StringBuilder result = new StringBuilder();

		while (c.hasNext()) {
			String tweet = (String) c.next().get("text");
			result.append(" ").append(tweet);
		}

		return result.toString();
	}

	public void writeAd(String ad) throws UnknownHostException {
		if (!init)
			intialize();
		DBObject o = (DBObject) JSON.parse(ad);
		this.ads.insert(o);
	}

	public List<AdObject> getAds() throws UnknownHostException {
		if (!init)
			intialize();
		DBCursor cur = this.ads.find();
		List<AdObject> list = new ArrayList<AdObject>();
		Gson g = new Gson();
		for (DBObject obj : cur) {
			AdObject a = g.fromJson(obj.toString(), AdObject.class);
			list.add(a);
		}
		return list;
	}

	public void writeTopic(String topic) throws UnknownHostException {
		if (!init)
			intialize();
		DBObject o = (DBObject) JSON.parse(topic);
		this.topics.insert(o);
	}

	public List<String> getTopicNames() throws UnknownHostException {
		if (!init)
			intialize();
		List<String> names = new ArrayList<String>();
		DBCursor cursor = this.topics.find();
		for (DBObject o : cursor) {
			names.add(o.get("id").toString()); // id == name
		}
		return names;
	}

	/**
	 * Updates a topic by adding or removing an ad id in the "ad" field
	 * 
	 * @param topicname
	 * @param adId
	 * @param adding
	 *            Adds an ad id, if true, removes an ad id, if false.
	 * @throws UnknownHostException
	 */
	public void updateTopicAd(String topicname, int adId, boolean adding) throws UnknownHostException {
		if (!init)
			intialize();

		BasicDBObject upd = new BasicDBObject();
		upd.put("id", topicname);

		DBObject dbOut = topics.findOne(upd);
		BasicDBList adsList = (BasicDBList) dbOut.get("ads");
		boolean changed = false;

		if (adding) {
			if (!adsList.contains(adId)) {
				adsList.add(adId);
				changed = true;
			}
		} else {
			if (adsList.contains(adId)) {
				adsList.remove(new Integer(adId));
				changed = true;
			}
		}

		// field was modified, thus update db
		if (changed) {
			dbOut.put("ads", adsList);
			topics.findAndModify(upd, dbOut);
		}
	}

	public void removeAdsTopics() throws UnknownHostException {
		if (!init)
			intialize();

		ads.remove(new BasicDBObject());
		topics.remove(new BasicDBObject());

	}

	public List<String> readAllTopicsInLowercase() throws UnknownHostException {
		if (!init)
			intialize();

		DBCursor c = topics.find();
		List<String> topicsList = new ArrayList<String>();

		while (c.hasNext()) {
			String topic = (String) c.next().get("id");
			topicsList.add(topic.toLowerCase());
		}

		return topicsList;
	}

	public boolean checkTopicExists(String name) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject f = new BasicDBObject();
		f.put("id", name);
		DBObject o = this.topics.findOne(f);
		return o != null;
	}

	public boolean checkAdExists(int id) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject f = new BasicDBObject();
		f.put("id", id);
		DBObject o = this.ads.findOne(f);
		return o != null;
	}

	private void createIndexies() {
		MongoDatabaseHelper helper = new MongoDatabaseHelper();

		helper.createUniqueIndex("id", this.users);
		helper.createUniqueIndex("id", this.tweets);
		helper.createUniqueIndex("id", this.ads);
		helper.createUniqueIndex("id", this.topics);

		helper.createIndex("user.id", this.tweets, 1);
		helper.createIndex("timestamp_ms", this.tweets, -1);
	}

	private void intialize() throws UnknownHostException {
		this.mongoclient = new MongoClient(); // use local started one
		String mongodbname = cfg.getMongoDbName();
		System.out.println(mongodbname);

		this.database = mongoclient.getDB(mongodbname);
		this.tweets = database.getCollection(cfg.getMongoCollection());
		this.users = database.getCollection(cfg.getMongoCollectionUsers());
		this.ads = database.getCollection(cfg.getMongoCollectionAds());
		this.topics = database.getCollection(cfg.getMongoCollectionTopics());
		createIndexies();
		this.init = true;
	}

}
