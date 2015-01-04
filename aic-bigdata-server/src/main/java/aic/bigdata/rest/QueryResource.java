package aic.bigdata.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import aic.bigdata.database.model.AicUser;

/*
 * 1. Which users are the most inﬂuential persons in your data set? Inﬂuential persons do not only have many followers, they also trigger many retweets and favourites.
 * 
 2. Which users are interested in a broad range of topics, which are more focused? Keep in mind that simply counting topics may be too limiting, as users that tweet 
 a lot will naturally also mention more topics. Porting basic concepts from the “term frequency-inverse document frequency” may help you here.

 3. Suggest concrete ads from your database to a given user based on his existing interests, i.e., topics the user is already mentioning actively.

 4. Suggest concrete ads from your database to a given user based on his potential interests, i.e., topics the user does not actively mention at the moment, but which
 are his connections (and their friends, and their friends, ..., with decreasing importance) are interested in.
 */

@Path("queries")
public class QueryResource {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AicUser getMostInflPerson() {
		return new AicUser();
	}

}
