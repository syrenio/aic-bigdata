package aic.bigdata.extraction.handler;

import java.net.UnknownHostException;
import java.sql.SQLException;

import twitter4j.Status;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.TweetHandler;

public class UserToDBHandler implements TweetHandler {

	private SqlDatabase db;

	public UserToDBHandler(SqlDatabase b) {
		db = b;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) throws UnknownHostException {

		AicUser usr = new AicUser(status.getUser());
		internalHandleTweet(usr);
	}

	@Override
	public void HandleTweet(String tweet) {
		// TODO Auto-generated method stub

	}

	private void internalHandleTweet(AicUser user) {
		try {

			this.db.createUser(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
