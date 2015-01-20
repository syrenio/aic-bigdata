package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.enrichment.AdObject;
import aic.bigdata.enrichment.TopicObject;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.AdDTO;
import aic.bigdata.rest.model.TopicDTO;
import aic.bigdata.server.ServerConfig;

@Path("ads")
public class AdResource {

	private MongoDatabase mongo = null;
	private ServerConfigBuilder scb = new ServerConfigBuilder();
	private ServerConfig config = scb.getConfig();

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AdDTO> getAds() {

		if (mongo == null)
			mongo = new MongoDatabase(config);

		Map<String, Long> mentionCounts = new HashMap<String, Long>();

		List<AdDTO> ads = new ArrayList<AdDTO>();
		try {
			for (AdObject adObject : mongo.getAds()) {
				AdDTO ad = new AdDTO(adObject);
				for (TopicDTO t : ad.getTopics()) {
					if (!mentionCounts.containsKey(t.getName())) {
						mentionCounts.put(t.getName(),
								new Long(GraphDatabase.getInstance().getUsersMentioning(t.getName()).size()));
					}

					long usercount = mentionCounts.get(t.getName());
					System.out.println("user mentioning count for topic " + t.getName() + " : " + usercount);
					t.setMentionings(usercount);
				}
				ads.add(ad);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ads;
	}

	@GET
	@Path("/topics")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<TopicDTO> getTopics(@QueryParam("withMention") boolean withMention) {

		List<TopicDTO> list = new ArrayList<TopicDTO>();
		try {
			if (mongo == null)
				mongo = new MongoDatabase(config);

			for (TopicObject o : mongo.getTopics()) {
				TopicDTO t = new TopicDTO(o.getId(), 0);
				if (withMention)
					t.setMentionings(GraphDatabase.getInstance().getUsersMentioning(o.getId()).size());
				list.add(t);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
