package freedom1b2830.globalleaks.module.telegrambot;

import java.security.NoSuchAlgorithmException;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.publiclibs.langpack.Langpack;

import freedom1b2830.globalleaks.module.telegrambot.cmd.TelegramCMD;
import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;
import wcas.freedom1b2830.globalleaks.module.GlobalLeakModule;

public abstract class TelegramModule extends GlobalLeakModule {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

	BotTelegram bot = new BotTelegram(config.telegramBotConfig.token) {

		public @Override String getBotUsername() {
			return config.telegramBotConfig.botUsername;
		}

		private void inputCMD(final BotTelegram telegram, final Message message, final Integer time, final Long userId,
				final String user_login, final String userFN, final String userLN, final String userLang,
				final TelegramCMD cmd) {

			switch (cmd) {
			case START -> userStart(telegram, message, time, userId, user_login, userFN, userLN, userLang);
			case BALANCE -> userBalance(telegram, message, time, userId, user_login, userFN, userLN, userLang);
			case PAYMENT -> sendPAYMENT(telegram, message, time, userId, user_login, userFN, userLN, userLang);
			case STATUS -> sendStatus(telegram, message, time, userId, user_login, userFN, userLN, userLang);
			case HTTP, SOCKS -> {
				final var costNow = moneyCostCurrentGet(cmd);
				final var costOld = userGetOldCostBuy(userId, cmd);
				switch (cmd) {
				case HTTP -> {
					// final Boolean http = userGetEnabled();
					// if (http.booleanValue()) {
					// }
					sendHTTP(telegram, message, time, userId, user_login, userFN, userLN, userLang);
				}
				case SOCKS -> {
					sendSOCKS(telegram, message, time, userId, user_login, userFN, userLN, userLang);
				}
				case BALANCE -> throw new IllegalArgumentException("Unexpected value: " + cmd);
				case PAYMENT -> throw new IllegalArgumentException("Unexpected value: " + cmd);
				case START -> throw new IllegalArgumentException("Unexpected value: " + cmd);
				case STATUS -> throw new IllegalArgumentException("Unexpected value: " + cmd);

				default -> throw new IllegalArgumentException("Unexpected value: " + cmd);
				}
				// если баланс, вычитаем,включаем http
				System.out.println();

			}

			default -> throw new IllegalArgumentException("Unexpected value: " + cmd);
			}
		}

		private void inputCMDRaw(final BotTelegram telegram, final Message message, final Integer time,
				final Long userId, final String user_login, final String userFN, final String userLN,
				final String userLang, final String cmdString) {

			try {

				final var cmd = TelegramCMD.valueOf(cmdString.toUpperCase());
				inputCMD(telegram, message, time, userId, user_login, userFN, userLN, userLang, cmd);
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		private void inputText(//
				final BotTelegram telegram, //
				final Message message, //
				final Integer time, //
				final Long userId, //
				final String user_login, //
				final String userFN, //
				final String userLN, //
				final String userLang, //
				final String text) {

			if (text.startsWith("/")) {
				inputCMDRaw(telegram, message, time, userId, user_login, userFN, userLN, userLang, text.split("/")[1]);
			} else {
				// FIXME no CMD
				System.err.println(text);
			}
		}

		public @Override void onUpdateReceived(final Update update) {

			// sender

			if (update.hasMessage()) {
				final var message = update.getMessage();
				final var user = message.getFrom();

				final var time = message.getDate();

				final Long userId = user.getId();
				final var user_login = user.getUserName();
				final String userFN = user.getFirstName();
				final var userLN = user.getLastName();
				final var userLang = user.getLanguageCode();

				if (message.hasText()) {
					final var text = message.getText();
					inputText(this, message, time, userId, user_login, userFN, userLN, userLang, text);
				} else {
					try {
						System.err.println(OBJECT_MAPPER.writeValueAsString(message));
						// FIXME only text
					} catch (final JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				return;
			}
			System.err.println(update.getClass());
		}

	};

	protected TelegramModule(final GlobalLeakConfig config) {
		super(config, "TELEGRAM-module");
		worker = new TelegramWorker(this, loggerName);
	}

	public @Override void init() throws Exception {
		worker.start();
		final var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(bot);
	}

	protected abstract Integer moneyCostCurrentGet(TelegramCMD type);

	public void sendFallBack(//
			final BotTelegram telegram, //
			final Long userId, //
			final String userLang, //
			final String key, //
			final String fallback) {
		try {
			final var sendMessage = new SendMessage();
			sendMessage.setChatId(userId);

			String msg;
			try {
				msg = Langpack.getData(key, userLang);
			} catch (final Exception e) {// aka runtime
				e.printStackTrace();
				System.err.println(key + " " + userLang);
				msg = fallback;
			}
			sendMessage.setText(msg);
			telegram.execute(sendMessage);
		} catch (final TelegramApiException e) {
			e.printStackTrace();
		}
	}

	protected abstract void sendHTTP(BotTelegram telegram, Message message, Integer time, Long userId, String userLogin,
			String userFN, String userLN, String userLang);

	protected void sendPAYMENT(final BotTelegram telegram, final Message message, final Integer time, final Long userId,
			final String userLogin, final String userFN, final String userLN, final String userLang) {

	};

	protected abstract void sendSOCKS(BotTelegram telegram, Message message, Integer time, Long userId,
			String userLogin, String userFN, String userLN, String userLang);

	protected abstract void sendStatus(BotTelegram telegram, Message message, Integer time, Long userId,
			String userLogin, String userFN, String userLN, String userLang);

	public @Override void stop() throws Exception {
		stopInternal();
	}

	public @Override void stopInternal() throws Exception {
		worker.close();
	}

	protected abstract void userBalance(BotTelegram telegram, Message msg, Integer time, Long userId, String user_login,
			String userFN, String userLN, String userLang);

	protected abstract Boolean userGetHttpEnabled() throws NoSuchAlgorithmException;

	protected abstract Integer userGetOldCostBuy(Long userId, TelegramCMD type);

	protected abstract void userStart(BotTelegram telegram, Message msg, Integer time, Long userId, String user_login,
			String userFN, String userLN, String userLang);

}
