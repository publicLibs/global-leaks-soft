package wcas.freedom1b2830.globalleaks.proxy.exceptions;

public class CloudflareDetectedException extends Exception {

	private static final long serialVersionUID = 6178257843933668689L;

	public CloudflareDetectedException() {
		super();
	}

	public CloudflareDetectedException(final String message) {
		super(message);
	}

	public CloudflareDetectedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CloudflareDetectedException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CloudflareDetectedException(final Throwable cause) {
		super(cause);
	}
}
