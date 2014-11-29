package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mongodb.DBObject;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.Connection;
import aic.bigdata.rest.model.Connections;
import aic.bigdata.server.ServerConfig;

@Path("connections")
public class ConnectionResource {

	
	@GET
	@Path("topics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getTopicNames() throws UnknownHostException{
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();
		MongoDatabase mdb = new MongoDatabase(config);

		return mdb.getTopicNames();
	}
	
	@GET
	@Path("topics/{topic}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Connections getTopicUsers(@PathParam("topic") String topicName) throws UnknownHostException {
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		MongoDatabase mdb = new MongoDatabase(config);
		
		Connections con = new Connections();
		Set<Long> usersMentioning = GraphDatabase.getSingleton(config).getUsersMentioning(topicName);
		for (Long id : usersMentioning) {
			DBObject o = mdb.getUserById(id);
			System.out.println(id);
			con.getConnections().add(new Connection(id.toString(), o.get("name").toString(), 0, 0, 1));
		}
		
		
		
		con.setTotalSize(1);
		return con;
	}
}
