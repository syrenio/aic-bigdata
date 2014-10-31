package aic.bigdata.extraction;

import java.net.UnknownHostException;

import aic.bigdata.server.ServerConfig;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBFiddeling {

	public static void main(String[] args) throws UnknownHostException {
		// Just for Code Development
		//MongoClient mongoclient = new MongoClient();
		//DB db = mongoclient.getDB("mytestdb");
		String object = 
				"{ 'name' : 'mkyong', 'age' : 30 }";
		
		MongoDatabase b = new MongoDatabase( new ServerConfigBuilder().getConfig());
		b.writeTweet(object);
		//for(String s: db.getCollectionNames())
		//{
		//	System.out.println(s);
		//}
	}

}
