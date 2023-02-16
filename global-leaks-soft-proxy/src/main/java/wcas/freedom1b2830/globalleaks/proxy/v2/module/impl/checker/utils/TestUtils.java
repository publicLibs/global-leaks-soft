package wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hc.core5.http.HttpHost;

import com.fasterxml.jackson.core.JsonProcessingException;

import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.exceptions.ApiException;

public class TestUtils {
	static final HttpHost target = new HttpHost("http", "ident.me", 80);

	public static void main(final String[] args) throws ApiException {
		final String[] ips = new String[] { "172.64.141.2:80", "185.15.172.212:3128", "92.101.95.210:1080",
				"51.250.80.131:80", "83.69.236.12:2019", "68.225.233.145:8111" };

		try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
			final CopyOnWriteArrayList<Future<ProxyData>> futures = new CopyOnWriteArrayList<>();

			for (final String string : ips) {
				final var proxy = ProxyData.parse(string);
				final Callable<ProxyData> taskCallable = () -> {
					Thread.currentThread().setName(proxy.host + ":" + proxy.port);
					ProxyConnectUtils.fulltest(proxy);
					return proxy;
				};
				final Future<ProxyData> futureC = executorService.submit(taskCallable);
				futures.add(futureC);
			}
			for (final Future<ProxyData> future : futures) {
				try {
					final ProxyData data = future.get();
					System.out.println(data.toJsonString());
				} catch (InterruptedException | ExecutionException | JsonProcessingException e) {
					e.printStackTrace();
				}

			}
			executorService.shutdown();
		}

	}// main

}// class
