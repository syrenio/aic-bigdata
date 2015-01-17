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
		Neo4JBatchInserter batchInserter = new Neo4JBatchInserter(config);

		// users
		MongoDbUserProvider userProvider = new MongoDbUserProvider(b);
		userProvider.addHandler(new UserToNeo4JHandler(config, batchInserter));
		userProvider.run();

		// a retweets b
		MongoDbRetweetingInfoProvider riProvider = new MongoDbRetweetingInfoProvider(b);
		riProvider.addHandler(new RetweetingInfoToNeo4JHandler(config, batchInserter));
		riProvider.run();
	}

}
