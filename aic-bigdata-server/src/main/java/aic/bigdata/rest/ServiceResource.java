package aic.bigdata.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.resource.Singleton;

import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TaskManager;

@Path("service")
@Singleton
public class ServiceResource {

	private TaskManager sm = null;
	
	private TaskManager getTaskManager() {
		if(sm == null)
			sm = new TaskManager();
		return sm;
	}
	
	@GET
	@Path("/status")
	@Produces(MediaType.TEXT_PLAIN)
	public String showServiceStatus() {
		return getTaskManager().getStatus();
	}

	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String commandService(@QueryParam("command") String command) {
		ServerConfig cf = new ServerConfigBuilder().getConfig();
		switch (command.toLowerCase()) {
		case "start":
			getTaskManager().startService(cf);
			return "Service starting...";
		case "stop":
			getTaskManager().stopService();
			return "Service stopping...";
		case "extraction":
			getTaskManager().startExtraction(cf);
			return "Extraction started...";
		case "analyse":
			getTaskManager().startAnalyse(cf);
			return "Analysis started...";
		default:
			break;
		}
		return null;
	}

	@GET
	@Path("/tweet")
	@Produces(MediaType.TEXT_PLAIN)
	public String showTweetCount() {
		return getTaskManager().getTweetCount();
	}

}
