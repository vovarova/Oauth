package fine.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class App {
	private static List<OauthServlet> oauthServlets = new ArrayList<OauthServlet>();
	
	public static void main(String[] args) throws Exception {

		oauthServlets.add(new FacebookOauth20Servlet());
		oauthServlets.add(new DropBoxOauth20Servlet());
		oauthServlets.add(new LinkedInOauth20Servlet());
		
		Server server = new Server();
		HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(App.class.getResource("/keystore.jks").toExternalForm());
		sslContextFactory.setKeyStorePassword("qwerty12345");
		sslContextFactory.setKeyManagerPassword("qwerty12345");

		ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory,
				"http/1.1"), new HttpConnectionFactory(https));
		sslConnector.setPort(443);
		server.setConnectors(new Connector[] { sslConnector });
		
		
		ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
		context.addServlet(new ServletHolder(new IndexServlet(oauthServlets)), "/");
		
		for (OauthServlet oauthServlet : oauthServlets) {
			context.addServlet(new ServletHolder(oauthServlet), oauthServlet.getPath());
		}
		context.addServlet(new ServletHolder(new ResultServlet()), "/result");
		server.start();
		server.join();
	}
}
