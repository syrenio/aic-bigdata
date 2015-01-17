package aic.bigdata.database;

import aic.bigdata.server.ServerConfig;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import twitter4j.User;

public class Neo4JBatchInserter {
	private GraphDatabaseService batchDb;
	private Label userLabel;

	public Neo4JBatchInserter(ServerConfig config) {
		batchDb = BatchInserters.batchDatabase(config.getNeo4jFullDbName());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				batchDb.shutdown();
			}
		});

		userLabel = DynamicLabel.label("user");
	}

	public void addUser(User user) {
		Node n = batchDb.createNode(userLabel);
		n.setProperty("id", user.getId());
		n.setProperty("name", user.getName());
		n.setProperty("favouritesCount", user.getFavouritesCount());
		n.setProperty("followersCount", user.getFollowersCount());
		n.setProperty("friendsCount", user.getFriendsCount());
		n.setProperty("screenName", user.getScreenName());
	}
}
