package aic.bigdata.rest;

import java.sql.SQLException;
import java.util.ArrayList;
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
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.Connections;
import aic.bigdata.rest.model.ResultEntry;
import aic.bigdata.rest.model.ResultPage;
import aic.bigdata.rest.model.SigmaNode;
import aic.bigdata.server.ServerConfig;

@Path("users")
public class UserResource {

	MongoDatabase mongo = null;
	SqlDatabase sql = null;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResultPage getUsers(@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("fname") String fname) {
		System.out.println("size=" + size + " page=" + page + " fname=" + fname);

		ServerConfigBuilder scb = new ServerConfigBuilder();
		ServerConfig config = scb.getConfig();

		List<AicUser> list = null;
		try {
			if (sql == null)
				sql = new SqlDatabase(config);

			if (fname != null && !fname.isEmpty()) {
				list = sql.getUsers(page, size, fname);
			} else {
				list = sql.getUsers(page, size);
			}
			List<ResultEntry> result = new ArrayList<ResultEntry>();
			for (AicUser user : list) {
				ResultEntry entry = new ResultEntry();
				entry.setId(user.getId());
				entry.setName(user.getName());
				entry.setScreenName(user.getScreenName());
				result.add(entry);
			}

			ResultPage resultPage = new ResultPage();
			resultPage.setResult(result);
			resultPage.setTotalSize(sql.getUserCount());
			System.out.println(resultPage);

			return resultPage;
		} catch (SQLException e) {
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

		if (mongo == null)
			mongo = new MongoDatabase(config);

		// MongoDatabase mongo = new MongoDatabase(config);
		Set<String> mentionedTopics = GraphDatabase.getInstance().getMentionedTopics(userId);

		Connections con = new Connections();
		for (String string : mentionedTopics) {
			con.getNodes().add(new SigmaNode(string, string, 0, 0, 1));
		}
		return con;
	}

}
