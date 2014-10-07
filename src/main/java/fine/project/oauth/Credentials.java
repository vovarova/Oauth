package fine.project.oauth;

public class Credentials {

	private String aplicationID;
	private String aplicationSecret;
	private String redirectUrl;
	private String scope;
	private String resourceRequestURL;

	public Credentials(String aplicationID, String aplicationSecret, String redirectUrl, String resourceRequestURL) {
		this.aplicationID = aplicationID;
		this.aplicationSecret = aplicationSecret;
		this.redirectUrl = redirectUrl;
		this.resourceRequestURL = resourceRequestURL;
	}

	public Credentials(String aplicationID, String aplicationSecret, String redirectUrl, String resourceRequestURL,
			String scope) {
		this.aplicationID = aplicationID;
		this.aplicationSecret = aplicationSecret;
		this.redirectUrl = redirectUrl;
		this.scope = scope;
		this.resourceRequestURL = resourceRequestURL;
	}

	public String getAplicationID() {
		return aplicationID;
	}

	public String getAplicationSecret() {
		return aplicationSecret;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getResourceRequestURL() {
		return resourceRequestURL;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
