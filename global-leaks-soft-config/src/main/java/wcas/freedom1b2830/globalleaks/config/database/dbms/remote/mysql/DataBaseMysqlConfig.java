package wcas.freedom1b2830.globalleaks.config.database.dbms.remote.mysql;

import wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote.DataBaseRemoteConfig;

public class DataBaseMysqlConfig extends DataBaseRemoteConfig {
	public DataBaseMysqlConfig() {
		port = Integer.valueOf(3306);
	}

}
