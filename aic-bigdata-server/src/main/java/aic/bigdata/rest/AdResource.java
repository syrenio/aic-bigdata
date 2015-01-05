package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.enrichment.AdObject;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

@Path("ads")
public class AdResource {

	MongoDatabase mongo = null;
	ServerConfigBuilder scb = new ServerConfigBuilder();
	ServerConfig config = scb.getConfig();

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AdObject> getAds() {

		if (mongo == null)
			mongo = new MongoDatabase(config);

		List<AdObject> list = new ArrayList<AdObject>();
		try {
			list = mongo.getAds();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return list;
	}
}
