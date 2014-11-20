package aic.bigdata.enrichment;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a list of topics, find all topics that an user is interested in based on his/her tweets content.
 * 
 */
public class TopicTweetsMiner {
	private List<String> topics;
	
	/* 
	 * Any time the topic surpasses or matches the interest threshold, 
	 * it can be assumed that the user is interested in that topic.
	 */
	private Integer interestThreshold = 3;
	private Integer weight = 1;

	public TopicTweetsMiner(List<String> topics) {
		this.topics = topics;
	}
	
	public List<String> getInterestedTopics(List<String> tweetsText) {
		return this.mineTweetsContents(tweetsText);
	}
	
	private List<String> mineTweetsContents(List<String> tweetsText) {
		List<String> interestedTopics = new ArrayList<String>();
		
		//concatenate all tweets to one long string -> performance win?
		String bigTweet = "";

		for(int i=0; i<tweetsText.size(); i++) {
			bigTweet += tweetsText.get(i);
		}
		
		//count how many times topics appears in the concatenated tweet
		
		for(int i=0; i<topics.size(); i++) {
			//TODO how to get the amount of times a topic appears in text
			
			//topic matches
			if(true) {
				Integer count = 0;
				count =+ this.weight;

				//if topic count surpasses the interest threshold, it's concluded that user is interested in topic
				//thus stop counting and go to next topic
				if(count >= interestThreshold) {
					interestedTopics.add(topics.get(i));
					continue;
				}
			}
		}
		
		return interestedTopics;
	}
}
