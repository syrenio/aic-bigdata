package aic.bigdata.extraction;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.Neo4JBatchInserter;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.enrichment.AdsTopicsToDatabaseFiller;
import aic.bigdata.extraction.handler.MentionsInfoToNeo4JHandler;
import aic.bigdata.extraction.handler.RetweetingInfoToNeo4JHandler;
import aic.bigdata.extraction.handler.TopicToNeo4JHandler;
import aic.bigdata.extraction.handler.UserToNeo4JHandler;
import aic.bigdata.extraction.provider.MongoDbMentionsInfoProvider;
import aic.bigdata.extraction.provider.MongoDbRetweetingInfoProvider;
import aic.bigdata.extraction.provider.MongoDbTopicProvider;
import aic.bigdata.extraction.provider.SqlDbUserProvider;
import aic.bigdata.server.ServerConfig;

public class Neo4JAnalysisRunner {

	private static ServerConfig config;
	static {
		config = new ServerConfigBuilder().getConfig();
	}

	private static void FillAdsTopicDatabase(GraphDatabase neo) {
		AdsTopicsToDatabaseFiller filler = new AdsTopicsToDatabaseFiller(config, neo);
		try {
			filler.fillDatabase();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void DeleteSampleAdsTopics() {
		MongoDatabase b = new MongoDatabase(config);
		try {
			b.removeAdsTopics();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		// delete and recreate Topics and Ads
		DeleteSampleAdsTopics();
		FillAdsTopicDatabase(GraphDatabase.getInstance());
		GraphDatabase.closeInstance(); // close for the BatchInserter later...

		MongoDatabase mongoDb = new MongoDatabase(config);
		SqlDatabase sqlDb = new SqlDatabase(config);
		// SqlDatabase sqlDb = new SqlDatabase(config);

		Neo4JBatchInserter batchInserter = new Neo4JBatchInserter(config);

		// users
		System.out.println("Creating User Nodes...");
		UserProvider userProvider = new SqlDbUserProvider(sqlDb); // new
																	// SqlUserProvider(sqlDb);
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
