package wcas.freedom1b2830.globalleaks.proxy.exceptions.detected;

public class MikrotikHttpProxyDetectedException extends Exception {

	private static final long serialVersionUID = 401418591820100048L;

	public MikrotikHttpProxyDetectedException() {
	}

	public MikrotikHttpProxyDetectedException(final String message) {
		super(message);
	}

	public MikrotikHttpProxyDetectedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MikrotikHttpProxyDetectedException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MikrotikHttpProxyDetectedException(final Throwable cause) {
		super(cause);
	}

}
