package wcas.freedom1b2830.globalleaks.lang;

import java.util.NoSuchElementException;

public class NoKeyException extends NoSuchElementException {

	public NoKeyException() {
		super();
	}

	public NoKeyException(final String s) {
		super(s);
	}

	public NoKeyException(final String s, final Throwable cause) {
		super(s, cause);
	}

	public NoKeyException(final Throwable cause) {
		super(cause);
	}

	public String getKey() {
		return getMessage();
	}

}
