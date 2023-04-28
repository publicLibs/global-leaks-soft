package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.getter.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.BasicHttpContext;

import wcas.freedom1b2830.globalleaks.AtomicCopyOnWriteArrayList;
import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorkerAction;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.ApiException;
import wcas.freedom1b2830.globalleaks.utils.ThreadUtils;

public abstract class ProxyFWWEbGetterWorker extends GlobalLeakModuleWorker {

	private final CloseableHttpClient httpClient;

	protected ProxyFWWEbGetterWorker(final GlobalLeakModule proxyFWModule, final String loggerName) {
		super(proxyFWModule, loggerName);
		httpClient = HttpClients.createDefault();
		actionThreadLoop = new GlobalLeakModuleWorkerAction() {
			public @Override void exec() throws IOException {
				for (final String source : proxyFWModule.config.proxyGCM.webGetterConfig.sources) {
					setName(source);
					while (!proxyFWModule.canProcess()) {
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							log().info("cant process");
						}
						ThreadUtils.sleep(1, TimeUnit.SECONDS);
					}

					try {
						final var get = new HttpGet(source);
						String data;
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							log().info("connection");
						}

						// FIXME
						System.err.println(get.getAuthority());
						final var context = new BasicHttpContext();

						final var target = new HttpHost(source);// TODO GET HOST

						try (var classicHttpResponse = httpClient.executeOpen(target, get, context)) {
							if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
								log().info("connected");
							}

							try (var httpEntity = classicHttpResponse.getEntity()) {
								if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
									log().info("get data");
								}
								data = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
								if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
									log().info("geted data");
								}
							}
						} // closed connection
						if (LogMode.forLogGlobalList(LogMode.DEBUG)) {
							log().info("parse data");
						}
						final var dataArray = data.split("\n");
						if (dataArray.length == 0) {
							log().error("source is empty");
							continue;
						}
						final var proxyDatas = new AtomicCopyOnWriteArrayList<ProxyData>();
						final List<String> dataList = Arrays.asList(dataArray);
						dataList.parallelStream().forEachOrdered(line -> {
							ProxyData proxyData;
							try {
								proxyData = ProxyData.parse(line);
								proxyDatas.addIfAbsent(proxyData);
							} catch (final ApiException e) {
								log().error("api error [{}]", e.getMessage());
							}
						});
						while (!proxyDatas.isEmpty()) {
							final var proxyData = proxyDatas.next();
							rawProxy(proxyData);
						}
					} catch (final Exception e) {
						log().error("error", e);
					}

				} // end source
				if (LogMode.forLogGlobalList(LogMode.INFO)) {
					log().info("sources getted");
				}
				ThreadUtils.sleep(10, TimeUnit.MINUTES);
			}
		};
	}

	public abstract void rawProxy(ProxyData proxyData);

}
