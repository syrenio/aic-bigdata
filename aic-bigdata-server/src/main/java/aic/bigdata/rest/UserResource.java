package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.SigmaNode;
import aic.bigdata.rest.model.Connections;
import aic.bigdata.rest.model.ResultEntry;
import aic.bigdata.rest.model.ResultPage;
import aic.bigdata.server.ServerConfig;

import com.mongodb.DBObject;
import com.sun.jersey.spi.resource.Singleton;

@Path("users")
public class UserResource {

	MongoDatabase mongo = null;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResultPage getUsers(@QueryParam("size") int size,
			@QueryParam("page") int page) {

		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		MongoDatabase mongo = new MongoDatabase(config);

		List<DBObject> list = null;
		try {
			list = mongo.getUsers(page, size);
			List<ResultEntry> result = new ArrayList<ResultEntry>();
			for (DBObject user : list) {
				ResultEntry entry = new ResultEntry();
				Long long1 = new Long(user.get("id").toString());
				String name = String.valueOf(user.get("name"));
				entry.setId(long1);
				entry.setName(name);
				result.add(entry);
			}

			ResultPage resultPage = new ResultPage();
			resultPage.setResult(result);
			resultPage.setTotalSize(size);
			return resultPage;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("{userId}/connections")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Connections getConnections(@PathParam("userId") long userId) {
		System.out.println("userId: " + userId);
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		if(mongo==null)
			mongo = new MongoDatabase(config);

		
		// FIXME CREATE NEO4J DB Instance and get Connections
		// MongoDatabase mongo = new MongoDatabase(config);
		Set<String> mentionedTopics = GraphDatabase.getInstance().getMentionedTopics(userId);

		Connections con = new Connections();
		for (String string : mentionedTopics) {
			con.getConnections().add(new SigmaNode(string, string, 0, 0, 1));
		}
		return con;
	}

}
