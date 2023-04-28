package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import wcas.freedom1b2830.globalleaks.AtomicCopyOnWriteArrayList;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorkerAction;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.thread.ProxyCheckerThread;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class ProxyCheckerWorker extends GlobalLeakModuleWorker {

	private final CopyOnWriteArrayList<ProxyCheckerThread> threads = new CopyOnWriteArrayList<>();
	public AtomicCopyOnWriteArrayList<ProxyData> queue = new AtomicCopyOnWriteArrayList<>();

	protected ProxyCheckerWorker(final ProxyCheckerModule checkerModule, final String loggerName) {
		super(checkerModule, loggerName);

		actionThreadPreLoop = () -> {
			for (var i = 0; i < checkerModule.config.proxyGCM.checker.maxThreads; i++) {
				final ProxyCheckerThread checker = new ProxyCheckerThread(ProxyCheckerWorker.this, log(), i) {

					protected @Override void end(final ProxyData proxyCheckResult) {
						checkerModule.checkedProxy(proxyCheckResult);
					}
				};
				threads.add(checker);
				checker.start();
			}

		};

		actionThreadLoop = new GlobalLeakModuleWorkerAction() {

			public @Override void exec() throws IOException {
				while (isRunning()) {
					while (queue.isEmpty()) {
						ThreadUtils.sleep(3, TimeUnit.SECONDS);
					}
					ThreadUtils.sleep(3, TimeUnit.SECONDS);
				}
			}
		};
	}

	protected void appendQueue(final ProxyData data) {
		if (queue.size() > 1000) {
			System.out.println("ProxyCheckerWorker.appendQueue():clear");
			return;
		}
		data.beforDB();
		queue.addIfAbsent(data);
	}

}
