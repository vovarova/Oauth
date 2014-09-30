package fine.project;

import javax.servlet.Servlet;

public interface OauthServlet extends Servlet {
	String getPath();
	String getName();
	String getIconPath();
}
