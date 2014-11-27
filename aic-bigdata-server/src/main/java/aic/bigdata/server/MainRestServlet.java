package aic.bigdata.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MainRestServlet extends ServletContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		
		//ServerConfig cf = new ServerConfigBuilder().getConfig();
//		TaskManager.startServices(cf);
	}

	@Override
	public void destroy() {
//		TaskManager.stopServices();
		// TODO Auto-generated method stub
		super.destroy();
	}
}
