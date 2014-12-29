package aic.bigdata.stats;

import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

public class UserStats {
	private static ServerConfig config;

	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) {
	}

}
