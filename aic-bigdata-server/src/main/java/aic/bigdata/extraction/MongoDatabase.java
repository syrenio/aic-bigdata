package aic.bigdata.extraction;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import aic.bigdata.server.ServerConfig;

public class MongoDatabase {
	
	private ServerConfig cfg;
	private MongoClient mongoclient;
	private DB database;
	private DBCollection collection;
	private boolean init = false;
	
	public MongoDatabase(ServerConfig cfg)
	{
		this.cfg = cfg;
	}

	public void writeTweet(String tweet) throws UnknownHostException {
		if(!init)intialize();
		DBObject o = (DBObject)JSON.parse(tweet);
		this.collection.insert(o);
		
	}

	private void intialize() throws UnknownHostException {
		this.mongoclient = new MongoClient(); //use local started one
		String mongodbname = cfg.getMongoDbName();
		System.out.println(mongodbname);
		this.database = mongoclient.getDB(mongodbname);
		this.collection = database.getCollection(cfg.getMongoCollection());
	}

}
