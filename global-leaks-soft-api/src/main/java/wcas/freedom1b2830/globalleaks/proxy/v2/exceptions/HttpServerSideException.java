package wcas.freedom1b2830.globalleaks.proxy.v2.exceptions;

public class HttpServerSideException extends Exception {

	private static final long serialVersionUID = 4282291065139825705L;

	public HttpServerSideException() {
	}

	public HttpServerSideException(final String message) {
		super(message);
	}

	public HttpServerSideException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HttpServerSideException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpServerSideException(final Throwable cause) {
		super(cause);
	}
}
