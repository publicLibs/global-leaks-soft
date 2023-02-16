package wcas.freedom1b2830.globalleaks.watchdog;

import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;

public class NetworkWatchdog extends GlobalLeakModule {

	boolean networkOk;

	public NetworkWatchdog(final GlobalLeakConfig config) {
		super(config, "NetworkWatchdog");
		worker = new NetworkWatchdogWorker(this, loggerName) {
			// ok
		};
	}

	public @Override boolean canProcess() {

		return true;
	}

	public @Override void init() throws Exception {
		worker.start();
	}

	public boolean isNetworkOk() {
		return networkOk;
	}

	public @Override void stop() throws Exception {
		stopInternal();
	}

	public @Override void stopInternal() throws Exception {
		worker.close();
	}

}
