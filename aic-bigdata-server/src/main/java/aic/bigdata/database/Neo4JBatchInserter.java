package aic.bigdata.database;

import aic.bigdata.server.ServerConfig;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.BatchInserter;

import aic.bigdata.database.model.AicUser;

import java.util.Map;
import java.util.HashMap;

public class Neo4JBatchInserter {
	private BatchInserter batchInserter;
	private Label userLabel;
	private Label retweetsLabel;
	private Label topicLabel;
	private Label mentionsLabel;
	private Map<Long, Long> twitterToNeo; // mapping ids
	private Map<String, Long> topicToNeo; // mapping topics

	enum Type implements RelationshipType {
		retweets,
		mentions
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
		topicLabel = DynamicLabel.label("topic");
		mentionsLabel = DynamicLabel.label("mentions");

		twitterToNeo = new HashMap<Long, Long>();
		topicToNeo = new HashMap<String, Long>();
	}

	public void addUser(AicUser user) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", user.getId());
		properties.put("name", user.getName());
		properties.put("favouritesCount", user.getFavouritesCount());
		properties.put("followersCount", user.getFollowersCount());
		properties.put("friendsCount", user.getFriendsCount());
		//properties.put("screenName", user.getScreenName());

		long neoId = batchInserter.createNode(properties, userLabel); // neo4j's internal id != user.getId()
		twitterToNeo.put(user.getId(), neoId);
	}

	public void addRetweetsRelationship(Long retweeterId, Long originalAuthorId, Integer count) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("count", count);

		boolean retweeterUnknown = twitterToNeo.get(retweeterId) == null;
		boolean originalAuthorUnknown = twitterToNeo.get(originalAuthorId) == null;
		if (retweeterUnknown) {
			//System.err.println("no mapping for retweeter " + retweeterId);
		}
		if (originalAuthorUnknown) {
			//System.out.println("no mapping for original author " + originalAuthorId + " (that's okay, it's probably just a user outside of our set)");
		}
		if (!retweeterUnknown && !originalAuthorUnknown) {
			long _id = batchInserter.createRelationship(twitterToNeo.get(retweeterId), twitterToNeo.get(originalAuthorId), Type.retweets, properties);
		}
	}

	public void addTopic(String topic) {
		topic = topic.toLowerCase();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", topic);

		long neoId = batchInserter.createNode(properties, topicLabel);
		topicToNeo.put(topic, neoId);
	}

	public void addMentionsRelationship(Long id, String topic, Integer count) {
		topic = topic.toLowerCase();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("count", count);

		boolean userUnknown = twitterToNeo.get(id) == null;
		boolean topicUnknown = topicToNeo.get(topic) == null;

/*		if (userUnknown)
			System.out.println("Neo4JBatchInserter: Unknown user #" + id);
		if (topicUnknown)
			System.out.println("Neo4JBatchInserter: Unknown topic '" + topic + "'");
*/
		if (!userUnknown && !topicUnknown) {
			//System.out.println("Neo4JBatchInserter: Adding (user #" + id + ")-[mentions]->(topic '" + topic + "')");
			long _id = batchInserter.createRelationship(twitterToNeo.get(id), topicToNeo.get(topic), Type.mentions, properties);
		}
	}
}
