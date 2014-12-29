package aic.bigdata.extraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import aic.bigdata.server.ServerConfig;

public class ServerConfigBuilder {
	
	public ServerConfig getConfig()
	{
		Properties propsTwitter = new Properties();
		Properties propsServer = new Properties();
		Properties propsMongo = new Properties();
		Properties propsNeo4J = new Properties();
		Properties propsSql = new Properties();
		try {
			InputStream isTwitter =  getClass().getClassLoader().getResourceAsStream("twitter.properties");
			propsTwitter.load(isTwitter);
			InputStream isServer = getClass().getClassLoader().getResourceAsStream("server.properties");
			propsServer.load(isServer);
			InputStream isMongo =getClass().getClassLoader().getResourceAsStream("mongo.properties");
			propsMongo.load(isMongo);
			InputStream isNeo4J =getClass().getClassLoader().getResourceAsStream("neo4j.properties");
			propsNeo4J.load(isNeo4J);
			InputStream isSql =getClass().getClassLoader().getResourceAsStream("sql.properties");
			propsSql.load(isSql);
		} catch (IOException e) {
			System.err.println("could not load twitter properties");
			e.printStackTrace();
		}
		ServerConfig s = new ServerConfig();
		s.setServer(propsServer);
		s.setTwitter(propsTwitter);
		s.setMongo(propsMongo);
		s.setNeo4J(propsNeo4J);
		s.setSql(propsSql);
		return s;
	}

}
