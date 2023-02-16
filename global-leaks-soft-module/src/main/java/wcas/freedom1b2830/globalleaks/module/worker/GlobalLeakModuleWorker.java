package wcas.freedom1b2830.globalleaks.module.worker;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;

public abstract class GlobalLeakModuleWorker extends Thread {
	private Logger logger;

	private boolean active = true;
	public final GlobalLeakModule module;

	public GlobalLeakModuleWorkerAction actionEarlyThread;
	public GlobalLeakModuleWorkerAction actionThreadPreLoop;
	public GlobalLeakModuleWorkerAction actionThreadLoop;
	public GlobalLeakModuleWorkerAction actionThreadAfterLoop;
	public GlobalLeakModuleWorkerAction actionAfterThread;

	public final String loggerName;

	protected GlobalLeakModuleWorker(GlobalLeakModule module, String loggerName) {
		this.module = module;
		this.loggerName = loggerName;
	}

	private final void afterLoop() throws IOException {
		if (actionThreadAfterLoop != null) {
			actionThreadAfterLoop.exec();
		}
	}

	private final void afterThread() throws IOException {
		if (actionAfterThread != null) {
			actionAfterThread.exec();
		}
	}

	public void close() {
		active = false;
	}

	private final void earlyThread() throws IOException {
		if (actionEarlyThread != null) {
			actionEarlyThread.exec();
		}
	}

	public boolean isRunning() {
		return active;
	}

	protected final Logger log() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(loggerName);
		}
		return logger;
	}

	private final void loop() throws IOException {
		while (active) {
			actionThreadLoop.exec();
		}
	}

	private final void preLoop() throws IOException {
		if (actionThreadPreLoop != null) {
			actionThreadPreLoop.exec();
		}
	}

	public final @Override void run() {
		try {
			Thread.currentThread().setName(getClass().getSimpleName());
			earlyThread();
			workerTask();
			afterThread();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void workerTask() throws IOException {
		if (actionThreadLoop != null) {
			preLoop();
			loop();
			afterLoop();
		}
	}

}
