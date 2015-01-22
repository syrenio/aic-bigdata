package aic.bigdata.rest;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import aic.bigdata.database.GraphDatabase;
import aic.bigdata.database.MongoDatabase;
import aic.bigdata.database.SqlDatabase;
import aic.bigdata.database.model.AicUser;
import aic.bigdata.enrichment.AdObject;
import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;

import com.mongodb.DBObject;

/*
 * 1. Which users are the most inﬂuential persons in your data set? Inﬂuential persons do not only have many followers, 
 * they also trigger many retweets and favourites.
 * 
 2. Which users are interested in a broad range of topics, which are more focused? 
 Keep in mind that simply counting topics may be too limiting, as users that tweet 
 a lot will naturally also mention more topics. Porting basic concepts from the “term frequency-inverse 
 document frequency” may help you here.

 3. Suggest concrete ads from your database to a given user based on his existing interests, i.e., 
 topics the user is already mentioning actively.

 4. Suggest concrete ads from your database to a given user based on his potential interests, i.e., 
 topics the user does not actively mention at the moment, but which
 are his connections (and their friends, and their friends, ..., with decreasing importance) are interested in.
 */

@Path("queries")
public class QueryResource {

	ServerConfig config = new ServerConfigBuilder().getConfig();

	// FIXME DUMMY CODE!!!!
	private List<AicUser> getDummyAicUserData() {
		try {
			// FIXME DUMMY CODE!!!!
			Random rnd = new Random();
			int x = rnd.nextInt(10) + 10;

			SqlDatabase db = new SqlDatabase(config);
			List<AicUser> users = db.getUsers(x, 10);
			return users;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<AicUser>();
	}

	private List<AdObject> getDummyAdObjects() {

		try {
			MongoDatabase md = new MongoDatabase(config);

			return md.getAds();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<AdObject>();
	}

	@GET
	@Path("inflUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AicUser> getMostInflUsers() {
		System.out.println("inflUser called!");
		List<AicUser> list = new ArrayList<AicUser>();

		GraphDatabase graphDb = GraphDatabase.getInstance();
		List<Long> ids = graphDb.getMostInfluentalUserIDs();

		ServerConfigBuilder b = new ServerConfigBuilder();

		try {
			SqlDatabase sqlDb = new SqlDatabase(b.getConfig());
			System.out.println("foundIds: " + StringUtils.join(ids, ","));

			for (Long id : ids) {
				list.add(sqlDb.getUserById(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// System.out.println("most influental users: " + ids);
		// System.out.println("list has " + list.size() + " elements");

		// list = getDummyAicUserData(); // FIXME DUMMY CODE!!!!

		return list;
	}

	@GET
	@Path("usersWithInterests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @QueryParam("topics") List<String> topics
	public List<AicUser> getBroadIntrestUsers() {

		List<AicUser> list = new ArrayList<AicUser>();

		ServerConfigBuilder b = new ServerConfigBuilder();

		try {
			SqlDatabase sqlDb = new SqlDatabase(b.getConfig());
			MongoDatabase mongoDb = new MongoDatabase(b.getConfig());

			for (DBObject o : mongoDb.getCursorForTFSums()) {
				Long id = null;
				try {
					id = ((Double) o.get("_id")).longValue();
				} catch (ClassCastException cce) {
					try {
						id = (Long) o.get("_id");
					} catch (ClassCastException cce2) {
						System.err.println("_id is neither Long nor Double?");
					}
				}
				list.add(sqlDb.getUserById(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Copy of Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	@GET
	@Path("suggestAdsForUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<AdObject> getSuggestedAdsForUser(@QueryParam("userId") long userId, @QueryParam("potentialInterests") boolean potIntr,
			@QueryParam("jumps") int jumps) {
		System.out.println("userId:" + userId + " potentialInterests: " + potIntr + " jumps: " + jumps);

		GraphDatabase db = GraphDatabase.getInstance();
		List<String> topics = db.getMostMentionedTopics(userId, potIntr, jumps);

		System.out.println("most mentioned topics for user #" + userId + ": " + topics);

		ServerConfigBuilder b = new ServerConfigBuilder();
		MongoDatabase mongoDb = new MongoDatabase(b.getConfig());

		List<AdObject> list = new ArrayList<AdObject>();
		// list = getDummyAdObjects();
		try {
			list = mongoDb.getAdsForTopics(topics);
		} catch (UnknownHostException e) {
			// TODO Copy of Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
