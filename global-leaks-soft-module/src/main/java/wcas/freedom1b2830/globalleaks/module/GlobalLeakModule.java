package wcas.freedom1b2830.globalleaks.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;

public abstract class GlobalLeakModule implements ModuleInterface {
	private Logger logger;

	public final GlobalLeakConfig config;
	public final String loggerName;
	public GlobalLeakModuleWorker worker;

	protected GlobalLeakModule(final GlobalLeakConfig config, final String loggerName) {
		this.config = config;
		this.loggerName = loggerName;
		worker = new GlobalLeakModuleWorker(this, loggerName) {
			// ok
		};
	}

	public abstract void init() throws Exception;

	protected final Logger log() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(loggerName);
		}
		return logger;
	}

	public abstract void stop() throws Exception;

	/**
	 * for end loop pls use {@link #stopWorker()}
	 *
	 * @throws Exception
	 */
	public abstract void stopInternal() throws Exception;

	/**
	 *
	 */
	protected final void stopWorker() {
		worker.close();
		log().info("worker stoped");
	}

}
