package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.thread;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyType;
import wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.ProxyCheckerWorker;
import wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.utils.ProxyConnectUtils;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class ProxyCheckerThread extends Thread {

	private final Logger logger;
	private final ProxyCheckerWorker worker;
	private final int id;

	public ProxyCheckerThread(final ProxyCheckerWorker worker, final Logger log, final int id) {
		this.worker = worker;
		this.logger = log;
		this.id = id;
	}

	protected abstract void end(ProxyData proxyCheckResult);

	@Override
	public void run() {
		while (true) {
			while (worker.queue.isEmpty()) {
				ThreadUtils.sleep(1, TimeUnit.SECONDS);
			}
			while (!worker.module.canProcess()) {
				logger.info("cant process");
				ThreadUtils.sleep(1, TimeUnit.SECONDS);
			}

			final ProxyData proxyCheckResult = worker.queue.next();
			final String className = ProxyCheckerThread.class.getSimpleName();
			final String host = proxyCheckResult.host;
			final Integer port = proxyCheckResult.port;
			final String tname = className + " id=" + id + "-" + host + ":" + port;
			Thread.currentThread().setName(tname);

			proxyCheckResult.lastChecked = System.currentTimeMillis() / 1000;
			proxyCheckResult.type = ProxyType.CHECKED;

			Thread.currentThread().setName(tname + "-check");
			ProxyConnectUtils.fulltest(proxyCheckResult);
			Thread.currentThread().setName(tname + "-ending");
			end(proxyCheckResult);

			Thread.currentThread().setName(tname + "-ended");
		} // while
	}
}
