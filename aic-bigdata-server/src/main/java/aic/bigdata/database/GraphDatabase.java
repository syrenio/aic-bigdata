package aic.bigdata.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;

import sun.security.jca.GetInstance;
import twitter4j.User;
import aic.bigdata.server.ServerConfig;

public class GraphDatabase {

	private static GraphDatabase isntance;
	
	public static GraphDatabase getSingleton(ServerConfig config){
		if(isntance == null)
			isntance = new GraphDatabase(config);
		return isntance;
	}
	
	
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private Index<Node> topicIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;

	final static private String getMentionedTopicsQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE u.userId = {userId} return t.topic";
	final static private String getUsersMentioningQ = "MATCH (u:user)-[r:mentions]->(t:topic) WHERE t.topic = {topic} return u.userId";

	public GraphDatabase(ServerConfig config) {
		this.config = config;

		try {
			createDb(config.getNeo4jFullDbName());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		IndexManager indexManager = graphDb.index();

		try (Transaction tx = graphDb.beginTx()) {
			userIndex = indexManager.forNodes("users");

			tx.success();
		}

		try (Transaction tx = graphDb.beginTx()) {
			topicIndex = indexManager.forNodes("topics");

			tx.success();
		}

		cypherEngine = new ExecutionEngine(graphDb);
	}

	private void createDb(String fullName) throws IOException {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(fullName).newGraphDatabase();
		//graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(fullName);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public Set<String> getMentionedTopics(User user) {
		return getMentionedTopics(user.getId());
	}

	public Set<String> getMentionedTopics(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		ExecutionResult result;

		HashSet<String> topics = new HashSet<String>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getMentionedTopicsQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				topics.add((String) map.get("t.topic"));
			}
		}

		return topics;
	}

	public Set<Long> getUsersMentioning(String topic) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topic", topic);

		ExecutionResult result;

		HashSet<Long> ids = new HashSet<Long>();

		try (Transaction ignoreMe = graphDb.beginTx()) {
			result = cypherEngine.execute(getUsersMentioningQ, params);
			ResourceIterator<Map<String, Object>> iterator = result.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = iterator.next();
				ids.add((Long) map.get("u.userId"));
			}
		}

		return ids;
	}
}
