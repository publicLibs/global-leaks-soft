package wcas.freedom1b2830.globalleaks.module.impl.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.ProxyDataResponceUtils;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class GlobalLeaksDataBaseWorker extends GlobalLeakModuleWorker {

	protected GlobalLeaksDataBaseWorker(final GlobalLeaksDataBase databaseModule, final String loggerName) {
		super(databaseModule, loggerName);

		// loop
		actionThreadLoop = () -> {

			while (!databaseModule.canProcess()) {
				log().info("cant process");
				ThreadUtils.sleep(1, TimeUnit.SECONDS);
			}
			try {
				final var queryBuilder = databaseModule.proxyDao.queryBuilder();
				final var where = queryBuilder.where();
				where.not().eq("cloudflare", Boolean.valueOf(true));

				final var recheckTime = databaseModule.config.proxyGCM.checker.recheckTime;
				final var currentTime = System.currentTimeMillis() / 1000;

				final var checkTime = currentTime - recheckTime;// время в прошлом, если время в бд меньше этого
																// времени то
				// брать
				// lastChecked

				queryBuilder.limit(Long.valueOf(500L));
				where.and().le("lastChecked", Long.valueOf(checkTime));
				System.err.println("noCloudflar1e: " + queryBuilder.prepareStatementString());
				queryBuilder.query().parallelStream().forEachOrdered(databaseModule::nocheckedInDB);
			} catch (final Exception e) {
				e.printStackTrace();
			}

			try {
				final var queryBuilder1 = databaseModule.proxyDao.queryBuilder();
				final var where1 = queryBuilder1.where();
				final var nonull = where1.isNotNull("httpResponse");
				final var hostNoExist = nonull.and().isNull("hostExit");

				final var pre = hostNoExist.and().not().eq("cloudflare", Boolean.valueOf(true));

				System.err.println(pre.getStatement());
				final var hostNoExistList = pre.query();

				hostNoExistList.parallelStream().forEachOrdered(proxy -> {
					try {
						final var data = ProxyDataResponceUtils.recognExit(proxy, proxy.httpResponse);
						// ProxyDataUtils.recognExit(proxy, proxy.httpResponse);
						databaseModule.saveProxy(data);

					} catch (final AntiPidorException e) {
						try {
							databaseModule.deleteProxy(proxy);
							log().info("APE error [{}]", e.getMessage());
						} catch (final SQLException e1) {
							e1.printStackTrace();
						}
					} catch (IOException | SQLException e) {
						log().info("fix error [{}]", e.getMessage());
					}
				});

			} catch (final SQLException e1) {
				e1.printStackTrace();
			}

			ThreadUtils.sleep(1, TimeUnit.MINUTES);
		};

	}

}
