package aic.bigdata.enrichment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ad")
public class AdObject {
	@XmlAttribute
	private int id;
	
	private String name;
	
	private String campaign;
	
	private String text;
	
	@XmlElement(name="topic")
	private List<String> topics;
	
	private String credits;
	
	private String picture;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	
}
