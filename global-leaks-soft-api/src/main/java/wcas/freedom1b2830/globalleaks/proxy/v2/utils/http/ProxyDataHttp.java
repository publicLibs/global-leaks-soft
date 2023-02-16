package wcas.freedom1b2830.globalleaks.proxy.v2.utils.http;

import java.io.IOException;

import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.ProxyDataResponceUtils;

public class ProxyDataHttp {
	public static void httpOk(final ProxyData proxydata, final int code, final String responceMessge,
			final StringBuilder hostExit) throws IOException, AntiPidorException {
		proxydata.http = Boolean.valueOf(true);
		ProxyDataResponceUtils.responceGetted(proxydata, code, responceMessge, hostExit);
	}
}
