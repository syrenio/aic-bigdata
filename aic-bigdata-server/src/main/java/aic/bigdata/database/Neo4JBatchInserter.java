package aic.bigdata.database;

import aic.bigdata.server.ServerConfig;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.BatchInserter;

import twitter4j.User;

import java.util.Map;
import java.util.HashMap;

public class Neo4JBatchInserter {
	private BatchInserter batchInserter;
	private Label userLabel;
	private Label retweetsLabel;
	private Map<Long, Long> twitterToNeo; // mapping ids

	enum OneType implements RelationshipType {
		retweets
	}

	public Neo4JBatchInserter(ServerConfig config) {
		batchInserter = BatchInserters.inserter(config.getNeo4jFullDbName());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				batchInserter.shutdown();
			}
		});

		userLabel = DynamicLabel.label("user");
		retweetsLabel = DynamicLabel.label("retweets");

		twitterToNeo = new HashMap<Long, Long>();
	}

	public void addUser(User user) {
//		Node n = batchDb.createNode(userLabel);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", user.getId());
		properties.put("name", user.getName());
		properties.put("favouritesCount", user.getFavouritesCount());
		/*properties.put("followersCount", user.getFollowersCount());
		properties.put("friendsCount", user.getFriendsCount());
		properties.put("screenName", user.getScreenName());*/

		//batchInserter.createNode(user.getId(), properties, userLabel); // neo4j's internal id == user.getId()
		long neoId = batchInserter.createNode(properties, userLabel); // neo4j's internal id != user.getId()
		twitterToNeo.put(user.getId(), neoId);
	}

	public void addRetweetsRelationship(Long retweeterId, Long originalAuthorId, Integer count) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("count", count);

		//long _id = batchInserter.createRelationship(retweeterId, originalAuthorId, OneType.retweets, properties);
		boolean retweeterUnknown = twitterToNeo.get(retweeterId) == null;
		boolean originalAuthorUnknown = twitterToNeo.get(originalAuthorId) == null;
		if (retweeterUnknown) {
			//System.err.println("no mapping for retweeter " + retweeterId);
		}
		if (originalAuthorUnknown) {
			//System.out.println("no mapping for original author " + originalAuthorId + " (that's okay, it's probably just a user outside of our set)");
		}
		if (!retweeterUnknown && !originalAuthorUnknown) {
			long _id = batchInserter.createRelationship(twitterToNeo.get(retweeterId), twitterToNeo.get(originalAuthorId), OneType.retweets, properties);
		}
	}
}
