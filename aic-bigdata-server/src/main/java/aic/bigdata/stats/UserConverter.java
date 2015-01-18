package aic.bigdata.stats;

import java.net.UnknownHostException;
import java.sql.SQLException;

import twitter4j.TwitterException;
import twitter4j.User;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.MongoDatabaseHelper;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class UserConverter {
	private static ServerConfig config;

	static {
		config = new ServerConfigBuilder().getConfig();
	}

	public static void main(String[] args) {
		SqlDatabase db;
		try {
			db = new SqlDatabase(config);
			MongoDatabase mongoDatabase = new MongoDatabase(config);
			try {

				DBCursor cur = mongoDatabase.getCursorForUsers();
				int max = cur.size();
				int step = max / 10;
				int count = 0;

				System.out.println("Current user count: " + db.getUserCount());
				System.out.print("Progress: ");
				MongoDatabaseHelper help = new MongoDatabaseHelper();
				while (cur.hasNext()) {
					DBObject o = cur.next();
					User usr = null;
					// if (o.containsField("followersCount")) {
					// usr = g.fromJson(o.toString(), User.class);
					// } else {
					// usr = TwitterObjectFactory.createUser(o.toString());
					// }
					usr = help.convertToUser(o);
					AicUser x = new AicUser(usr);
					db.createUser(x);
					count++;
					if (count % step == 0)
						System.out.print("#");
				}
				System.out.println();
				System.out.println("Current user count: " + db.getUserCount());

			} catch (UnknownHostException | TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// db.getAllUsers();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("something went wrong in sql database");
		}

		System.out.println("task finished");

	}

}
