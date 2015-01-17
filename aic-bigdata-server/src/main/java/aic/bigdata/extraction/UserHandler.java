package aic.bigdata.extraction;

import java.net.UnknownHostException;

import twitter4j.User;

public interface UserHandler {
	public void HandleUser(User user) throws UnknownHostException;
}
