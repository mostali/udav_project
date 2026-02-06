package nett;


import mpu.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;
import mpc.arr.QUEUE;

import java.util.function.Consumer;

public class TgPollingBot extends TelegramLongPollingBot {
	public static final Logger L = LoggerFactory.getLogger(TgPollingBot.class);

	private BotSession session;

	public void setBotSession(BotSession session) {
		this.session = session;
	}

	public BotSession getBotSession() {
		return session;
	}

	private final String botUsername, botToken;

	public TgPollingBot(String botUsername, String botToken, DefaultBotOptions botOptions) {
		super(botOptions);
		this.botUsername = botUsername;
		this.botToken = botToken;
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	public void stop() {
		if (session == null) {
			L.warn("stop::Session is null");
		} else if (!session.isRunning()) {
			L.warn("stop::Session is not running");
		}
		session.stop();
	}

	private QUEUE.QueueSafe<Update> queue_updates;
	public static final int MAX_CACHE_UPDATE = 100;

	public QUEUE.QueueSafe<Update> getQueueUpdates() {
		if (queue_updates == null) {
			queue_updates = (QUEUE.QueueSafe<Update>) QUEUE.cache_queuesafe_sync_FILO(MAX_CACHE_UPDATE);
		}
		return queue_updates;
	}

	public final QUEUE.QueueSafe<Update> queue_updates_1000 = (QUEUE.QueueSafe<Update>) QUEUE.cache_queuesafe_sync_FILO(1000);

	@Override
	public void onUpdateReceived(Update update) {

		if (true) {
			queue_updates_1000.add(update, new Consumer<Update>() {
				@Override
				public void accept(Update update) {
					Sys.p("WARNING :: thi update is lost ::: " + update);
				}
			});
			if (L.isInfoEnabled()) {
				L.info("Update wait handle  ::: " + update);
			}
			return;
		}

		if (L.isInfoEnabled()) {
			L.info(getBotUsername() + " ::: HAS NEW UPDATE ::: " + update);
		}

		getQueueUpdates().add(update, new Consumer<Update>() {
			@Override
			public void accept(Update update) {
				//				U.p("Warning, max size was happens, because write last element in cache ::: " + getTreeForUnhandledUpdate());
				if (L.isInfoEnabled()) {
					L.info("Warning, max size was happens, because write last element in cache ::: ");
				}

			}
		});

	}

}
