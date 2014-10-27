package aic.bigdata.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class MainRestServlet extends ServletContainer {

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);

		String twitterPath = getServletConfig().getInitParameter("twitter");
		Properties props = new Properties();
		try {
			InputStream is = getServletContext().getResourceAsStream(twitterPath);
			props.load(is);
		} catch (IOException e) {
			System.err.println("could not load twitter properties");
			e.printStackTrace();
		}

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setTwitter(props);

		BackgroundTaskManager.startServices(serverConfig);

	}

	@Override
	public void destroy() {
		BackgroundTaskManager.stopServices();
		// TODO Auto-generated method stub
		super.destroy();
	}
}
