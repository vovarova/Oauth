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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;

import fine.project.oauth.Credentials;
import fine.project.oauth.OauthServlet;
import fine.project.oauth.OauthServletException;

public class DropBoxOauth20Servlet extends OauthServlet {

	private static final long serialVersionUID = 5578585967401452811L;
	private static final String TOKEN_ENDPOINT = "https://api.dropbox.com/1/oauth2/token";
	private static final String AUTHORIZATION_ENDPOINT = "https://www.dropbox.com/1/oauth2/authorize";

	public DropBoxOauth20Servlet(String path, String name, String iconPath, Credentials credential) {
		super(path, name, iconPath, credential);
	}

	private HttpClient httpClient = HttpClients.createDefault();

	@Override
	public String generateRedirectUrl() {
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
		postParameters.add(new BasicNameValuePair("response_type", "code"));
		postParameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
		postParameters.add(new BasicNameValuePair("state", "STATE"));
		String redirectUrl = AUTHORIZATION_ENDPOINT + "?" + URLEncodedUtils.format(postParameters, "UTF-8");

		return redirectUrl;
	}

	@Override
	public String retrieveAccessToken(HttpServletRequest httpServletRequest) throws OauthServletException {

		String accessToken = null;
		try {
			String codeParameter = httpServletRequest.getParameter("code");
			HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT);
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("code", codeParameter));
			postParameters.add(new BasicNameValuePair("client_id", credentials.getAplicationID()));
			postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
			postParameters.add(new BasicNameValuePair("redirect_uri", credentials.getRedirectUrl()));
			postParameters.add(new BasicNameValuePair("client_secret", credentials.getAplicationSecret()));

			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse tokenRequestResult = httpClient.execute(httpPost);
			String tokenRequestResponse = IOUtils.toString(tokenRequestResult.getEntity().getContent());
			EntityUtils.consume(tokenRequestResult.getEntity());
			if (tokenRequestResult.getStatusLine().getStatusCode() != HttpStatus.OK_200) {
				throw new OauthServletException(tokenRequestResponse);
			}
			Matcher matcher = Pattern.compile("\"access_token\": \"(.*?)\"").matcher(tokenRequestResponse);
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
			HttpResponse protectedResourceRequest = httpClient.execute(httpGet);
			resp = IOUtils.toByteArray(protectedResourceRequest.getEntity().getContent());
			EntityUtils.consume(protectedResourceRequest.getEntity());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}

}
