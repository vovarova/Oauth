package fine.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			writer.println("<form action=\""+oauthServlet.getPath()+"\" method=\"post\">");
			if(oauthServlet.getIconPath()==null){
				writer.println("<input type=\"submit\" value=\""+oauthServlet.getName()+"\">");				
			}else{				
				writer.println("<input type=\"image\" width=\"80\" height=\"80\" src=\""+oauthServlet.getIconPath()+"\">");
			}
			 
			writer.println("</form>");
		}
		writer.println("</body>");
		writer.println("</html>");
	}

}
