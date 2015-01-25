package aic.bigdata.stats;

import java.sql.SQLException;

import aic.bigdata.database.SqlDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

public class UserCleaner {

	private static ServerConfig config;

	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) {
		SqlDatabase db;
		try {
			db = new SqlDatabase(config);
			db.recreateTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
