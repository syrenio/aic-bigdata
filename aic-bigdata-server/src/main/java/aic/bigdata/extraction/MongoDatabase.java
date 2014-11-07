package aic.bigdata.extraction;

import java.net.UnknownHostException;

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
	private DBCollection collection;
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
		this.collection.insert(o);
	}

	public DBCursor getCursorForTweets() throws UnknownHostException {
		if (!init)
			intialize();
		DBCursor c = collection.find();
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

	private void intialize() throws UnknownHostException {
		this.mongoclient = new MongoClient(); // use local started one
		String mongodbname = cfg.getMongoDbName();
		System.out.println(mongodbname);
		this.database = mongoclient.getDB(mongodbname);
		this.collection = database.getCollection(cfg.getMongoCollection());
		this.users = database.getCollection(cfg.getMongoCollectionUsers());
		this.init = true;
	}

}
