package aic.bigdata.enrichment;

import java.util.ArrayList;
import java.util.List;


public class TopicObject {
	private String id;
	
	private List<Integer> ads;
	
	public TopicObject() {
		this.ads = new ArrayList<Integer>();
	}
	
	public TopicObject(String id, List<Integer> ads) {
		this.id = id;
		this.ads = ads;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Integer> getAds() {
		return ads;
	}

	public void setAds(List<Integer> ads) {
		this.ads = ads;
	}
	
	
}
