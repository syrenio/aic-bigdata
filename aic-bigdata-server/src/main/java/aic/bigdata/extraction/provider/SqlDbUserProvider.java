package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.UserHandler;
import aic.bigdata.extraction.UserProvider;

import com.j256.ormlite.dao.CloseableIterator;

public class SqlDbUserProvider implements UserProvider {

	private SqlDatabase db;
	private List<UserHandler> handler = new ArrayList<UserHandler>();
	private boolean running;

	public SqlDbUserProvider(SqlDatabase db) {
		this.db = db;
	}

	private void printTimeCount(DateTime start, long count, long max) {

		DateTime end = new DateTime();
		Duration diff = new Duration(start, end);
		System.out.println("Current User Count: " + count + " / " + max + " Minutes:" + diff.getStandardMinutes());

	}

	@Override
	public void run() {
		this.running = true;
		long stepSize = 100000;

		try {
			long userCount = db.getUserCount();
			long processedUsers = 0;

			CloseableIterator<AicUser> iterator = db.getUsersIterator();
			DateTime begin = new DateTime();
			System.out.println("SqlDbUserProvider starting ... " + begin);
			try {
				while (iterator.hasNext()) {
					AicUser user = iterator.next();
					if (!running)
						break;
					for (UserHandler t : this.handler) {
						try {
							t.HandleUser(user);
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						processedUsers++;

						if (processedUsers % stepSize == 0)
							printTimeCount(begin, processedUsers, userCount);
					}
				}
			} finally {
				// close it at the end to close underlying SQL statement
				iterator.close();
			}

			Duration diff = new Duration(begin, new DateTime());
			System.out.println("SqlDbUserProvider ended ... " + (new DateTime()));
			System.out.println("Current User Count: " + processedUsers + " / " + userCount + " Minutes:"
					+ diff.getStandardMinutes());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stopProvider() {
		this.running = false;
	}

	@Override
	public void addHandler(UserHandler t) {
		this.handler.add(t);
	}

}
