package aic.bigdata.enrichment;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.server.ServerConfig;

/**
 * Determines topics that the user is interested in and stores them in Neo4J.
 */
public class TopicAnalyzer implements Runnable {

	private MongoDatabase mongodb;
	private GraphDatabase graphDB;

	private boolean running = false;

	public TopicAnalyzer(ServerConfig config, GraphDatabase graphDB) {
		this.mongodb = new MongoDatabase(config);
		this.graphDB = graphDB;
	}

	/**
	 * Analyze tweets and store topics that are seen as interesting in Neo4J db
	 * 
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	public void analyzeTweets() throws UnknownHostException, SQLException {
		System.out.println("analyzing tweets...");
		this.running = true;
		
		long time = System.currentTimeMillis();
		
		HashMap<Long, String> tweets = mongodb.getConcatTweets();
		List<String> topics = mongodb.readAllTopicsInLowercase();
		TopicTweetsMiner miner = new TopicTweetsMiner(topics);
		
		Iterator<Long> it = tweets.keySet().iterator();
		
		while(it.hasNext() && running) {
			Long id = it.next();
			String tweet = tweets.get(id);
			
			List<String> interests = miner.getInterestedTopics(tweet);
			
			for (int i = 0; i < interests.size(); i++) {
				graphDB.addMentionsRelationship(id, interests.get(i));
			}
		}
		
		/*List<String> topics = mongodb.readAllTopicsInLowercase();
		List<Long> userIds = sqldb.getUserIds(userLimit);
		TopicTweetsMiner miner = new TopicTweetsMiner(topics);
		String bigTweet = null;

		for (int i = 0; i < userIds.size() && running; i++) {
			bigTweet = mongodb.readLatestTweetsAsOneString(userIds.get(i), latestTweetsLimit);

			List<String> interests = miner.getInterestedTopics(bigTweet);

			for (int j = 0; j < interests.size(); j++) {
				graphDB.addMentionsRelationship(userIds.get(i), interests.get(j));
			}
		}*/

		double totalTime = (System.currentTimeMillis() - time)/1000;
		System.out.println("mining done. processing time: " + totalTime + "s.");
	}

	@Override
	public void run() {
		try {
			this.analyzeTweets();
		} catch (UnknownHostException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopAnalyze() {
		this.running = false;
	}
}
