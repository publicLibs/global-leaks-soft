package wcas.freedom1b2830.globalleaks.proxy.v2.exceptions;

public class AntiPidorException extends Exception {

	private static final long serialVersionUID = -7460359139861572866L;

	public AntiPidorException() {
	}

	public AntiPidorException(final String message) {
		super(message);
	}

	public AntiPidorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AntiPidorException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AntiPidorException(final Throwable cause) {
		super(cause);
	}
}
