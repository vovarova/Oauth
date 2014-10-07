package fine.project.oauth;

public class OauthServletException extends Exception {
	private static final long serialVersionUID = 2271255829608395642L;

	public OauthServletException(String message) {
		super(message);
	}

	public OauthServletException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
