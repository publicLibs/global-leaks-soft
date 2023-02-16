package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.getter.web;

import java.io.IOException;

import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;

public abstract class ProxyFWWEbGetter extends GlobalLeakModule {

	protected ProxyFWWEbGetter(final GlobalLeakConfig config) {
		super(config, ProxyFWWEbGetter.class.getSimpleName());
		worker = new ProxyFWWEbGetterWorker(this, loggerName) {
			public @Override void rawProxy(final ProxyData proxyData) {
				ProxyFWWEbGetter.this.rawProxy(proxyData);
			}
		};
		if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
			log().info("created");
		}
	}

	public @Override void init() throws Exception {
		worker.start();
	}

	public abstract void rawProxy(ProxyData proxyData);

	@Override
	public void stopInternal() throws IOException, InterruptedException {

	}

}
