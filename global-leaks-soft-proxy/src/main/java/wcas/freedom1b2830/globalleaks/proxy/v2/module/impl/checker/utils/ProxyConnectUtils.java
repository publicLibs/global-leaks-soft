package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.proxy.ProxyTypes;
import wcas.freedom1b2830.globalleaks.proxy.exceptions.CloudflareDetectedException;
import wcas.freedom1b2830.globalleaks.proxy.exceptions.TorDetectedException;
import wcas.freedom1b2830.globalleaks.proxy.exceptions.detected.MikrotikHttpProxyDetectedException;
import wcas.freedom1b2830.globalleaks.proxy.exceptions.detected.socketconnected.SocketForProxyNotConnectedException;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyType;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AntiPidorException;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.ApiException;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.AuthProxyNeededException;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.ProxyDataUtils;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.http.ProxyDataHttp;
import wcas.freedom1b2830.globalleaks.proxy.v2.utils.socks.ProxyDataSocks;

public class ProxyConnectUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConnectUtils.class.getSimpleName());
	private static final Pattern IPV4_PATTERN = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");
	private static final Pattern IPV6_PATTERN = Pattern.compile(
			"(?i)(?:[\\da-f]{0,4}:){1,7}(?:(?<ipv4>(?:(?:25[0-5]|2[0-4]\\d|1?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|1?\\d\\d?))|[\\da-f]{0,4})");

	static Timeout timeout = Timeout.ofSeconds(10);

	private static final HttpHost target = new HttpHost("http", "ident.me", 80);

	/**
	 * @param response
	 * @param proxydata
	 * @throws AuthProxyNeededException
	 */
	private static void checkIfAuthNeeded(final ClassicHttpResponse response, final ProxyData proxydata)
			throws AuthProxyNeededException {

		final var httpCode = response.getCode();
		if (httpCode == 407) {
			throw new AuthProxyNeededException(proxydata.host);
		}

	}

	/**
	 * @param response
	 * @param proxydata
	 * @throws CloudflareDetectedException
	 *
	 */
	private static void checkIfCloudflare(final ClassicHttpResponse response, final ProxyData proxydata)
			throws CloudflareDetectedException {
		final var headersIterator = response.headerIterator();
		while (headersIterator.hasNext()) {
			final var header = headersIterator.next();
			final var headerName = header.getName();
			final var headerValue = header.getValue();
			if (headerName.equalsIgnoreCase("server")) {
				if (headerValue.equalsIgnoreCase("cloudflare")) {
					throw new CloudflareDetectedException(proxydata.host);
				}
			}
		}
	}

	/**
	 * @param response
	 * @param proxydata
	 */
	private static void checkIfForbidden(final ClassicHttpResponse response, final ProxyData proxydata) {
		final var httpCode = response.getCode();
		if (httpCode == 403) {

		}
	}

	/**
	 * @param http
	 * @param proxydata
	 * @throws TorDetectedException
	 *
	 */
	private static void checkIfTor(final ProxyData proxydata, final StringBuilder http) throws TorDetectedException {
		if (http.toString().contains("title>Tor is not an HTTP Proxy</title")) {
			throw new TorDetectedException(proxydata.host);
		}
		if (http.toString().contains("title>This is a SOCKS Proxy, Not An HTTP Proxy</title")) {
			throw new TorDetectedException(proxydata.host);
		}

	}

	public static void fulltest(final ProxyData proxydata) {
		ProxyConnectUtils.preTest(proxydata);

		final var socketCon = ProxyConnectUtils.socketCanConnect(proxydata);
		if (socketCon.booleanValue()) {// socket
			while (true) {
				ProxyDataUtils.socketOK(proxydata);
				try {
					final var http = new StringBuilder();
					if (ProxyConnectUtils.httpToConnect2(proxydata, http, ProxyTypes.HTTPS)) {
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							LOGGER.info("https {}", http);
						}
						break;
					}
					if (ProxyConnectUtils.httpToConnect2(proxydata, http, ProxyTypes.HTTP)) {
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							LOGGER.info("http {}", http);
						}
						break;
					}

					// socks4
					final var socks4Builder = new StringBuilder();
					if (ProxyConnectUtils.socks4ToConnect(proxydata, socks4Builder)) {
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							LOGGER.info("socks4 {}", socks4Builder);
						}
						break;
					}
					if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
						LOGGER.info("TODO");
					}
					// var socks5Builder = new StringBuilder();
					// ProxyConnectUtils.socks5ToConnect(proxyAddr, socks5Builder);

					break;
				} catch (final AuthProxyNeededException e) {
					e.printStackTrace();
					break;
				} catch (final MikrotikHttpProxyDetectedException e) {
					proxydata.mikrotikhttpproxy = true;
					e.printStackTrace();
					break;
				} catch (final CloudflareDetectedException e) {
					proxydata.cloudflare = true;
					e.printStackTrace();
					break;
				} catch (final TorDetectedException e) {
					e.printStackTrace();
					proxydata.tor = true;
					try {
						final var tors = new File("tor.ips.txt");
						if (!tors.exists()) {
							tors.createNewFile();
						}
						final var hp = proxydata.host + ":" + proxydata.port + '\n';
						Files.writeString(tors.toPath(), hp, StandardOpenOption.APPEND);
					} catch (final Exception e2) {
						e2.printStackTrace();
					}
					break;
				}
			}
		} else {// socket
			ProxyDataUtils.socketError(proxydata);
		}

		// after test
		ProxyConnectUtils.postTest(proxydata);
	}

	public static boolean httpToConnect2(final ProxyData proxydata, final StringBuilder http, final ProxyTypes types)
			throws CloudflareDetectedException, AuthProxyNeededException, TorDetectedException,
			MikrotikHttpProxyDetectedException {
		if (types != ProxyTypes.HTTPS && types != ProxyTypes.HTTP) {
			throw new IllegalArgumentException("no " + types);
		}

		final var requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).build();
		final var connectionConfigBuilder = ConnectionConfig.custom();
		connectionConfigBuilder.setConnectTimeout(timeout);
		final var connectionConfig = connectionConfigBuilder.build();
		final var proxyHost = new HttpHost(types.name().toLowerCase(), proxydata.host, proxydata.port.intValue());
		final var routePlanner = new DefaultProxyRoutePlanner(proxyHost);
		final var connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
		connectionManagerBuilder.setDefaultConnectionConfig(connectionConfig);

		try (final var connectionManager = connectionManagerBuilder.build();
				var httpclient = HttpClients.custom().setRoutePlanner(routePlanner)
						.setConnectionManager(connectionManager).build()) {
			final var request = new HttpGet("/");
			request.setConfig(requestConfig);

			final var context = new BasicHttpContext();

			try (var response = httpclient.executeOpen(target, request, context)) {
				checkIfCloudflare(response, proxydata);
				checkIfAuthNeeded(response, proxydata);

				final var httpCode = response.getCode();
				if (httpCode == 403) {
					return false;
				}

				try (var entity = response.getEntity();
						var contentStream = entity.getContent();
						var reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8))) {
					reader.lines().forEachOrdered(line -> {
						if (IPV4_PATTERN.matcher(line).matches()) {
							http.append(line);
						} else {
							http.append(line).append('\n');
						}
					});
				} // response

				if (http.length() > 100) {
					checkIfTor(proxydata, http);
					if (http.toString().contains("Mikrotik HttpProxy")) {
						throw new MikrotikHttpProxyDetectedException(proxydata.host);
					}

					if (LogMode.forLogGlobalList(LogMode.CRIT)) {
						LOGGER.warn("unwanted data:{}", http);
					}
					return false;
				}
				ProxyDataHttp.httpOk(proxydata, response.getCode(), response.getReasonPhrase(), http);
				return true;
			}
		} catch (NoHttpResponseException | SocketException | SocketTimeoutException e) {
			return false;
		} catch (final AuthProxyNeededException | MikrotikHttpProxyDetectedException | CloudflareDetectedException
				| TorDetectedException e) {
			throw e;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(final String[] args) {
		final var proxyDatas = new CopyOnWriteArrayList<ProxyData>();
		try {
			proxyDatas.add(ProxyData.parse("103.153.254.198:80"));
			proxyDatas.add(ProxyData.parse("103.170.122.208:80"));
			proxyDatas.add(ProxyData.parse("107.21.38.230:9050:8080"));
			proxyDatas.add(ProxyData.parse("108.26.234.212:9050:8080"));
			proxyDatas.add(ProxyData.parse("116.203.227.24:9050:8080"));
			proxyDatas.add(ProxyData.parse("119.81.194.52:12315:8080"));
			proxyDatas.add(ProxyData.parse("122.248.197.121:9050:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.46:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.47:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.47:80"));
			proxyDatas.add(ProxyData.parse("125.141.133.48:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.49:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.98:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.133.99:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.139.112:5566:8080"));
			proxyDatas.add(ProxyData.parse("125.141.139.60:5566:8080"));
			proxyDatas.add(ProxyData.parse("128.199.172.229:80"));
			proxyDatas.add(ProxyData.parse("134.209.237.29:9050:8080"));
			proxyDatas.add(ProxyData.parse("134.209.44.126:80"));
			proxyDatas.add(ProxyData.parse("135.181.79.170:80"));
			proxyDatas.add(ProxyData.parse("13.57.50.68:3128"));
			proxyDatas.add(ProxyData.parse("137.74.61.115:9100:8080"));
			proxyDatas.add(ProxyData.parse("139.130.87.162:80"));
			proxyDatas.add(ProxyData.parse("139.130.87.162:8080"));
			proxyDatas.add(ProxyData.parse("139.162.151.176:9050:8080"));
			proxyDatas.add(ProxyData.parse("139.99.133.41:80"));
			proxyDatas.add(ProxyData.parse("142.132.238.18:9050:8080"));
			proxyDatas.add(ProxyData.parse("157.245.62.1:9050:8080"));
			proxyDatas.add(ProxyData.parse("159.203.70.88:9100:8080"));
			proxyDatas.add(ProxyData.parse("159.65.26.244:80"));
			proxyDatas.add(ProxyData.parse("159.69.204.95:9100:8080"));
			proxyDatas.add(ProxyData.parse("161.97.160.158:80"));
			proxyDatas.add(ProxyData.parse("167.86.114.19:80"));
			proxyDatas.add(ProxyData.parse("168.119.118.84:80"));
			proxyDatas.add(ProxyData.parse("168.119.118.84:8080"));
			proxyDatas.add(ProxyData.parse("172.104.158.151:80"));
			proxyDatas.add(ProxyData.parse("172.105.201.56:19151:8080"));
			proxyDatas.add(ProxyData.parse("173.212.220.213:20939:8080"));
			proxyDatas.add(ProxyData.parse("173.212.220.213:20940:8080"));
			proxyDatas.add(ProxyData.parse("173.255.199.69:19151:8080"));
			proxyDatas.add(ProxyData.parse("176.9.248.241:80"));
			proxyDatas.add(ProxyData.parse("177.93.51.168:80"));
			proxyDatas.add(ProxyData.parse("178.154.228.16:9050:8080"));
			proxyDatas.add(ProxyData.parse("178.18.248.104:49153:8080"));
			proxyDatas.add(ProxyData.parse("178.252.104.64:9050:8080"));
			proxyDatas.add(ProxyData.parse("178.62.227.85:80"));
			proxyDatas.add(ProxyData.parse("179.43.140.131:9050:8080"));
			proxyDatas.add(ProxyData.parse("18.184.15.209:9050:8080"));
			proxyDatas.add(ProxyData.parse("185.165.169.199:10846:8080"));
			proxyDatas.add(ProxyData.parse("185.208.172.248:9051:8080"));
			proxyDatas.add(ProxyData.parse("185.217.199.5:17018:8080"));
			proxyDatas.add(ProxyData.parse("185.245.96.112:1337:8080"));
			proxyDatas.add(ProxyData.parse("188.40.96.177:9050:8080"));
			proxyDatas.add(ProxyData.parse("197.232.152.244:80"));
			proxyDatas.add(ProxyData.parse("198.186.192.83:9050:8080"));
			proxyDatas.add(ProxyData.parse("198.199.86.169:9050:8080"));
			proxyDatas.add(ProxyData.parse("198.98.51.120:31450:8080"));
			proxyDatas.add(ProxyData.parse("200.55.247.3:80"));
			proxyDatas.add(ProxyData.parse("202.61.251.201:9097:8080"));
			proxyDatas.add(ProxyData.parse("203.134.66.85:3128"));
			proxyDatas.add(ProxyData.parse("203.134.66.85:8080"));
			proxyDatas.add(ProxyData.parse("203.189.137.96:8080"));
			proxyDatas.add(ProxyData.parse("211.194.214.128:9050:8080"));
			proxyDatas.add(ProxyData.parse("212.112.127.20:80"));
			proxyDatas.add(ProxyData.parse("212.112.127.20:8080"));
			proxyDatas.add(ProxyData.parse("217.12.203.117:11221:8080"));
			proxyDatas.add(ProxyData.parse("31.31.76.178:5566:8080"));
			proxyDatas.add(ProxyData.parse("36.94.122.18:80"));
			proxyDatas.add(ProxyData.parse("37.18.73.94:5566:8080"));
			proxyDatas.add(ProxyData.parse("37.59.50.81:9050:8080"));
			proxyDatas.add(ProxyData.parse("37.59.98.31:9050:8080"));
			proxyDatas.add(ProxyData.parse("45.113.80.37:9050:8080"));
			proxyDatas.add(ProxyData.parse("45.9.14.11:80"));
			proxyDatas.add(ProxyData.parse("4.59.83.198:80"));
			proxyDatas.add(ProxyData.parse("46.249.122.1:80"));
			proxyDatas.add(ProxyData.parse("46.249.122.1:8080"));
			proxyDatas.add(ProxyData.parse("47.243.121.74:3128"));
			proxyDatas.add(ProxyData.parse("51.195.137.139:8080"));
			proxyDatas.add(ProxyData.parse("51.222.12.245:80"));
			proxyDatas.add(ProxyData.parse("51.68.176.6:443:8080"));
			proxyDatas.add(ProxyData.parse("51.83.190.248:19050:8080"));
			proxyDatas.add(ProxyData.parse("51.89.68.78:9050:8080"));
			proxyDatas.add(ProxyData.parse("5.252.177.254:12020:8080"));
			proxyDatas.add(ProxyData.parse("54.36.183.52:9173:8080"));
			proxyDatas.add(ProxyData.parse("54.82.13.32:9050:8080"));
			proxyDatas.add(ProxyData.parse("70.90.138.109:80"));
			proxyDatas.add(ProxyData.parse("70.90.138.109:8080"));
			proxyDatas.add(ProxyData.parse("77.95.229.224:9051:8080"));
			proxyDatas.add(ProxyData.parse("78.46.225.37:19051:8080"));
			proxyDatas.add(ProxyData.parse("78.47.240.61:9050:8080"));
			proxyDatas.add(ProxyData.parse("80.87.200.140:9050:8080"));
			proxyDatas.add(ProxyData.parse("81.91.136.76:8080"));
			proxyDatas.add(ProxyData.parse("82.165.137.115:7061:8080"));
			proxyDatas.add(ProxyData.parse("88.99.191.147:9050:8080"));
			proxyDatas.add(ProxyData.parse("89.111.133.217:9151:8080"));
			proxyDatas.add(ProxyData.parse("91.121.48.221:80"));
			proxyDatas.add(ProxyData.parse("93.95.227.154:9050:8080"));
			proxyDatas.add(ProxyData.parse("94.130.182.121:8080"));
			proxyDatas.add(ProxyData.parse("94.75.76.10:8080"));
			proxyDatas.add(ProxyData.parse("95.213.154.54:31337:8080"));
			proxyDatas.add(ProxyData.parse("95.216.181.107:9070:8080"));
			proxyDatas.add(ProxyData.parse("95.216.181.107:9080:8080"));
			proxyDatas.add(ProxyData.parse("95.216.181.107:9090:8080"));

		} catch (final ApiException e) {
			e.printStackTrace();
		}
		proxyDatas.stream().forEachOrdered(proxy -> {
			System.err.println("in " + proxy);
			mainOutSideCheck(proxy);
			if (proxy.socketConnection.booleanValue()) {
				System.err.println("out " + proxy);
			}
			System.err.println();
		});

	}

	private static void mainOutHttp(final ProxyData proxydata) throws CloudflareDetectedException,
			AuthProxyNeededException, TorDetectedException, MikrotikHttpProxyDetectedException {
		try {
			mainOutHttpX(proxydata, ProxyTypes.HTTP);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void mainOutHttpS(final ProxyData proxydata) throws CloudflareDetectedException,
			AuthProxyNeededException, TorDetectedException, MikrotikHttpProxyDetectedException {
		try {
			mainOutHttpX(proxydata, ProxyTypes.HTTPS);
		} catch (final SSLException e) {
			// proxydata.https
			// ignore
		}

		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void mainOutHttpX(final ProxyData proxydata, final ProxyTypes schema)
			throws IOException, CloudflareDetectedException, AuthProxyNeededException, TorDetectedException,
			MikrotikHttpProxyDetectedException {
		if (schema != ProxyTypes.HTTP && schema != ProxyTypes.HTTPS) {
			throw new IllegalArgumentException("illegal schema: " + schema.name());
		}

		// OLD
		final var http = new StringBuilder();

		final var requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).build();
		final var connectionConfigBuilder = ConnectionConfig.custom();
		connectionConfigBuilder.setConnectTimeout(timeout);
		final var connectionConfig = connectionConfigBuilder.build();
		final var proxyHost = new HttpHost(schema.name().toLowerCase(), proxydata.host, proxydata.port.intValue());
		final var routePlanner = new DefaultProxyRoutePlanner(proxyHost);
		final var connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
		connectionManagerBuilder.setDefaultConnectionConfig(connectionConfig);

		try (var connectionManager = connectionManagerBuilder.build();
				var httpclient = HttpClients.custom().setRoutePlanner(routePlanner)
						.setConnectionManager(connectionManager).build()) {
			final var request = new HttpGet("/");
			request.setConfig(requestConfig);

			final var context = new BasicHttpContext();

			try (var response = httpclient.executeOpen(target, request, context)) {
				checkIfCloudflare(response, proxydata);
				checkIfAuthNeeded(response, proxydata);
				checkIfForbidden(response, proxydata);

				try (var entity = response.getEntity();
						var contentStream = entity.getContent();
						var reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8))) {
					reader.lines().forEachOrdered(line -> {
						http.append(line).append('\n');
					});

				} // response
				if (http.charAt(http.length() - 1) == '\n') {
					http.deleteCharAt(http.length() - 1);
				}

				if (IPV4_PATTERN.matcher(http.toString()).matches()
						|| IPV6_PATTERN.matcher(http.toString()).matches()) {

				}

				if (http.length() > 100) {
					checkIfTor(proxydata, http);
					if (http.toString().contains("Mikrotik HttpProxy")) {
						throw new MikrotikHttpProxyDetectedException(proxydata.host);
					}

					if (LogMode.forLogGlobalList(LogMode.CRIT)) {
						LOGGER.warn("unwanted data:{}", http);
					}
				}

			}
		} catch (final UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// OLD

	}

	public static ProxyData mainOutSideCheck(final ProxyData proxydata) {// FIXME
		try {
			proxydata.lastChecked = System.currentTimeMillis() / 1000;
			proxydata.type = ProxyType.CHECKED;
			try (var socket = new Socket()) {
				socket.connect(new InetSocketAddress(proxydata.host, proxydata.port), 5000);
				proxydata.socketConnection = true;
			} catch (final IOException e) {
				throw new SocketForProxyNotConnectedException(e);
			}
			mainOutHttpS(proxydata);
			mainOutHttp(proxydata);
			mainOutSocks(proxydata);

			// https
			// http
			// socks
		} catch (final SocketForProxyNotConnectedException e) {
			proxydata.socketConnection = false;
			proxydata.cloudflare = false;
			proxydata.mikrotikhttpproxy = false;
			proxydata.socks = false;
			proxydata.tor = false;
			proxydata.ASNExit = null;
			proxydata.hostExit = null;
			proxydata.countryExit = null;
			proxydata.http = false;
			proxydata.httpCode = 0;
			proxydata.httpReason = null;
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return proxydata;
	}

	/**
	 * @param proxydata
	 */
	private static void mainOutSocks(final ProxyData proxydata) {
		// TODO Auto-generated method stub

	}

	private static void postTest(final ProxyData proxydata) {
		// in
		try {
			ProxyDataUtils.recognIn(proxydata);
		} catch (AntiPidorException | IOException e) {
			e.printStackTrace();
		}
		// out
	}

	private static void preTest(final ProxyData proxydata) {
		proxydata.socketConnection = Boolean.valueOf(false);
		proxydata.http = Boolean.valueOf(false);
		proxydata.socks = Boolean.valueOf(false);

		proxydata.hostExit = null;
		proxydata.countryExit = null;
		proxydata.ASNExit = null;
		proxydata.type = ProxyType.CHECKED;
	}

	public static Boolean socketCanConnect(final ProxyData proxydata) {
		try (var socket = new Socket()) {
			socket.connect(new InetSocketAddress(proxydata.host, proxydata.port.intValue()),
					Long.valueOf(TimeUnit.SECONDS.toMillis(10)).intValue());
			final var status = Boolean.valueOf(socket.isConnected());
			proxydata.socketConnection = status;
			return status;
		} catch (final Exception e) {
			proxydata.socketConnection = Boolean.valueOf(false);
			return proxydata.socketConnection;
		}
	}

	public static boolean socks4ToConnect(final ProxyData proxydata, final StringBuilder socks4Builder) {
		try {
			final var proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxydata.host, proxydata.port.intValue()));

			final var url = URI.create("http://ident.me/").toURL();
			final var connection = (HttpURLConnection) url.openConnection(proxy);
			connection.setConnectTimeout(Long.valueOf(timeout.toMilliseconds()).intValue());
			connection.setReadTimeout(Long.valueOf(timeout.toMilliseconds()).intValue());
			try (var is = connection.getInputStream()) {
				try (var bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
					bufferedReader.lines().forEach(line -> {
						if (line.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
							socks4Builder.append(line);
						} else {
							socks4Builder.append(line).append('\n');
						}
					});
				}
			}
			ProxyDataSocks.socksOk(proxydata, connection.getResponseCode(), connection.getResponseMessage(),
					socks4Builder);
			return true;
		} catch (final java.net.SocketException | SocketTimeoutException e) {
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;

	}

}
