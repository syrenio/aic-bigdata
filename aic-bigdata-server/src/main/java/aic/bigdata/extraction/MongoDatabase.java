package aic.bigdata.extraction;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.User;
import aic.bigdata.server.ServerConfig;

import com.google.gson.Gson;
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

	public boolean checkUserExists(User user) {
		DBObject f = new BasicDBObject();
		f.put("name", user.getName());
		DBObject o = this.users.findOne(f);
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

	private void createTweetsIndex(){
		DBObject idx = new BasicDBObject("id", 1);
		DBObject opt = new BasicDBObject("unique",true);
		opt.put("name", "uq_id_idx");
		//index exists
		List<DBObject> list =  this.tweets.getIndexInfo();
		for(DBObject i : list){
			if(i.get("name").equals("uq_id_idx")){
				return;
			}
		}
		this.tweets.createIndex(idx,opt);
	}
	
	private void intialize() throws UnknownHostException {
		this.mongoclient = new MongoClient(); // use local started one
		String mongodbname = cfg.getMongoDbName();
		System.out.println(mongodbname);
		this.database = mongoclient.getDB(mongodbname);
		this.tweets = database.getCollection(cfg.getMongoCollection());
		this.users = database.getCollection(cfg.getMongoCollectionUsers());
		createTweetsIndex();
		this.init = true;
	}

}
