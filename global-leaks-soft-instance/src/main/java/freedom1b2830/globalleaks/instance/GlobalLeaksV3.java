package freedom1b2830.globalleaks.instance;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.telegram.telegrambots.meta.api.objects.Message;

import com.github.publiclibs.langpack.Langpack;
import com.github.publiclibs.langpack.provider.file.DefaultLangProvider;
import com.github.publiclibs.langpack.provider.file.JarResourcesLangProvider;

import freedom1b2830.globalleaks.instance.utils.ConfigUtils;
import freedom1b2830.globalleaks.module.telegrambot.BotTelegram;
import freedom1b2830.globalleaks.module.telegrambot.TelegramModule;
import freedom1b2830.globalleaks.module.telegrambot.cmd.TelegramCMD;
import wcas.freedom1b2830.globalleaks.LogMode;
import wcas.freedom1b2830.globalleaks.lang.GLKeys;
import wcas.freedom1b2830.globalleaks.module.EmailGetterModule;
import wcas.freedom1b2830.globalleaks.module.impl.db.GlobalLeaksDataBase;
import wcas.freedom1b2830.globalleaks.proxy.v2.data.proxy.ProxyData;
import wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.checker.ProxyCheckerModule;
import wcas.freedom1b2830.globalleaks.proxy.v2.module.impl.getter.web.ProxyFWWEbGetter;
import wcas.freedom1b2830.globalleaks.watchdog.NetworkWatchdog;

public class GlobalLeaksV3 {

	public static final File configFile = new File(String.format("%s-config.yaml", GlobalLeaksV3.class.getName()));

	public static void main(final String[] args) throws Exception {
		Langpack.getInstance().registerPrivider(new DefaultLangProvider());
		Langpack.getInstance().registerPrivider(new JarResourcesLangProvider());
		final var v3 = new GlobalLeaksV3();
		v3.init();
	}

	private ProxyFWWEbGetter proxyFWWEbGetter;

	private ProxyCheckerModule proxyFWChecker;

	private GlobalLeaksDataBase globalLeaksDataBase;

	private EmailGetterModule emailGetterModule;

	private NetworkWatchdog networkWatchdog;

	private TelegramModule telegramModule;

	private void init() throws Exception {
		final var config = ConfigUtils.readConfig(null, configFile);
		LogMode.global = LogMode.DEBUG;

		// ##################################################### network watchdog
		networkWatchdog = new NetworkWatchdog(config);
		// ##################################################### network watchdog

		// ##################################################### DB
		globalLeaksDataBase = new GlobalLeaksDataBase(config) {

			public @Override boolean canProcess() {
				return networkWatchdog.isNetworkOk();
			}

			public @Override void nocheckedInDB(final ProxyData proxyData) {
				proxyFWChecker.append(proxyData);
			}

		};

		// ##################################################### email
		emailGetterModule = new EmailGetterModule(config) {
			public @Override boolean canProcess() {
				return networkWatchdog.isNetworkOk();
			}
		};

		// ##################################################### proxy
		// ##################################################### proxy-checker
		proxyFWChecker = new ProxyCheckerModule(config) {
			public @Override boolean canProcess() {
				return networkWatchdog.isNetworkOk();
			}

			protected @Override void checkedProxy(final ProxyData data) {
				try {
					globalLeaksDataBase.saveProxy(data);
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		};
		proxyFWWEbGetter = new ProxyFWWEbGetter(config) {// FIXME
			public @Override boolean canProcess() {
				return networkWatchdog.isNetworkOk();
			}

			public @Override void rawProxy(final ProxyData proxyData) {
				try {
					proxyData.beforDB();
					globalLeaksDataBase.saveProxy(proxyData);
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}

			public @Override void stop() throws Exception {
			}

		};
		// #####################################################
		networkWatchdog.init();
		globalLeaksDataBase.init();

		proxyFWChecker.init();

		proxyFWWEbGetter.init();
		// emailGetterModule.init();

		// telegram
		telegramModule = new TelegramModule(config) {

			@Override
			protected Integer moneyCostCurrentGet(final TelegramCMD type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void sendHTTP(final BotTelegram telegram, final Message message, final Integer time,
					final Long userId, final String userLogin, final String userFN, final String userLN,
					final String userLang) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void sendSOCKS(final BotTelegram telegram, final Message message, final Integer time,
					final Long userId, final String userLogin, final String userFN, final String userLN,
					final String userLang) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void sendStatus(final BotTelegram telegram, final Message message, final Integer time,
					final Long userId, final String userLogin, final String userFN, final String userLN,
					final String userLang) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void userBalance(final BotTelegram telegram, final Message msg, final Integer time,
					final Long userId, final String user_login, final String userFN, final String userLN,
					final String userLang) {
				// TODO Auto-generated method stub

			}

			@Override
			protected Boolean userGetHttpEnabled() throws NoSuchAlgorithmException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected Integer userGetOldCostBuy(final Long userId, final TelegramCMD type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void userStart(final BotTelegram telegram, final Message msg, final Integer time,
					final Long userId, final String user_login, final String userFN, final String userLN,
					final String userLang) {

				try {
					telegramModule.sendFallBack(telegram, userId, userLang, GLKeys.USER_START, GLKeys.INTERNAL_ERROR);
					if (globalLeaksDataBase.customersDao.idExists(userId)) {
						telegramModule.sendFallBack(telegram, userId, userLang, GLKeys.USER_START,
								GLKeys.INTERNAL_ERROR);

					}
				} catch (final SQLException e) {
					e.printStackTrace();
				}
				// globalLeaksDataBase.createCustomers(null)

			}
		};
		telegramModule.init();
	}

}
