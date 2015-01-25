package aic.bigdata.extraction.handler;

import org.neo4j.cypher.javacompat.ExecutionEngine;
//import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.extraction.MentionsInfoHandler;
import aic.bigdata.server.ServerConfig;

public class MentionsInfoToNeo4JHandler implements MentionsInfoHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private Index<Node> topicIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;
	private Neo4JBatchInserter batchInserter;

	public MentionsInfoToNeo4JHandler(ServerConfig config, Neo4JBatchInserter batchInserter) {
		this.config = config;
		this.batchInserter = batchInserter;
	}
	
	@Override
	public void HandleTopic(Long id, String topic, Integer count) {
		batchInserter.addMentionsRelationship(id, topic, count);
	}
}
