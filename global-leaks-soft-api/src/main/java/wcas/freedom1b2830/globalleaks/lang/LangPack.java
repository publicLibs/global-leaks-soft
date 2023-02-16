package wcas.freedom1b2830.globalleaks.lang;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// welcome_ru=
public class LangPack {
	private static final LangPack instance = new LangPack();

	/**
	 *
	 * @param key
	 * @return translated key for current jvm
	 */
	static final String jvmLang = Locale.getDefault().getLanguage();

	public static final String fallbackLang = "en";

	private static String convertKey(final String lang, final String key) {
		return key + "_" + lang;
	}

	public static String get(final String key) {
		return get(jvmLang, key);
	}

	/**
	 *
	 * @param lang
	 * @param key
	 * @return translated key for users with lang
	 */
	public static String get(final String lang, final String key) {
		final var langPack = getInstance();
		return langPack.getFromSource(lang, key, true);
	}

	public static String get(final String lang, final String key, final String fallbackMessage) {
		try {
			return get(lang, key);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return fallbackMessage;
	}

	private static LangPack getInstance() {
		return instance;
	}

	public static void main(final String[] args) {

		try {
			System.out.println(get("welcome"));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(get("test_t_aa_td"));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	CopyOnWriteArrayList<File> inputFiles = new CopyOnWriteArrayList<>();

	ConcurrentHashMap<String, ConcurrentHashMap<String, String>> data = new ConcurrentHashMap<>();

	private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

	private final CopyOnWriteArrayList<String> todoLog = new CopyOnWriteArrayList<>();

	final String regex = "(^[a-zA-Z_]+)_([a-z]+)=(.*)";

	final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

	public LangPack() {
		try {
			readSource();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private String getFromSource(final String lang, final String key, final boolean firstIteration) {
		if (data.containsKey(key)) {// нужное слово
			final ConcurrentHashMap<String, String> values = data.get(key);// язык+нужная_строка
			if (values.containsKey(lang)) {
				return values.get(lang);
			}
			readSource();
			if (values.containsKey(fallbackLang)) {
				noLangExist(key, lang);
				return values.get(fallbackLang);
			}
			if (firstIteration) {
				logger.error("no lang:[{}] value for [{}]", lang, key);
				readSource();
				return getFromSource(lang, key, false);
			}
			throw new NoLangException(key, lang);
		}
		if (firstIteration) {
			logger.warn("no key [{}], invoke readSource()", key);
			noLangExist(key, lang);
			readSource();
			return getFromSource(lang, key, false);
		}
		logger.error("no key [{}],end", key);
		noLangExist(key, lang);
		noLangExist(key, fallbackLang);
		throw new NoKeyException(key);
	}

	private void noLangExist(final String key, final String lang) {
		try {
			final String errorData = key + "_" + lang;
			if (todoLog.addIfAbsent(errorData)) {
				logger.warn("no translate data for Key:[{}] Lang:[{}]", key, lang);
				final var errorLogFile = new File(getClass().getCanonicalName());
				if (!errorLogFile.exists()) {
					errorLogFile.createNewFile();
				}
				Files.writeString(errorLogFile.toPath(), errorData + '\n', UTF_8, StandardOpenOption.APPEND);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void parseLineFromSource(final String line) {
		// key_lang=value
		final var matcher = pattern.matcher(line);

		String key = null;
		String lang = null;
		String value = null;
		while (matcher.find()) {
			key = matcher.group(1);
			lang = matcher.group(2);
			value = matcher.group(3);
		}

		final var langValue = data.computeIfAbsent(key, a -> new ConcurrentHashMap<>());// key->lang+value
		langValue.put(lang, value);

	}

	private void readSource() {
		data.clear();
		if (inputFiles.isEmpty()) {
			final var langPackFile = new File("langpack.txt");
			inputFiles.addIfAbsent(langPackFile);
			if (!langPackFile.exists()) {
				try {
					langPackFile.createNewFile();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		inputFiles.parallelStream().filter(File::exists).filter(File::isFile).forEachOrdered((final var file) -> {
			try {
				Files.lines(file.toPath(), UTF_8).filter(line -> !line.isEmpty())
						.forEachOrdered(this::parseLineFromSource);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});

	}

}
