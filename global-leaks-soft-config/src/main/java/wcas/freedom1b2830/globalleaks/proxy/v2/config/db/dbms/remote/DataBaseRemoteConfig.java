package wcas.freedom1b2830.globalleaks.proxy.v2.config.db.dbms.remote;

import wcas.freedom1b2830.globalleaks.config.database.DataBaseConfig;

public class DataBaseRemoteConfig extends DataBaseConfig {
	public String host = "127.0.0.1";
	public Integer port = Integer.valueOf(-1000);

	public String dbName = "DataBaseName";
	public String dbUser = "DataBaseUserName";
	public String dbUserPassword = "DataBaseUserPassword";

}
