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
		String serverPath = getServletConfig().getInitParameter("server");
		Properties propsTwitter = new Properties();
		Properties propsServer = new Properties();
		try {
			InputStream isTwitter = getServletContext().getResourceAsStream(twitterPath);
			propsTwitter.load(isTwitter);
			InputStream isServer = getServletContext().getResourceAsStream(serverPath);
			propsServer.load(isServer);
		} catch (IOException e) {
			System.err.println("could not load twitter properties");
			e.printStackTrace();
		}

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setTwitter(propsTwitter);
		serverConfig.setServer(propsServer);

		BackgroundTaskManager.startServices(serverConfig);

	}

	@Override
	public void destroy() {
		BackgroundTaskManager.stopServices();
		// TODO Auto-generated method stub
		super.destroy();
	}
}
