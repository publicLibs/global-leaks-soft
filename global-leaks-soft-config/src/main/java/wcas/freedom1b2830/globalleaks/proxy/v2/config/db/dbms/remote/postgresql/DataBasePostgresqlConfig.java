package wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote.postgresql;

import wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote.DataBaseRemoteConfig;

public class DataBasePostgresqlConfig extends DataBaseRemoteConfig {
	public DataBasePostgresqlConfig() {
		port = Integer.valueOf(5432);
	}
}
