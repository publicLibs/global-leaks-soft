package wcas.freedom1b2830.globalleaks.proxy.v2.exceptions;

public class AuthProxyNeededException extends Exception {

	private static final long serialVersionUID = 8864553909291038905L;

	public AuthProxyNeededException() {
	}

	public AuthProxyNeededException(final String message) {
		super(message);
	}

	public AuthProxyNeededException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AuthProxyNeededException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthProxyNeededException(final Throwable cause) {
		super(cause);
	}
}
