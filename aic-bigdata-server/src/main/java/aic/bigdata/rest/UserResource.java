package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.MongoDatabase;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.rest.model.ResultEntry;
import aic.bigdata.rest.model.ResultPage;
import aic.bigdata.server.ServerConfig;

import com.mongodb.DBObject;

@Path("users")
public class UserResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResultPage getUsers(@QueryParam("size") int size, @QueryParam("page") int page) {

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
}
