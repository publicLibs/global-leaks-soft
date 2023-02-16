package wcas.freedom1b2830.globalleaks.proxy.v2.utils;

import java.io.IOException;

import wcas.freedom1b2830.globalleaks.HostUtils;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;

public class ProxyDataResponceUtils {

	public static ProxyData recognExit(final ProxyData proxyForCheck, String hostString)
			throws IOException, AntiPidorException {
		if (hostString == null || hostString.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("null||empty: proxy:%s data:[%s]", proxyForCheck.proxyID, hostString));
		}
		hostString = hostString.replaceAll("\n", "");

		proxyForCheck.httpResponse = hostString;
		final var geoipData = HostUtils.geoiplookup(hostString);
		proxyForCheck.hostExit = hostString;
		proxyForCheck.ASNExit = geoipData.ASN;
		proxyForCheck.countryExit = geoipData.country;
		return proxyForCheck;
	}

	public static void responceGetted(final ProxyData proxydata, final int httpStatusCode,
			final String httpStatusMessage, final StringBuilder httpResponce) throws IOException, AntiPidorException {
		proxydata.httpCode = httpStatusCode;
		proxydata.httpReason = httpStatusMessage;
		proxydata.httpResponse = httpResponce.toString();
		recognExit(proxydata, httpResponce.toString());
	}
}
