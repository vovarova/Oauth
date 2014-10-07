package fine.project;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultServlet extends HttpServlet {

	private static final long serialVersionUID = -7438102992359827539L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = request.getParameter("data");
		String servlet = request.getParameter("servlet");
		if (data == null || servlet == null) {
			response.sendError(400, "There no avaliable parameters data or servlet");
			return;
		}

		byte[] decodeBase64Data = Base64.decodeBase64(data);
		String finalData = StringEscapeUtils.unescapeJava(new String(decodeBase64Data, "UTF-8"));
		try {
			JSONObject jsonInfo = new JSONObject(finalData);
			finalData = jsonInfo.toString(1).replace("\n", "<br/>");
		} catch (JSONException e) {
			System.out.println("Unable to parse JSON " + finalData);
		}
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<body>");
		writer.println("<a href=\"/\">Back to main</a>");
		writer.println("<h3>This is your information from <b>" + servlet + "</b></h3>");
		writer.println("<div>" + finalData + "<div>");
		writer.println("</body>");
		writer.println("</html>");
	}

}
