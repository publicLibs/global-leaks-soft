package wcas.freedom1b2830.globalleaks;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public enum LogMode {
	DEBUG, INFO, NONE, WARN, CRIT;

	public static LogMode global = LogMode.DEBUG;

	public static boolean forLog(final LogMode current, final LogMode testMode) {
		return switch (current) {
		case NONE -> false;
		case DEBUG -> forLogDebug(testMode);
		case INFO -> forLogInfo(testMode);
		case WARN -> forLogWarn(testMode);
		case CRIT -> forLogCrit(testMode);
		default -> throw new IllegalArgumentException("Unexpected value: " + current);
		};
	}

	static boolean forLogCrit(final LogMode testMode) {
		return switch (testMode) {
		case CRIT -> true;
		default -> false;
		};
	}

	static boolean forLogDebug(final LogMode testMode) {
		return testMode != NONE;
	}

	public static boolean forLogGlobal(@NotNull final LogMode testMode) {
		return forLogGlobalList(global, testMode);
	}

	public static boolean forLogGlobalList(final LogMode... testMode) {
		return Arrays.asList(testMode).parallelStream().anyMatch(logMode -> forLog(global, logMode));
	}

	private static boolean forLogInfo(final LogMode testMode) {
		return switch (testMode) {
		case INFO, WARN, CRIT -> true;
		default -> false;
		};
	}

	static boolean forLogWarn(final LogMode testMode) {
		return switch (testMode) {
		case WARN, CRIT -> true;
		default -> false;
		};
	}

}
