package aic.bigdata.extraction;

import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.extraction.handler.RetweetingInfoToNeo4JHandler;
import aic.bigdata.extraction.handler.MentionsInfoToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToNeo4JHandler;
import aic.bigdata.extraction.handler.TopicToNeo4JHandler;
import aic.bigdata.extraction.provider.MongoDbRetweetingInfoProvider;
import aic.bigdata.extraction.provider.MongoDbMentionsInfoProvider;
import aic.bigdata.extraction.provider.MongoDbUserProvider;
import aic.bigdata.extraction.provider.MongoDbTopicProvider;
import aic.bigdata.extraction.provider.SqlUserProvider;
import aic.bigdata.extraction.UserProvider;
import aic.bigdata.extraction.TopicProvider;
import aic.bigdata.extraction.RetweetingInfoHandler;
import aic.bigdata.extraction.MentionsInfoHandler;
import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TwitterStreamJob;

import java.sql.SQLException;

public class Neo4JRetweetingInfoExtractionRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) throws SQLException {
		MongoDatabase mongoDb = new MongoDatabase(config);
		//SqlDatabase sqlDb = new SqlDatabase(config);

		Neo4JBatchInserter batchInserter = new Neo4JBatchInserter(config);

		// users
		System.out.println("Creating User Nodes...");
		UserProvider userProvider = new MongoDbUserProvider(mongoDb); //new SqlUserProvider(sqlDb);
		userProvider.addHandler(new UserToNeo4JHandler(config, batchInserter));
		userProvider.run();

		// a retweets b
		System.out.println("Creating retweets Edges...");
		MongoDbRetweetingInfoProvider riProvider = new MongoDbRetweetingInfoProvider(mongoDb);
		riProvider.addHandler(new RetweetingInfoToNeo4JHandler(config, batchInserter));
		riProvider.run();

		// topics
		System.out.println("Creating Topic Nodes...");
		TopicProvider tProvider = new MongoDbTopicProvider(mongoDb);
		tProvider.addHandler(new TopicToNeo4JHandler(config, batchInserter));
		tProvider.run();

		// a mentions b
		System.out.println("Creating mentions Edges...");
		MentionsInfoProvider mProvider = new MongoDbMentionsInfoProvider(mongoDb);
		mProvider.addHandler(new MentionsInfoToNeo4JHandler(config, batchInserter));
		mProvider.run();
	}
}
