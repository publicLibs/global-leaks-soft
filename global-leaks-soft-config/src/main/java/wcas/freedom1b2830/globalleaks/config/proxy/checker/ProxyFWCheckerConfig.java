package wcas.freedom1b2830.globalleaks.config.proxy.checker;

import java.util.concurrent.TimeUnit;

public class ProxyFWCheckerConfig {
	public long maxThreads = 20;
	public long recheckTime = TimeUnit.DAYS.toSeconds(2);
}
