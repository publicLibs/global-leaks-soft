package wcas.freedom1b2830.globalleaks.watchdog;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorkerAction;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class NetworkWatchdogWorker extends GlobalLeakModuleWorker {

	protected NetworkWatchdogWorker(final NetworkWatchdog networkWatchdog, final String loggerName) {
		super(networkWatchdog, loggerName);
		actionThreadPreLoop = new GlobalLeakModuleWorkerAction() {
			@Override
			public void exec() throws IOException {
				setName(loggerName);
			}
		};

		actionThreadLoop = new GlobalLeakModuleWorkerAction() {

			@Override
			public void exec() throws IOException {
				try {
					final var url = new URL("http://ident.me");
					try (var is = url.openStream()) {
						networkWatchdog.networkOk = true;
					}
				} catch (final Exception e) {
					networkWatchdog.networkOk = false;
				}
				ThreadUtils.sleep(10, TimeUnit.SECONDS);
			}
		};

	}

}
