package aic.bigdata.rest.model;

public class TopicDTO {
	private String name;
	private long mentionings;

	public TopicDTO(String name, long countMentionings) {
		this.name = name;
		this.setMentionings(countMentionings);
	}

	public long getMentionings() {
		return mentionings;
	}

	public void setMentionings(long mentionings) {
		this.mentionings = mentionings;
	}

	public String getName() {
		return name;
	}

}
