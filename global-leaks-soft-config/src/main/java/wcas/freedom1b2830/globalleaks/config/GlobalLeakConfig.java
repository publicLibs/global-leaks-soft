package wcas.freedom1b2830.globalleaks.config;

import wcas.freedom1b2830.globalleaks.config.database.ProxyFWDataBaseConfig;
import wcas.freedom1b2830.globalleaks.config.email.EmailGetterModuleConfig;
import wcas.freedom1b2830.globalleaks.config.telegram.BotConfig;

public class GlobalLeakConfig {

	static {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

	public ProxyFWDataBaseConfig database = new ProxyFWDataBaseConfig();
	public ProxyFWProxyGCModule proxyGCM = new ProxyFWProxyGCModule();
	public EmailGetterModuleConfig emailGetterModuleConfig = new EmailGetterModuleConfig();
	public BotConfig telegramBotConfig = new BotConfig();
}
