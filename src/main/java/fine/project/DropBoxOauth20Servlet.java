package fine.project;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class DropBoxOauth20Servlet extends HttpServlet implements OauthServlet {

	private String aplicationID = "iptz6hzp644bddf";
	private String aplicationSecret = "ubs8fxukbxegvbw";
	private String redirectUrl = "https://vroman.com/dropboxOauth";

	private static final long serialVersionUID = -6730806246376461723L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpClient client = HttpClients.createDefault();

		String tokenEndpoint = "https://api.dropbox.com/1/oauth2/token";
		String codeParameter = request.getParameter("code");

		HttpPost httpPost = new HttpPost(tokenEndpoint);

		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("code", codeParameter));
		postParameters.add(new BasicNameValuePair("client_id", aplicationID));
		postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
		postParameters.add(new BasicNameValuePair("redirect_uri", redirectUrl));
		postParameters.add(new BasicNameValuePair("client_secret", aplicationSecret));

		httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpResponse tokenRequestResult = client.execute(httpPost);

		String tokenRequestResponse = IOUtils.toString(tokenRequestResult.getEntity().getContent());
		EntityUtils.consume(tokenRequestResult.getEntity());

		String accessToken = "";
		Matcher matcher = Pattern.compile("\"access_token\": \"(.*?)\"").matcher(tokenRequestResponse);
		if (matcher.find()) {
			accessToken = matcher.group(1);
		}
		String profileRequestString = "https://api.dropbox.com/1/account/info";
		HttpGet httpGet = new HttpGet(profileRequestString);
		httpGet.addHeader("Authorization", "Bearer " + accessToken);
		HttpResponse profileRequestResult = client.execute(httpGet);
		byte[] resp = IOUtils.toByteArray(profileRequestResult.getEntity().getContent());
		byte[] respBase64 = Base64.encodeBase64(resp);
		EntityUtils.consume(profileRequestResult.getEntity());
		
		response.sendRedirect("/result?data=" + new String(respBase64) + "&servlet="
				+ URLEncoder.encode(getName(),"UTF-8"));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String redirectStringTemplate = "https://www.dropbox.com/1/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=STATE";
		String requestString = String.format(redirectStringTemplate, aplicationID, redirectUrl);
		response.sendRedirect(requestString);
	}

	public String getPath() {
		return "/dropboxOauth";
	}

	public String getName() {
		return "Dropbox Oauth 2.0";
	}

	public String getIconPath() {
		return "http://www.ministry2youth.com/wp-content/uploads/2014/04/dropbox-icon.png";
	}

}
