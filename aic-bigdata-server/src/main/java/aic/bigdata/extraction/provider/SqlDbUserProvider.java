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

public class SqlDbUserProvider implements UserProvider {

	private SqlDatabase db;
	private List<UserHandler> handler = new ArrayList<UserHandler>();
	private boolean running;

	public SqlDbUserProvider(SqlDatabase db) {
		this.db = db;
	}

	@Override
	public void run() {
		DateTime begin = new DateTime();
		this.running = true;
		long counter = 0;
		long stepSize = 1000;

		try {
			long userCount = db.getUserCount();
			long processedUsers = 0;
			while (processedUsers <= userCount) {

				for (AicUser user : db.getUsers(counter, stepSize)) {
					if (!running)
						break;
					for (UserHandler t : this.handler) {
						try {
							t.HandleUser(user);
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
					}
				}
				DateTime end = new DateTime();
				counter++;
				Duration diff = new Duration(begin, end);
				System.out.println("Current User Count: " + (counter * stepSize) + " Minutes:"
						+ diff.getStandardMinutes());

				processedUsers += stepSize;
			}
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
