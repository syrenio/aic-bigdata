package aic.bigdata.extraction;

import java.net.UnknownHostException;

import aic.bigdata.database.model.AicUser;

public interface UserHandler {
	public void HandleUser(AicUser user) throws UnknownHostException;
}
