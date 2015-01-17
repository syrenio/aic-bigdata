package aic.bigdata.extraction;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.handler.RetweetingInfoToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToNeo4JHandler;
import aic.bigdata.extraction.provider.MongoDbRetweetingInfoProvider;
import aic.bigdata.extraction.provider.MongoDbUserProvider;
import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TwitterStreamJob;

public class Neo4JRetweetingInfoExtractionRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) {
		MongoDatabase b = new MongoDatabase(config);

		MongoDbUserProvider userProvider = new MongoDbUserProvider(b);

		userProvider.addHandler(new UserToNeo4JHandler(config, new Neo4JBatchInserter(config)));

		userProvider.run();

/*
		MongoDbRetweetingInfoProvider provider = new MongoDbRetweetingInfoProvider(b);

		provider.addHandler(new RetweetingInfoToNeo4JHandler(config, GraphDatabase.getInstance()));

		provider.run();
*/

	}

}
