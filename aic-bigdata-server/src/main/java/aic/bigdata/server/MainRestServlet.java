package aic.bigdata.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import aic.bigdata.extraction.ServerConfigBuilder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MainRestServlet extends ServletContainer {

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);

		ServerConfig cf = new ServerConfigBuilder().getConfig();
		BackgroundTaskManager.startServices(cf);
	}

	@Override
	public void destroy() {
		BackgroundTaskManager.stopServices();
		// TODO Auto-generated method stub
		super.destroy();
	}
}
