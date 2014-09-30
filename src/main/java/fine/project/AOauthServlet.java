package fine.project;

import javax.servlet.Servlet;

public interface AOauthServlet extends Servlet {
	String getPath();
	String getName();
	String getIconPath();
}
