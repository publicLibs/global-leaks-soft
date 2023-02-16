package wcas.freedom1b2830.globalleaks.proxy.v2.exceptions;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1955679228725294273L;

	public ApiException() {
	}

	public ApiException(final String message) {
		super(message);
	}

	public ApiException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ApiException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApiException(final Throwable cause) {
		super(cause);
	}

}
