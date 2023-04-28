package wcas.freedom1b2830.globalleaks.watchdog;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorkerAction;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class NetworkWatchdogWorker extends GlobalLeakModuleWorker {

	protected NetworkWatchdogWorker(final NetworkWatchdog networkWatchdog, final String loggerNameInput) {
		super(networkWatchdog, loggerNameInput);
		actionThreadPreLoop = new GlobalLeakModuleWorkerAction() {
			public @Override void exec() throws IOException {
				setName(loggerName);
			}
		};

		actionThreadLoop = () -> {
			try {
				final var url = URI.create("http://ident.me").toURL();
				try (var is = url.openStream()) {
					networkWatchdog.networkOk = true;
				}
			} catch (final Exception e) {
				networkWatchdog.networkOk = false;
			}
			ThreadUtils.sleep(10, TimeUnit.SECONDS);
		};

	}

}
