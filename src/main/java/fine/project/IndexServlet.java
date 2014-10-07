package fine.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fine.project.oauth.OauthServlet;

public class IndexServlet extends HttpServlet {

	private List<OauthServlet> oauthServlets = null;
	private static final long serialVersionUID = -6730806246376461723L;

	public IndexServlet(List<OauthServlet> oauthServlets) {
		if (oauthServlets == null) {
			this.oauthServlets = new ArrayList<OauthServlet>();
		} else {
			this.oauthServlets = oauthServlets;
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<body>");
		writer.println("<h1>Wellcome Human Being !</h1>");

		for (OauthServlet oauthServlet : oauthServlets) {
			writer.println("<form action=\"" + oauthServlet.getPath() + "\" method=\"post\">");
			writer.println("<div style=\"width: 170px; height: 80px; display: table\">");

			if (oauthServlet.getIconPath() == null) {
				writer.println("<input type=\"submit\" value=\"" + oauthServlet.getName() + "\">");
			} else {
				writer.println("<input type=\"image\" style=\"float : left; margin-right:15\" width=\"80\" height=\"80\" src=\""
						+ oauthServlet.getIconPath() + "\">");
				writer.println("<p>"+oauthServlet.getName()+"</p>");
			}
			writer.println("</div>");
			writer.println("</form>");
		}
		writer.println("</body>");
		writer.println("</html>");
	}

}
