package aic.bigdata.importer

import groovy.json.JsonSlurper

import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.util.JSON

class Importer {

	private static String file = "tweets.txt"
	private static String mongoServer = "127.0.0.1"
	private static Integer mongoPort = 27017
	private static String mongoDB = "ImportDB"
	private static String tweetsColleciton = "ImportTweets"
	private static Integer stepSize = 1000000 // 1.000.000




	static main(args) {
		def mongo = new MongoClient(mongoServer,mongoPort)
		def db  = mongo.getDB(mongoDB)
		def col = db.getCollection(tweetsColleciton)
		def slurp = new JsonSlurper()
		
		def f = new File(file)
		def count = 0
		f.eachLine {
			if(!it.isNumber()){
				DBObject obj = JSON.parse(it)
				col.insert(obj)
			}
			if(count%stepSize==0){
				println count/stepSize
			}
			count++
		}
	}
}
