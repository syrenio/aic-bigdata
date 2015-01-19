package aic.bigdata.extraction;

import java.net.UnknownHostException;

public interface MentionsInfoHandler {
	public void HandleTopic(Long id, String topic, Integer count) throws UnknownHostException;
}
