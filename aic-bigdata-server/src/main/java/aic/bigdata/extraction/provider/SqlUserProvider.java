package aic.bigdata.extraction.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import aic.bigdata.database.model.AicUser;

import aic.bigdata.database.SqlDatabase;
import aic.bigdata.extraction.UserHandler;
import aic.bigdata.extraction.UserProvider;

import java.sql.SQLException;

public class SqlUserProvider implements UserProvider {

	private SqlDatabase db;
	private List<UserHandler> handler = new ArrayList<UserHandler>();
	private boolean running;

	public SqlUserProvider(SqlDatabase db) {
		this.db = db;
	}

	@Override
	public void run() {
		DateTime begin = new DateTime();
		this.running = true;
		long counter = 0;
		long stepCounter = 0;
		long stepSize = 1000;

		try {
			for (AicUser user : db.getAllUsers()) {
				if (!running)
					break;

				if (stepCounter >= stepSize) {
					DateTime end = new DateTime();
					stepCounter = 0;
					counter++;
					Duration diff = new Duration(begin, end);
					System.out.println("Current User Count: " + (counter * stepSize) + " Minutes:"
							+ diff.getStandardMinutes());
				}

				for (UserHandler t : this.handler) {
					t.HandleUser(user);
				}
				stepCounter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
