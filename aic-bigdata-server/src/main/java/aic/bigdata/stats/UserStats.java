package aic.bigdata.stats;

import java.sql.SQLException;

import aic.bigdata.database.SqlDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

public class UserStats {
	private static ServerConfig config;

	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) {
		try {
			SqlDatabase db = new SqlDatabase(config);
			System.out.println("Users count: " + db.getUserCount());

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
