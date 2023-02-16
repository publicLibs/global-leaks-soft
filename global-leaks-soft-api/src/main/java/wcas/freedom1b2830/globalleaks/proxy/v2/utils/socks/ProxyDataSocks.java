package wcas.freedom1b2830.globalleaks.proxy.v2.utils.socks;

import java.io.IOException;

import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.ProxyDataResponceUtils;

public class ProxyDataSocks {

	public static void socksOk(final ProxyData proxydata, final int httpStatusCode, final String responseMessage,
			final StringBuilder httpResponce) throws IOException, AntiPidorException {
		proxydata.socks = Boolean.valueOf(true);
		ProxyDataResponceUtils.responceGetted(proxydata, httpStatusCode, responseMessage, httpResponce);
	}

}
