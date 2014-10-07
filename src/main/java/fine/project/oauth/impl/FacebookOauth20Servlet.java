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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;

import fine.project.oauth.Credentials;
import fine.project.oauth.OauthServlet;
import fine.project.oauth.OauthServletException;

public class FacebookOauth20Servlet extends OauthServlet {

	private static final long serialVersionUID = -6730806246376461723L;

	private static final String TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
	private static final String AUTHORIZATION_ENDPOINT = "https://www.facebook.com/dialog/oauth";
	private HttpClient httpClient = HttpClients.createDefault();

	public FacebookOauth20Servlet(String path, String name, String iconPath, Credentials credential) {
		super(path, name, iconPath, credential);
	}

	@Override
	public String generateRedirectUrl() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
		parameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
		parameters.add(new BasicNameValuePair("scope", credentials.getScope()));

		String redirectUrl = AUTHORIZATION_ENDPOINT + "?" + URLEncodedUtils.format(parameters, "UTF-8");
		return redirectUrl;
	}

	@Override
	public String retrieveAccessToken(HttpServletRequest httpServletRequest) throws OauthServletException {
		String accessToken = null;
		try {
			String codeParameter = httpServletRequest.getParameter("code");

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("code", codeParameter));
			postParameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
			postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
			postParameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
			postParameters.add(new BasicNameValuePair("client_secret", credentials.getAplicationSecret()));

			HttpGet httpGet = new HttpGet(TOKEN_ENDPOINT + "?" + URLEncodedUtils.format(postParameters, "UTF-8"));

			HttpResponse tokenRequestResult = httpClient.execute(httpGet);
			String tokenRequestResponse = IOUtils.toString(tokenRequestResult.getEntity().getContent());
			EntityUtils.consume(tokenRequestResult.getEntity());
			if (tokenRequestResult.getStatusLine().getStatusCode() != HttpStatus.OK_200) {
				throw new OauthServletException(tokenRequestResponse);
			}
			Matcher matcher = Pattern.compile("access_token=(.*?)&").matcher(tokenRequestResponse);
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
			HttpGet httpGet = new HttpGet(credentials.getResourceRequestURL() + "?access_token=" + accessToken);
			HttpResponse protectedResourceRequest = httpClient.execute(httpGet);
			resp = IOUtils.toByteArray(protectedResourceRequest.getEntity().getContent());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}

}
