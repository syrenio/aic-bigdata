package aic.bigdata.extraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import aic.bigdata.server.*;

public class ExtractionRunner {
	
	private static TweetProvider CreateTweetProviderForTwitterExtraction()
	{
		ServerConfig serverConfig = new ServerConfigBuilder().getConfig();
		TwitterStreamJob j = new TwitterStreamJob(serverConfig);
		return j;
	}
	
	public static void main(String[] args)
	{		
		TweetProvider p = CreateTweetProviderForTwitterExtraction();
		p.addTweetHandler(new TweetToConsolePrinter());
		p.run();
		System.out.print(p.toString()+"hi");
	}

}
