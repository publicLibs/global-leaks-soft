package wcas.freedom1b2830.globalleaks.module;

import org.jetbrains.annotations.NotNull;

import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;

public abstract class EmailGetterModule extends GlobalLeakModule {

	protected EmailGetterModule(final GlobalLeakConfig config) {
		super(config, EmailGetterModule.class.getSimpleName());
		worker = new EmailGetterModuleWorker(this, loggerName) {

			@Override
			public void httpClientException(final Exception e) {
				log().error("httpClientException ", e);

			}

			@Override
			public void parsedInputEmail(@NotNull final String email) {
			}

			@Override
			public void rawInputWebPage(@NotNull final String line) {
				if (!line.contains("postfix")) {
					return;
				}

				if (line.contains("postfix/smtp")) {
					if (line.contains(": warning: disabling connection caching")) {
						return;
					}
					if (line.contains("connect to")
							&& (line.contains("Connection timed out") || line.contains("Connection refused"))) {
						return;
					}
					if (line.contains(": warning: ")) {
						if (line.contains("Address family not supported by protocol")) {
							return;
						}
						log().info(line);
						return;
					}
					if (line.contains(": connect to ")) {
						if (line.contains("No route to host")) {
							return;
						}

						System.out.println(line);
						return;
					}

					if (line.contains(": host ")) {
						return;
					}
					if (line.contains(": lost ")) {
						return;
					}
					if (line.contains(": to=")) {
						return;
					}

					log().info(line);
					return;
				}
				if (line.contains("postfix/qmgr")) {
					return;
				}
				if (line.contains("postfix/cleanup")) {
					return;
				}
				if (line.contains("postfix/local")) {
					return;
				}
				if (line.contains("postfix/sendmail")) {
					return;
				}
				if (line.contains("postfix/postdrop")) {
					return;
				}
				if (line.contains("postfix/pickup")) {
					return;
				}
				if (line.contains("postfix/anvil")) {
					return;
				}
				if (line.contains("postfix/bounce")) {
					return;
				}

				log().warn(line);
			}

			@Override
			public void sourceException(final Exception e) {
				log().error("sourceException ", e);
			}

		};
		if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
			log().info("created");
		}
	}

	@Override
	public void init() throws Exception {
		worker.start();
		if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
			log().debug("worker started");
		}
	}

	@Override
	public void stop() throws Exception {
		stopInternal();
		log().info("stoped");
	}

	@Override
	public void stopInternal() throws Exception {

	}

}
