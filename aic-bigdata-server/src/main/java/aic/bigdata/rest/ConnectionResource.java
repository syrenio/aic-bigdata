package aic.bigdata.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.Connections;
import aic.bigdata.rest.model.SigmaEdge;
import aic.bigdata.rest.model.SigmaNode;
import aic.bigdata.server.ServerConfig;

@Path("connections")
public class ConnectionResource {

	@GET
	@Path("topics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getTopicNames() throws UnknownHostException {
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();
		MongoDatabase mdb = new MongoDatabase(config);

		List<String> list = mdb.getTopicNames();
		list.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return list;
	}

	@GET
	@Path("topics/{topic}/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Connections getTopicUsers(@PathParam("topic") String topicName) throws UnknownHostException, SQLException {
		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		SqlDatabase sqldb = new SqlDatabase(config);

		Connections con = new Connections();
		Set<Long> usersMentioning = GraphDatabase.getInstance().getUsersMentioning(topicName);
		String baseEdgeName = "e";
		long edgeCounter = 0;
		for (Long id : usersMentioning) {
			AicUser usr = sqldb.getUserById(id);
			if (usr == null) {
				System.err.println(id + " UserId not found!");
			} else {
				BigDecimal x = new BigDecimal(Math.random() * 10);
				BigDecimal y = new BigDecimal(Math.random() * 10);
				SigmaNode node = new SigmaNode(id.toString(), usr.getName(), x.setScale(2, RoundingMode.HALF_DOWN).doubleValue(), y.setScale(2,
						RoundingMode.HALF_DOWN).doubleValue(), 1);
				SigmaEdge edge = new SigmaEdge(baseEdgeName + edgeCounter, node.getId(), topicName);
				con.getNodes().add(node);
				con.getEdges().add(edge);
			}
			edgeCounter++;
		}
		con.getNodes().add(new SigmaNode(topicName, topicName, 5, 5, 2));

		con.setTotalSize(1);
		return con;
	}
}
