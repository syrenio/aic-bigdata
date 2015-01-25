package aic.bigdata.extraction.handler;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.UserHandler;
import aic.bigdata.server.ServerConfig;
//import org.neo4j.graphdb.RelationshipType;

public class UserToNeo4JHandler implements UserHandler {
	private ServerConfig config;
	private Neo4JBatchInserter batchInserter;

	public UserToNeo4JHandler(ServerConfig config, Neo4JBatchInserter batchInserter) {
		this.config = config;
		this.batchInserter = batchInserter;
	}
	
	@Override
	public void HandleUser(AicUser user) {
		batchInserter.addUser(user);
	}

}
