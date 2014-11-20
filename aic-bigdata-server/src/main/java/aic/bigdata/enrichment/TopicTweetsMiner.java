package aic.bigdata.enrichment;

import java.util.ArrayList;
import java.util.List;


/**
 * Given a list of topics, find all topics that an user is interested in based on 
 * his/her tweets content.
 * 
 */
public class TopicTweetsMiner {
	private List<String> topics;
	
	/* 
	 * Number of times a topic word has to appear in tweets to 
	 * be considered as interesting for user
	 */
	private Integer interestThreshold = 2;

	public TopicTweetsMiner(List<String> topics) {
		this.topics = topics;
	}
	
	//additional method, in case there is some other mining/processing stuff to do
	public List<String> getInterestedTopics(String concatTweet) {
		return this.mineTweetsContents(concatTweet);
	}
	
	private List<String> mineTweetsContents(String concatTweet) {
		List<String> interestedTopics = new ArrayList<String>();

		for(int i=0; i<topics.size(); i++) {
			String substrRem = concatTweet;
			String substrFirst = "";
			int count = 0;
			int lastIndex = concatTweet.indexOf(topics.get(i));
			
			while(lastIndex != -1 && count<interestThreshold) {
				substrFirst = substrRem.substring(0, lastIndex);
				substrRem = substrRem.substring(lastIndex+topics.get(i).length());

				lastIndex = substrRem.indexOf(topics.get(i));
				
				boolean isStartLetter = false;
				boolean isEndLetter = false;
				
				//check, if letter after and before the match is another letter
				//done to avoid matching similar words (e.g. searching for 'car' and getting 'care')
				if(substrRem.length()>0 && Character.isLetter(substrRem.charAt(0))) {
					isEndLetter = true;
				}
				
				if(substrFirst.length()>0 && Character.isLetter(substrFirst.charAt(substrFirst.length()-1))) {
					isStartLetter = true;
				}
					
				if(!isStartLetter && !isEndLetter) {
					count++;
				}
			}
			
			if(count >= interestThreshold) {
				interestedTopics.add(topics.get(i));
				//System.out.println("user is interested in: "+topics.get(i)+" *** all latest tweets: "+concatTweet);
				//System.out.println("----------------");
			}
			
		}
		
		return interestedTopics;
	}
}
