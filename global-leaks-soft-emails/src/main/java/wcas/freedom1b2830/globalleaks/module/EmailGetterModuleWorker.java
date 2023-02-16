package wcas.freedom1b2830.globalleaks.module;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import wcas.freedom1b2830.globalleaks.config.email.EmailGetterModuleConfig;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorker;
import wcas.freedom1b2830.globalleaks.module.worker.GlobalLeakModuleWorkerAction;

public abstract class EmailGetterModuleWorker extends GlobalLeakModuleWorker
		implements EmailGetterModuleWorkerListener {

	protected EmailGetterModuleWorker(GlobalLeakModule module, String loggerName) {
		super(module, loggerName);
		actionAfterThread = new GlobalLeakModuleWorkerAction() {

			@Override
			public void exec() throws IOException {
			}
		};
		actionThreadLoop = new GlobalLeakModuleWorkerAction() {

			@Override
			public void exec() throws IOException {
				try (PoolingHttpClientConnectionManager a1 = PoolingHttpClientConnectionManagerBuilder.create()
						.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
								.setSslContext(
										SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
								.setHostnameVerifier(NoopHostnameVerifier.INSTANCE).build())
						.build();
						CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(a1).build()) {
					EmailGetterModuleConfig config = module.config.emailGetterModuleConfig;

					for (String url : config.sources) {
						log().info("get from {}", url);

						HttpGet httpGet = new HttpGet(url);

						try (CloseableHttpResponse result = httpclient.execute(httpGet);
								HttpEntity ent = result.getEntity()) {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(ent.getContent(), UTF_8));

							String line;
							while ((line = bufferedReader.readLine()) != null) {
								rawInputWebPage(line);
							}
						} catch (Exception e) {
							sourceException(e);
						}

					}
				} catch (Exception e1) {
					httpClientException(e1);
				}
			}// method
		};

	}

}
