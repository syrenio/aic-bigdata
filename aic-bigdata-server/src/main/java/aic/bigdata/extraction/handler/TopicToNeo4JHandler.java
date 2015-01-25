package aic.bigdata.extraction.handler;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.extraction.TopicHandler;
import aic.bigdata.server.ServerConfig;
//import org.neo4j.graphdb.RelationshipType;

public class TopicToNeo4JHandler implements TopicHandler {
	private ServerConfig config;
	private Neo4JBatchInserter batchInserter;

	public TopicToNeo4JHandler(ServerConfig config, Neo4JBatchInserter batchInserter) {
		this.config = config;
		this.batchInserter = batchInserter;
	}
	
	@Override
	public void HandleTopic(String topic) {
		batchInserter.addTopic(topic);
	}

}
