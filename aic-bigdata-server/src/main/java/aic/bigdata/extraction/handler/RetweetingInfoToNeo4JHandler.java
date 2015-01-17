package aic.bigdata.extraction.handler;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.extraction.RetweetingInfoHandler;
import aic.bigdata.server.ServerConfig;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.IDs;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;




//import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Transaction;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.ResourceIterator;

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
