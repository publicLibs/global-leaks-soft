package freedom1b2830.globalleaks.module.telegrambot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * api самого телеграма
 *
 * @author user_dev
 */
public abstract class BotTelegram extends TelegramLongPollingBot {

	/**
	 * @param options
	 * @param botToken
	 */
	public BotTelegram(final DefaultBotOptions options, final String botToken) {
		super(options, botToken);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param botToken
	 */
	public BotTelegram(final String botToken) {
		super(botToken);
		// TODO Auto-generated constructor stub
	}

	public void fBalanceLow(final Update event) throws TelegramApiException {
		final var MESSAGE = "low balance";
		final var sendMessageCmd = new SendMessage();
		sendMessageCmd.setChatId(event.getMessage().getChatId());
		sendMessageCmd.setText(MESSAGE);
		execute(sendMessageCmd);
	}

	public void fUnknownCMd(final Update event) throws TelegramApiException {
		final var FORMAT = "%s send unknown command";
		final var sendMessageCmd = new SendMessage();
		sendMessageCmd.setChatId(event.getMessage().getChatId());
		final String who = event.getMessage().getFrom().getFirstName();
		final var text = String.format(FORMAT, who);
		sendMessageCmd.setText(text);
		execute(sendMessageCmd);
	}
}
