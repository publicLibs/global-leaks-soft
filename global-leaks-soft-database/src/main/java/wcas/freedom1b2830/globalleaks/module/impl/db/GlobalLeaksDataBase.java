package wcas.freedom1b2830.globalleaks.module.impl.db;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.config.database.DataBaseConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;
import wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote.DataBaseRemoteConfig;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.customers.Customers;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class GlobalLeaksDataBase extends GlobalLeakModule {

	protected Dao<ProxyData, Integer> proxyDao;
	public Dao<Customers, Long> customersDao;
	private JdbcPooledConnectionSource connectionSource;

	protected GlobalLeaksDataBase(final GlobalLeakConfig config) {
		super(config, GlobalLeaksDataBase.class.getSimpleName());
		worker = new GlobalLeaksDataBaseWorker(this, loggerName) {
		};
		if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
			log().info("created");
		}
	}

	protected void connect() throws SQLException {
		final var dataBaseConfig = config.database;
		DataBaseConfig activeDBConfig;
		String type;

		switch (dataBaseConfig.activeDBMS) {
		case MYSQL: {
			activeDBConfig = dataBaseConfig.mysqlConfig;
			type = "mysql";
			break;
		}
		case POSTGRESQL: {
			activeDBConfig = dataBaseConfig.postgresqlConfig;
			type = "postgresql";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + dataBaseConfig);
		}

		final var stringBuilder = new StringBuilder();
		stringBuilder.append("jdbc:");

		if (activeDBConfig instanceof final DataBaseRemoteConfig dataBaseRemoteConfig) {
			final var user = dataBaseRemoteConfig.dbUser;
			final var passwd = dataBaseRemoteConfig.dbUserPassword;

			stringBuilder.append(type).append("://");

			stringBuilder.append(dataBaseRemoteConfig.host).append(":").append(dataBaseRemoteConfig.port);
			stringBuilder.append("/");
			stringBuilder.append(dataBaseRemoteConfig.dbName);

			// props.setProperty("ssl", "true");
			connectionSource = new JdbcPooledConnectionSource(stringBuilder.toString(), user, passwd);
		} else {
			throw new UnsupportedOperationException("Unsupported DBMS:" + dataBaseConfig.activeDBMS);
		}
		// h2: jdbc:h2:mem:myDb

		createTables();
		createDAOs();

	}

	public boolean createCustomers(final Customers customer) throws SQLException {
		if (customersDao.idExists(customer.id)) {
			return false;
		}
		customersDao.create(customer);
		return true;
	}

	private void createDAOs() throws SQLException {
		proxyDao = DaoManager.createDao(connectionSource, ProxyData.class);
		customersDao = DaoManager.createDao(connectionSource, Customers.class);
	}

	private void createTables() throws SQLException {
		TableUtils.createTableIfNotExists(connectionSource, ProxyData.class);
		TableUtils.createTableIfNotExists(connectionSource, Customers.class);
	}

	public void deleteProxy(final ProxyData proxy) throws SQLException {
		proxyDao.deleteById(proxy.proxyID);
	}

	public @Override void init() throws Exception {
		connect();
		worker.start();
	}

	public abstract void nocheckedInDB(ProxyData proxyData);

	/**
	 *
	 * @param proxyData
	 * @throws SQLException
	 */
	public final void saveProxy(final ProxyData proxyData) throws SQLException {
		try {

			final var id = proxyData.beforDB();
			if (proxyDao.idExists(id)) {
				final var proxyDataFromDB = proxyDao.queryForId(id);

				if (proxyDataFromDB == null) {
					ThreadUtils.sleep(1, TimeUnit.SECONDS);
					saveProxy(proxyData);
					return;
				}

				if (proxyData.lastChecked > proxyDataFromDB.lastChecked) {
					proxyDataFromDB.lastChecked = proxyData.lastChecked;

					if (proxyData.socketConnection.equals(Boolean.valueOf(false))) {
						deleteProxy(proxyData);
						return;
					}

					proxyDataFromDB.user = proxyData.user;
					proxyDataFromDB.password = proxyData.password;

					proxyDataFromDB.country = proxyData.country;
					proxyDataFromDB.ASN = proxyData.ASN;

					proxyDataFromDB.socketConnection = proxyData.socketConnection;

					proxyDataFromDB.type = proxyData.type;

					proxyDataFromDB.http = proxyData.http;
					proxyDataFromDB.socks = proxyData.socks;

					proxyDataFromDB.httpCode = proxyData.httpCode;
					proxyDataFromDB.httpReason = proxyData.httpReason;

					proxyDataFromDB.httpResponse = proxyData.httpResponse;

					proxyDataFromDB.hostExit = proxyData.hostExit;
					proxyDataFromDB.ASNExit = proxyData.ASNExit;
					proxyDataFromDB.countryExit = proxyData.countryExit;

					proxyDataFromDB.cloudflare = proxyData.cloudflare;
					proxyDataFromDB.tor = proxyData.tor;
					proxyDataFromDB.mikrotikhttpproxy = proxyData.mikrotikhttpproxy;

					proxyDao.update(proxyDataFromDB);
				}
			} else {
				proxyDao.create(proxyData);
			}
		}

		catch (final SQLException e) {
			final var casee = e.getCause();
			if (casee != null) {
				if (casee instanceof final SQLIntegrityConstraintViolationException e2) {
					System.err.println(e.getClass() + " " + e2.getClass() + " " + proxyData.proxyID);
					return;
				}
			}
			e.printStackTrace();
		} catch (final NullPointerException e) {
			log().error("DB NPE:", e);
		}

	}

	public @Override void stop() throws Exception {
		stopInternal();
	}

	public @Override void stopInternal() throws Exception {
		connectionSource.close();
	}

}
