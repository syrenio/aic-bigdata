package aic.bigdata.extraction;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBFiddeling {

	public static void main(String[] args) throws UnknownHostException {
		// Just for Code Development
		MongoClient mongoclient = new MongoClient();
		DB db = mongoclient.getDB("aicDump");
		DBCursor c = db.getCollection("Tweets").find();
		for (DBObject dbObject : c) {
			System.out.println(dbObject.toString());
		}
		
		//String object = 
		//		"{ 'name' : 'mkyong', 'age' : 30 }";
		
		//MongoDatabase b = new MongoDatabase( new ServerConfigBuilder().getConfig());
		//b.writeTweet(object);
		//for(String s: db.getCollectionNames())
		//{
		//	System.out.println(s);
		//}
	}

}
