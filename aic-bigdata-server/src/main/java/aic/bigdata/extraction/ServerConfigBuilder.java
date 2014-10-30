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
		try {
			InputStream isTwitter =  getClass().getClassLoader().getResourceAsStream("twitter.properties");
			propsTwitter.load(isTwitter);
			InputStream isServer = getClass().getClassLoader().getResourceAsStream("server.properties");
			propsServer.load(isServer);
		} catch (IOException e) {
			System.err.println("could not load twitter properties");
			e.printStackTrace();
		}
		ServerConfig s = new ServerConfig();
		s.setServer(propsServer);
		s.setTwitter(propsTwitter);
		return s;
	}

}
