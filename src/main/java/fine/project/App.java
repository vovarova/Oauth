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

import fine.project.oauth.Credentials;
import fine.project.oauth.OauthServlet;
import fine.project.oauth.impl.DropBoxOauth20Servlet;
import fine.project.oauth.impl.FacebookOauth20Servlet;
import fine.project.oauth.impl.LinkedInOauth20Servlet;

public class App {

	private static List<OauthServlet> getOauthServlets() {
		List<OauthServlet> oauthServlets = new ArrayList<OauthServlet>();

		Credentials facebookCredential = new Credentials("282861085239794", "069ad672c04e061778cb46833bbc84bf",
				"https://vroman.asuscomm.com/facebookOauth", "https://graph.facebook.com/me/",
				"public_profile,email,user_friends");
		FacebookOauth20Servlet facebookOauthServlet = new FacebookOauth20Servlet("/facebookOauth",
				"Facebook Oauth 2.0", "https://www.facebook.com/images/fb_icon_325x325.png", facebookCredential);

		Credentials linkedInCredential = new Credentials("779b93nrns2gwu", "EnfHpQ1MbBilvpiv",
				"https://vroman.asuscomm.com/linkedinOauth", "https://api.linkedin.com/v1/people/~/?format=json");
		LinkedInOauth20Servlet linkedInOauthServlet = new LinkedInOauth20Servlet("/linkedinOauth",
				"LinkedIn Oauth 2.0", "http://www.gradleware.com/wp-content/uploads/2014/08/linkedin-logo-square-300x300.png",
				linkedInCredential);

		Credentials dropboxCredential = new Credentials("iptz6hzp644bddf", "ubs8fxukbxegvbw",
				"https://vroman.asuscomm.com/dropboxOauth", "https://api.dropbox.com/1/account/info");
		DropBoxOauth20Servlet dropdoxOauthServlet = new DropBoxOauth20Servlet("/dropboxOauth", "Dropbox Oauth 2.0",
				"http://www.ministry2youth.com/wp-content/uploads/2014/04/dropbox-icon.png", dropboxCredential);

		oauthServlets.add(dropdoxOauthServlet);
		oauthServlets.add(facebookOauthServlet);
		oauthServlets.add(linkedInOauthServlet);

		return oauthServlets;
	}

	public static void main(String[] args) throws Exception {

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

		List<OauthServlet> oauthServlets = getOauthServlets();
		context.addServlet(new ServletHolder(new IndexServlet(oauthServlets)), "/");

		for (OauthServlet oauthServlet : oauthServlets) {
			context.addServlet(new ServletHolder(oauthServlet), oauthServlet.getPath());
		}
		context.addServlet(new ServletHolder(new ResultServlet()), "/result");
		server.start();
		server.join();
	}
}
