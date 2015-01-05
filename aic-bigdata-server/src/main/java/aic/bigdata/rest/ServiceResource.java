package aic.bigdata.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import aic.bigdata.rest.model.ServiceStatus;
import com.sun.jersey.spi.resource.Singleton;

import aic.bigdata.extraction.ServerConfigBuilder;
import aic.bigdata.server.ServerConfig;
import aic.bigdata.server.TaskManager;

@Path("service")
@Singleton
public class ServiceResource {

	private TaskManager sm = null;
	private ServiceStatus status = new ServiceStatus();
	
	private TaskManager getTaskManager() {
		if(sm == null)
			sm = new TaskManager();
		return sm;
	}
	
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceStatus showServiceStatus() {
		return status;
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceStatus commandService(@QueryParam("command") String command) {
		ServerConfig cf = new ServerConfigBuilder().getConfig();
		switch (command.toLowerCase()) {
		case "start":
			getTaskManager().startService(cf);
			status.stream = true;
			return status;
		case "stop":
			getTaskManager().stopService();
			status = new ServiceStatus();
			return status;
		case "extraction":
			getTaskManager().startExtraction(cf);
			status.extraction = true;
			return status;
		case "analyse":
			getTaskManager().startAnalyse(cf);
			status.analyse = true;
			return status;
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
