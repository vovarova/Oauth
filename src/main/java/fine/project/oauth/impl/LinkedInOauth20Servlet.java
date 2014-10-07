package fine.project.oauth.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import fine.project.oauth.Credentials;
import fine.project.oauth.OauthServlet;

public class LinkedInOauth20Servlet extends OauthServlet {

	public LinkedInOauth20Servlet(String path, String name, String iconPath, Credentials credential) {
		super(path, name, iconPath, credential);
	}

	private static final long serialVersionUID = -6730806246376461723L;
	private static final String AUTHORIZATION_ENDPOINT = "https://www.linkedin.com/uas/oauth2/authorization";
	private static final String TOKEN_ENDPOINT = "https://www.linkedin.com/uas/oauth2/accessToken";
	private HttpClient httpClient = HttpClients.createDefault();

	@Override
	public String generateRedirectUrl() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
		parameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
		parameters.add(new BasicNameValuePair("response_type", "code"));
		parameters.add(new BasicNameValuePair("state", "state"));
		String redirectUrl = AUTHORIZATION_ENDPOINT + "?" + URLEncodedUtils.format(parameters, "UTF-8");
		return redirectUrl;

	}

	@Override
	public String retrieveAccessToken(HttpServletRequest httpServletRequest) {
		String accessToken = null;
		try {
			String codeParameter = httpServletRequest.getParameter("code");

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("code", codeParameter));
			postParameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
			postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
			postParameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
			postParameters.add(new BasicNameValuePair("client_secret", credentials.getAplicationSecret()));

			HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT + "?" + URLEncodedUtils.format(postParameters, "UTF-8"));
			HttpResponse tokenRequestResult = httpClient.execute(httpPost);

			String tokenRequestResponse = IOUtils.toString(tokenRequestResult.getEntity().getContent());
			EntityUtils.consume(tokenRequestResult.getEntity());

			Matcher matcher = Pattern.compile("\"access_token\":\"(.*?)\"").matcher(tokenRequestResponse);
			if (matcher.find()) {
				accessToken = matcher.group(1);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	@Override
	public byte[] protectedResourceRequest(String accessToken, HttpServletRequest httpServletRequest) {
		byte[] resp = null;
		try {
			HttpGet httpGet = new HttpGet(credentials.getResourceRequestURL());
			httpGet.addHeader("Authorization", "Bearer " + accessToken);
			HttpResponse profileRequestResult = httpClient.execute(httpGet);
			resp = IOUtils.toByteArray(profileRequestResult.getEntity().getContent());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}

}
