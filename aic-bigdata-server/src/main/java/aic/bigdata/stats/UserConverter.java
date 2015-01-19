package aic.bigdata.stats;

import java.net.UnknownHostException;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

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

	// private static void convertUsersFromUserCollection(SqlDatabase db,
	// MongoDatabase mongo) throws SQLException {
	//
	// try {
	// DBCursor cur = mongo.getCursorForUsers();
	// int max = cur.size();
	// int step = max / 10;
	// int count = 0;
	//
	// System.out.println("Current user count: " + db.getUserCount());
	// System.out.print("Progress: ");
	// MongoDatabaseHelper help = new MongoDatabaseHelper();
	// while (cur.hasNext()) {
	// DBObject o = cur.next();
	// User usr = null;
	// // if (o.containsField("followersCount")) {
	// // usr = g.fromJson(o.toString(), User.class);
	// // } else {
	// // usr = TwitterObjectFactory.createUser(o.toString());
	// // }
	// usr = help.convertToUser(o);
	// AicUser x = new AicUser(usr);
	// db.createUser(x);
	// count++;
	// if (count % step == 0)
	// System.out.print("#");
	// }
	// System.out.println();
	// System.out.println("Current user count: " + db.getUserCount());
	//
	// } catch (UnknownHostException | TwitterException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	private static void convertUsersFromTweetCollection(SqlDatabase db, MongoDatabase mongo) throws SQLException {

		try {
			DBCursor cur = mongo.getCursorForTweets();
			int max = cur.size();
			int step = max / 10;
			int count = 0;

			System.out.println("Current user count: " + db.getUserCount());
			System.out.print("Progress: ");
			MongoDatabaseHelper help = new MongoDatabaseHelper();
			while (cur.hasNext()) {
				DBObject o = cur.next();
				User usr = null;
				if (o.containsField("user")) {
					DBObject dbo = (DBObject) o.get("user");
					usr = help.convertToUser(dbo);
					AicUser x = new AicUser(usr);
					db.createUser(x);
				}

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
	}

	public static void main(String[] args) {
		SqlDatabase db;
		try {
			db = new SqlDatabase(config);
			MongoDatabase mongo = new MongoDatabase(config);

			org.joda.time.DateTime start = new DateTime();

			// convertUsersFromUserCollection(db, mongo);

			convertUsersFromTweetCollection(db, mongo);

			org.joda.time.DateTime end = new DateTime();

			Minutes minutesBetween = Minutes.minutesBetween(start, end);

			System.out.println("Minutes: " + minutesBetween.getMinutes());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("something went wrong in sql database");
		}

		System.out.println("task finished");

	}

}
