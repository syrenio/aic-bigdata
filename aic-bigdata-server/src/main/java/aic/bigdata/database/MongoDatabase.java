package aic.bigdata.database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.lang.Iterable;

import twitter4j.Status;
import aic.bigdata.enrichment.AdObject;
import aic.bigdata.enrichment.TopicObject;
import aic.bigdata.server.ServerConfig;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
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
	private DBCollection retweeteroriginalauthors;
	private DBCollection usermentionedtopics;

	public MongoDatabase(ServerConfig cfg) {
		this.cfg = cfg;
	}

	public void writeTweet(String tweet) throws UnknownHostException {
		initialize();
		DBObject o = (DBObject) JSON.parse(tweet);
		this.tweets.insert(o);
	}

	public DBCursor getCursorForTweets() throws UnknownHostException {
		initialize();
		DBCursor c = tweets.find();
		return c;
	}

//	@Deprecated
//	public DBCursor getCursorForUsers() throws UnknownHostException {
//		initialize();
//		DBCursor c = users.find();
//		return c;
//	}

	public DBCursor getCursorForTopics() throws UnknownHostException {
		initialize();
		DBCursor c = topics.find();
		return c;
	}

	public DBCursor getCursorForRetweeterOriginalAuthors() throws UnknownHostException {
		initialize();
		DBCursor c = retweeteroriginalauthors.find();
		return c;
	}

	public Iterable<DBObject> getIterableForRetweeterOriginalAuthors() throws UnknownHostException {
		initialize();

		DBObject query = new BasicDBObject();
		DBObject exists = new BasicDBObject();
		exists.put("$exists", "true");
		query.put("retweeted_status", exists);

		MapReduceOutput out = tweets.mapReduce(
			"function() {" +
			"	emit(this.user.id, { arr: [this.retweeted_status.user.id], len: 1});" +
    		"}",
			"function(userId, objs) {" +
	        "	accum = objs[0];" +
        	"	for (var i = 1; i < objs.length; i++) {" +
			"		accum.arr = accum.arr.concat(objs[i].arr);" +
        	"	}" +
        	"	accum.len = accum.arr.length;" +
	        "	return accum;" +
    		"}",
			null,
			MapReduceCommand.OutputType.INLINE,
			query
		);

		return out.results();
	}

	public DBCursor getCursorForUserMentionedTopics() throws UnknownHostException {
		initialize();
		DBCursor c = usermentionedtopics.find();
		return c;
	}

	public boolean checkTweetExists(Status status) throws UnknownHostException {
		initialize();

		DBObject f = new BasicDBObject();
		f.put("id", status.getId());
		DBObject o = this.tweets.findOne(f);
		return o != null;
	}

	// outdated method
	/*public String readLatestTweetsAsOneString(Long userId, Integer latest) throws UnknownHostException {
		
		initialize();

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
	}*/
	
	/**
	 * Get all tweets as concatenated string from each user
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public HashMap<Long, String> getConcatTweets() throws UnknownHostException {
		initialize();
		
		String map = "function map() {emit(this.user.id, {text: this.text});}";
		String reduce = "function reduce(key, values) {var all=[]; values.forEach(function(x){all.push(x.text);}); return {'text': all.join(' . ')};}";
		
		MapReduceOutput out = tweets.mapReduce(map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
		
		HashMap<Long, String> tweets = new HashMap<Long, String>();

		for(DBObject obj : out.results()) {
			String text = (String) ((DBObject)obj.get("value")).get("text");
			double id = 0;
			try {
				id = (double) obj.get("_id");
				long idL = (long) id;
				tweets.put(idL, text);
			} catch(Exception e) {
				//System.out.println(id + "---------" + a.get("text"));
			}
			
		}
		
		return tweets;
	}

	public void writeAd(String ad) throws UnknownHostException {
		initialize();
		DBObject o = (DBObject) JSON.parse(ad);
		this.ads.insert(o);
	}

	public List<AdObject> getAds() throws UnknownHostException {
		initialize();
		DBCursor cur = this.ads.find();
		List<AdObject> list = new ArrayList<AdObject>();
		Gson g = new Gson();
		for (DBObject obj : cur) {
			AdObject a = g.fromJson(obj.toString(), AdObject.class);
			list.add(a);
		}
		return list;
	}

	public List<TopicObject> getTopics() throws UnknownHostException {
		initialize();
		DBCursor cur = this.topics.find();
		List<TopicObject> list = new ArrayList<TopicObject>();
		Gson g = new Gson();
		for (DBObject obj : cur) {
			TopicObject a = g.fromJson(obj.toString(), TopicObject.class);
			list.add(a);
		}
		return list;
	}

	public void writeTopic(String topic) throws UnknownHostException {
		initialize();
		DBObject o = (DBObject) JSON.parse(topic);
		this.topics.insert(o);
	}

	public List<String> getTopicNames() throws UnknownHostException {
		initialize();
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
		initialize();

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
		initialize();

		ads.remove(new BasicDBObject());
		topics.remove(new BasicDBObject());

	}

	public List<String> readAllTopicsInLowercase() throws UnknownHostException {
		initialize();

		DBCursor c = topics.find();
		List<String> topicsList = new ArrayList<String>();

		while (c.hasNext()) {
			String topic = (String) c.next().get("id");
			topicsList.add(topic.toLowerCase());
		}

		return topicsList;
	}

	public boolean checkTopicExists(String name) throws UnknownHostException {
		initialize();

		DBObject f = new BasicDBObject();
		f.put("id", name);
		DBObject o = this.topics.findOne(f);
		return o != null;
	}

	public boolean checkAdExists(int id) throws UnknownHostException {
		initialize();

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
		helper.createUniqueIndex("_id", this.retweeteroriginalauthors); // ?
		helper.createUniqueIndex("_id", this.usermentionedtopics); // ?

		helper.createIndex("user.id", this.tweets, 1);
		helper.createIndex("timestamp_ms", this.tweets, -1);
	}

	private void initialize() throws UnknownHostException {
		if (!init) {
			this.mongoclient = new MongoClient(); // use local started one
			String mongodbname = cfg.getMongoDbName();
			System.out.println(mongodbname);

			this.database = mongoclient.getDB(mongodbname);
			this.tweets = database.getCollection(cfg.getMongoCollection());
			this.users = database.getCollection(cfg.getMongoCollectionUsers());
			this.ads = database.getCollection(cfg.getMongoCollectionAds());
			this.topics = database.getCollection(cfg.getMongoCollectionTopics());
			this.retweeteroriginalauthors = database.getCollection(cfg.getMongoCollectionRetweeterOriginalAuthors());
			this.usermentionedtopics = database.getCollection(cfg.getMongoCollectionUserMentionedTopics());
			createIndexies();
			this.init = true;
		}
	}

}
