package aic.bigdata.rest;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.Connection;
import aic.bigdata.rest.model.Connections;
import aic.bigdata.server.ServerConfig;

@Path("connections")
public class ConnectionResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Connections getConnections(@QueryParam("topic") String topicName) {
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		// FIXME CREATE NEO4J DB Instance and get Connections
		// MongoDatabase mongo = new MongoDatabase(config);
		Set<Long> usersMentioning = GraphDatabase.getSingleton(config).getUsersMentioning(topicName);
		for (Long id : usersMentioning) {
			System.out.println(id);
		}
		
		Connections con = new Connections();
		con.getConnections().add(new Connection("n0", "test", 0, 0, 1));
		con.setTotalSize(1);
		return con;
	}
}
