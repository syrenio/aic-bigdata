package aic.bigdata.enrichment;

import java.net.UnknownHostException;
import java.util.List;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.handler.TweetToNeo4JHandler;
import aic.bigdata.server.ServerConfig;

/**
 *  Determines topics that the user is interested in and stores them in Neo4J.
 */
public class TopicAnalyzer implements Runnable {

	private MongoDatabase mongodb;
	private TweetToNeo4JHandler neo4jHandler;
	
	/**
	 * Maximum amount of users that are analyzed.
	 */
	private int userLimit = 100000;
	
	/**
	 * Analyze only the most recent tweets to keep performance manageable.
	 */
	private int latestTweetsLimit = 100;
	
	
	public TopicAnalyzer(ServerConfig config, TweetToNeo4JHandler neo4jHandler) {
		this.mongodb = new MongoDatabase(config);
		this.neo4jHandler = neo4jHandler;
	}
	
	/**
	 * Analyze tweets and store topics that are seen as interesting in Neo4J db
	 * 
	 * @throws UnknownHostException
	 */
	public void analyzeTweets() throws UnknownHostException {
		long time = System.currentTimeMillis();
		List<String> topics = mongodb.readAllTopicsInLowercase();
		List<Long> userIds = mongodb.readUserIds(userLimit); //TODO SLOW
		TopicTweetsMiner miner = new TopicTweetsMiner(topics);
		String bigTweet = null;
		
		for(int i=0; i<userIds.size(); i++) {
			bigTweet = mongodb.readLatestTweetsAsOneString(userIds.get(i), latestTweetsLimit);

			List<String> interests = miner.getInterestedTopics(bigTweet);
			
			for(int j=0; j<interests.size(); j++) {
				neo4jHandler.addMentionsRelationship(userIds.get(i), interests.get(j));
			}
		}
		
		System.out.println("mining done. processing time: "+(System.currentTimeMillis()-time)+"ms for "+userIds.size()+" users.");
	}

	@Override
	public void run() {
		try {
			this.analyzeTweets();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
