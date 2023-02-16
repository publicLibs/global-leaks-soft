package wcas.freedom1b2830.globalleaks.config.database;

import wcas.freedom1b2830.globalleaks.config.database.dbms.remote.mysql.DataBaseMysqlConfig;
import wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote.postgresql.DataBasePostgresqlConfig;

public class ProxyFWDataBaseConfig {
	/**
	 * all available DBMS
	 */
	public DBMS[] allExampleDBMS = DBMS.values();// example

	public DBMS activeDBMS = DBMS.POSTGRESQL;
	public DataBasePostgresqlConfig postgresqlConfig = new DataBasePostgresqlConfig();
	public DataBaseMysqlConfig mysqlConfig = new DataBaseMysqlConfig();

}
