package aic.bigdata.database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;
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
	private Gson gson;
	private DBCollection ads;
	private DBCollection topics;

	public MongoDatabase(ServerConfig cfg) {
		this.gson = new Gson();
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

	public boolean checkUserExists(User user) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject o = getUserById(user.getId());
		return o != null;
	}

	public boolean checkTweetExists(Status status) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject f = new BasicDBObject();
		f.put("id", status.getId());
		DBObject o = this.tweets.findOne(f);
		return o != null;
	}

	public void writeUser(User user) throws UnknownHostException {
		if (!init)
			intialize();

		if (!checkUserExists(user)) {
			String json = gson.toJson(user);
			DBObject o = (DBObject) JSON.parse(json);
			this.users.insert(o);
		}
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

	public List<Long> readUserIds(Integer limit) throws UnknownHostException {
		if (!init)
			intialize();

		List<Long> list = new ArrayList<Long>();
		DBObject f = new BasicDBObject();
		DBObject k = new BasicDBObject();
		k.put("id", 1);
		DBCursor find = this.users.find(f, k).limit(limit);
		for (DBObject o : find) {
			Long val = ((Number) o.get("id")).longValue();
			list.add(val);
		}
		return list;
	}

	public void writeAd(String ad) throws UnknownHostException {
		if (!init)
			intialize();
		DBObject o = (DBObject) JSON.parse(ad);
		this.ads.insert(o);
	}

	public void writeTopic(String topic) throws UnknownHostException {
		if (!init)
			intialize();
		DBObject o = (DBObject) JSON.parse(topic);
		this.topics.insert(o);
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
		MongoDatabaseHelper dbBuilder = new MongoDatabaseHelper();
		
		
		this.database = mongoclient.getDB(mongodbname);
		this.tweets = database.getCollection(cfg.getMongoCollection());
		this.users = database.getCollection(cfg.getMongoCollectionUsers());
		this.ads = database.getCollection(cfg.getMongoCollectionAds());
		this.topics = database.getCollection(cfg.getMongoCollectionTopics());
		createIndexies();
		this.init = true;
	}

	public List<DBObject> getUsers(int pageNumber, int size) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject sort = new BasicDBObject("name", 1);
		DBCursor cur = this.users.find().sort(sort).skip(pageNumber > 0 ? ((pageNumber - 1) * size) : 0).limit(size);

		List<DBObject> list = new ArrayList<DBObject>();
		for (DBObject dbObject : cur) {
			list.add(dbObject);
		}
		return list;
	}

	public DBObject getUserById(Long id) throws UnknownHostException {
		if (!init)
			intialize();

		DBObject f = new BasicDBObject();
		f.put("id", id);
		DBObject o = this.users.findOne(f);
		return o;
	}

}
