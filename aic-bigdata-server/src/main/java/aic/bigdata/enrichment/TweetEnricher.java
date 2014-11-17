package aic.bigdata.enrichment;

/**
 * Enrich tweets with ads that match the topics stored in MongoDB.
 * This is how it works:
 * 1. Topics and Ads are stored in two separate Collections inside the MongoDB database. 
 * 		This is done for performance purposes, since looking for the corresponding topics inside every ad entry would take too long.
 * 		The Topics collection as its key-value-pairs the name of the topic (as key) and the reference id to the ad entry (value)
 * 		The Ads collection stores id numbers as its keys and a number of different properties (name, company, campaign, text, topic, credits, picture) 
 * 		as its values with some them of being optional.
 * 2. After an ad is added to the Ads collection, [FILL IN CLASS/METHOD] checks, whether the topic exists in the Topics collection or not.
 * 		2a. If the topic already exists, the id of the ad is added to the corresponding topics
 * 		2b. If the topic doesn't/ exist, a new topic entries are created
 * 3. The Enricher now looks periodically for ads that fit the user profile by analyzing tweets of users and matching them against the topics available.
 * 		(Specify how)
 * 4. If a topic matches, it will look for the corresponding ads by matching the reference id in the topic entry to the ad entry.
 * 5. With the ad reference id the corresponding ad is fetched from the Ads collection (and the ad shown to the user).
 * 6. Additionally, the credit value is decremented by 1.
 * 		6a. If the credit count hits 0, the ad entry is deleted from the Topics collection, by deleting the reference id
 * (Optionally 7. Credits can be re-filled. In that case, the credit value is changed and the ad entry added back to the topic entry)
 */
public class TweetEnricher {
	
}
