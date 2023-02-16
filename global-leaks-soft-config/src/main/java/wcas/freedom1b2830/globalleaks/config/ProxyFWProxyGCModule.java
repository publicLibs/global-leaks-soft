package wcas.freedom1b2830.globalleaks.config;

import wcas.freedom1b2830.globalleaks.config.proxy.checker.ProxyFWCheckerConfig;
import wcas.freedom1b2830.globalleaks.proxy.v2.config.getter.web.ProxyFWGetterWEBConfig;

public class ProxyFWProxyGCModule {
	public ProxyFWCheckerConfig checker = new ProxyFWCheckerConfig();
	public ProxyFWGetterWEBConfig webGetterConfig = new ProxyFWGetterWEBConfig();
}
