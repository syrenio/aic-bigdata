package aic.bigdata.extraction.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.extraction.RetweetingInfoHandler;
import aic.bigdata.server.ServerConfig;

public class RetweetingInfoToNeo4JHandler implements RetweetingInfoHandler {
	private int tweetsLogged = 0;
	private GraphDatabaseService graphDb;
	private Index<Node> userIndex;
	private Index<Node> topicIndex;
	private ServerConfig config;
	private ExecutionEngine cypherEngine;
	private Neo4JBatchInserter batchInserter;

	public RetweetingInfoToNeo4JHandler(ServerConfig config, Neo4JBatchInserter batchInserter) {
		this.config = config;
		this.batchInserter = batchInserter;
	}
	
	@Override
	public void HandleOriginalAuthors(Long id, List<Long> originalAuthors) {
		Map<Long, Integer> frequencies = new HashMap<Long, Integer>();

		// count occurences for all original author ids
		for (Long l : originalAuthors) {
			if (frequencies.containsKey(l)) {
				frequencies.put(l, frequencies.get(l)+1);
			}
			else {
				frequencies.put(l, 1);
			}
		}

		// insert edges
		for (Long oaid : frequencies.keySet()) {
			batchInserter.addRetweetsRelationship(id, oaid, frequencies.get(oaid));
		}
	}
}
