package wcas.freedom1b2830.globalleaks.lang;

import java.util.NoSuchElementException;

public class NoLangException extends NoSuchElementException {
	private final String key;
	private final String lang;

	public NoLangException(final String key, final String lang) {
		super(lang);
		this.key = key;
		this.lang = lang;
	}

	public NoLangException(final String key, final String lang, final Throwable cause) {
		super(lang, cause);
		this.key = key;
		this.lang = lang;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return lang;
	}
}
