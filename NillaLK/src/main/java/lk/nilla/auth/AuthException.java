package lk.nilla.auth;

/** Generic exception to handle exceptions in Authentication that are not similar to ExpiredJwtException
 */
public class AuthException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthException() {
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AuthException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
