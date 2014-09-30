package fine.project;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class FacebookOauth20Servlet extends HttpServlet implements OauthServlet {

	private static final long serialVersionUID = -6730806246376461723L;

	private String aplicationID = "282861085239794";
	private String aplicationSecret = "069ad672c04e061778cb46833bbc84bf";
	private String redirectUrl = "https://vroman.com/facebookOauth";
	private String scope = "public_profile,email,user_friends";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tokenEndpointTemplete = "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
		String codeParameter = request.getParameter("code");
		String tokenRequest = String.format(tokenEndpointTemplete, aplicationID, redirectUrl, aplicationSecret,
				codeParameter);

		URLConnection connection = new URL(tokenRequest).openConnection();
		String facebookResponse = IOUtils.toString(connection.getInputStream());

		String accessToken = "";
		Matcher matcher = Pattern.compile("access_token=(.*?)&").matcher(facebookResponse);
		if (matcher.find()) {
			accessToken = matcher.group(1);
		}
		String profileRequestString = "https://graph.facebook.com/me/?access_token=%s";

		String format = String.format(profileRequestString, accessToken);
		System.out.println(format);
		connection = new URL(format).openConnection();

		byte[] resp = IOUtils.toByteArray(connection.getInputStream());
		byte[] respBase64 = Base64.encodeBase64(resp);

		response.sendRedirect("/result?data=" + new String(respBase64) + "&servlet="
				+ URLEncoder.encode(getName(),"UTF-8"));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String redirectStringTemplate = "https://www.facebook.com/dialog/oauth?scope=%s&client_id=%s&redirect_uri=%s";
		String requestString = String.format(redirectStringTemplate, scope, aplicationID, redirectUrl);
		response.sendRedirect(requestString);
	}

	public String getPath() {
		return "/facebookOauth";
	}

	public String getName() {
		return "Facebook Oauth 2.0 ";
	}

	public String getIconPath() {
		return "https://www.facebook.com/images/fb_icon_325x325.png";
	}

}
