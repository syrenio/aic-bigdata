package aic.bigdata.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import aic.bigdata.server.BackgroundTaskManager;

@Path("service")
public class ServiceResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String showServiceStatus() {
		return BackgroundTaskManager.getStatus();
	}
}
