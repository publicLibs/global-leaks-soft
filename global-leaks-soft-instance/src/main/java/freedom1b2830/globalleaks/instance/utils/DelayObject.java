package freedom1b2830.globalleaks.instance.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public final class DelayObject implements Serializable {
	private static final long serialVersionUID = 4985907762335121702L;

	public Long delay = Long.valueOf(10);

	// new Long(10);
	public TimeUnit timeUnit = TimeUnit.MINUTES;

	public DelayObject() {
	}

	public DelayObject(Long delay, TimeUnit timeUnit) {
		this.delay = delay;
		this.timeUnit = timeUnit;
	}
}
