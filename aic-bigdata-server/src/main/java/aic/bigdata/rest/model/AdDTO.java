package aic.bigdata.rest.model;

import java.util.ArrayList;
import java.util.List;

import aic.bigdata.enrichment.AdObject;

public class AdDTO {

	private int id;
	private String name;
	private String campaign;
	private String text;
	private List<TopicDTO> topics;

	public AdDTO(AdObject ad) {
		id = ad.getId();
		name = ad.getName();
		campaign = ad.getCampaign();
		text = ad.getText();
		topics = new ArrayList<TopicDTO>();
		for (String t : ad.getTopics()) {
			topics.add(new TopicDTO(t, 0));
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCampaign() {
		return campaign;
	}

	public String getText() {
		return text;
	}

	public List<TopicDTO> getTopics() {
		return topics;
	}

}
