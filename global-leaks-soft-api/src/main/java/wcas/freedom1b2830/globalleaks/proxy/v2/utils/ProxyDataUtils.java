package wcas.freedom1b2830.globalleaks.proxy.v2.utils;

import java.io.IOException;

import wcas.freedom1b2830.globalleaks.HostUtils;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;

public class ProxyDataUtils {
	/**
	 *
	 * @param proxyCheckResult прокси для получения данных
	 * @throws AntiPidorException при попадании ненадежного хоста
	 * @throws IOException
	 */
	public static void recognIn(final ProxyData proxyCheckResult) throws AntiPidorException, IOException {
		final var geoipData = HostUtils.geoiplookup(proxyCheckResult.host);
		proxyCheckResult.ASN = geoipData.ASN;
		proxyCheckResult.country = geoipData.country;
	}

	public static void socketError(final ProxyData proxydata) {
		proxydata.socketConnection = Boolean.valueOf(false);
	}

	public static void socketOK(final ProxyData proxyCheckResult) {
		proxyCheckResult.socketConnection = Boolean.valueOf(true);
	}

}
