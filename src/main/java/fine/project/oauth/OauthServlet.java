package fine.project.oauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

public abstract class OauthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String path;
	private String name;
	private String iconPath;
	protected Credentials credentials;

	public abstract String generateRedirectUrl();

	public abstract String retrieveAccessToken(HttpServletRequest httpServletRequest) throws OauthServletException;

	public abstract byte[] protectedResourceRequest(String accessToken, HttpServletRequest httpServletRequest);

	public OauthServlet(String path, String name, String iconPath, Credentials credentials) {
		this.path = path;
		this.name = name;
		this.iconPath = iconPath;
		this.credentials = credentials;
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		byte[] responseToSend = null;
		try {
			String accessToken = retrieveAccessToken(httpServletRequest);
			responseToSend = protectedResourceRequest(accessToken, httpServletRequest);
		} catch (OauthServletException e) {
			responseToSend = e.getMessage().getBytes("UTF-8");
		}

		byte[] respBase64 = Base64.encodeBase64(responseToSend);

		httpServletResponse.sendRedirect("/result?data=" + new String(respBase64) + "&servlet="
				+ URLEncoder.encode(getName(), "UTF-8"));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(generateRedirectUrl());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

}
