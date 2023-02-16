package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker;

import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;

public abstract class ProxyCheckerModule extends GlobalLeakModule {

	protected ProxyCheckerModule(final GlobalLeakConfig config) {
		super(config, ProxyCheckerModule.class.getSimpleName());
		worker = new ProxyCheckerWorker(this, loggerName) {

		};
	}

	public void append(final ProxyData proxyData) {
		if (worker instanceof ProxyCheckerWorker) {
			final var cworker = (ProxyCheckerWorker) worker;
			cworker.appendQueue(proxyData);
		} else {
			throw new IllegalStateException("AAA");
		}
	}

	protected abstract void checkedProxy(ProxyData data);

	@Override
	public void init() throws Exception {
		worker.start();
		if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
			log().info("worker started");
		}
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void stopInternal() throws Exception {

	}
}
