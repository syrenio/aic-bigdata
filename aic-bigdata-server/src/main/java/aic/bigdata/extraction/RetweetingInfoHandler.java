package aic.bigdata.extraction;

import java.net.UnknownHostException;

import java.util.List;

public interface RetweetingInfoHandler {
	public void HandleOriginalAuthors(Long id, List<Long> originalAuthors) throws UnknownHostException;
//	public void HandleRetweeters(Long id, List<Long> retweeters) throws UnknownHostException;
}
