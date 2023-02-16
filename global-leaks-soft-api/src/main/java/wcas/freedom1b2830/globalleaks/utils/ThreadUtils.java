package wcas.freedom1b2830.globalleaks.utils;

import java.util.concurrent.TimeUnit;

public final class ThreadUtils {
	public static void sleep(final long time) {
		try {
			Thread.sleep(time);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void sleep(final long time, final TimeUnit timeUnit) {
		sleep(timeUnit.toMillis(time));
	}

	private ThreadUtils() {
	}

}
