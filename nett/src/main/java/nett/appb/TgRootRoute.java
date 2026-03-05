package nett.appb;

import botcore.clb.EncodeDecodeCacheClb;
import lombok.SneakyThrows;
import mpc.rfl.RFL;
import mp.utl_odb.netapp.INetApp;
import mpu.X;
import nett.ats.ATS;
import nett.TgPollingBot;
import nett.Tgc;
import botcore.BotRoute;
import botcore.IBotUpdate;
import botcore.RootRoute;
import nett.ats.SendMessage0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public abstract class TgRootRoute extends RootRoute {

	public static final Logger L = LoggerFactory.getLogger(TgRootRoute.class);

	public abstract TgApp getTgApp();

	public abstract INetApp getNetApp();

	public TgPollingBot getTgBot() {
		return ATS.getTgBot(getBotId());
	}

	public TgRootRoute(String botId, List<Class<BotRoute>> routes) {
		super(botId, routes);
	}

	@Override
	protected BotRoute findRouteByCallback(IBotUpdate update) {
		String data = update.getCallbackData();
		boolean hasPdxEDTG = data.startsWith(EncodeDecodeCacheClb.PFX_ED_TG);
		for (Collection<BotRoute> allRoutes : getMapRoutes().values()) {
			for (BotRoute botRoute : allRoutes) {
				if (hasPdxEDTG && !update.isDecode()) {
					pathDecodeUpdate(update, false);
				}
				if (botRoute.isCallbackRoute(update, false)) {
					return botRoute;
				}
			}
		}
		return null;
	}

	protected void pathDecodeUpdate(IBotUpdate update, boolean silent) {
		EncodeDecodeClb.pathDecodeUpdate(update, silent);
	}

	protected void pathUpdateMessage(BotRoute aliasRoute, BotRoute targetRoute, IBotUpdate botUpdate) {
		Update update = ((TgUpdate) botUpdate).update;
		Message message = update.getMessage();
		if (message == null) {
			return;
		}
		String orgMsg = message.getText();
		String newMsg = getNewMessageFromAlias(aliasRoute, targetRoute, orgMsg);
		RFL.fieldValueSet(message, "text", newMsg, true);
	}

	//
	//
	public Message sendMessageSimple(String chatId, String msg, Object... args) {
		return sendMessage(new SendMessage0(chatId, X.f(msg, args), null));
	}

	@SneakyThrows
	public Message sendMessageVideo(String chatId, String cap, String file) {
		return ATS.doSendVideo(getTgBot(), chatId, cap, file);
	}

	@SneakyThrows
	public Message sendMessageAudio(String chatId, String cap, String file) {
		return ATS.doSendAudio(getTgBot(), chatId, cap, file);
	}

	@SneakyThrows
	public Message sendMessageAudio(SendAudio sendAudio) {
		return ATS.doSendAudio(getTgBot(), sendAudio);
	}

	@SneakyThrows
	public Serializable sendMessage(PartialBotApiMethod method) {
		return ATS.doSend(getTgBot(), method);
	}

	@SneakyThrows
	public Message sendMessageDocument(String chatId, String cap, String file) {
		return ATS.doSendDocument(getTgBot(), chatId, cap, file);
	}

	public Message sendMessage(SendMessage msg) {
		return ATS.sendMessage(getBotId(), null, msg, false, false, this);
	}

	public Message sendMessage(Update update, String msg) {
		SendMessage sm = Tgc.newSendMessage(Tgc.getChatIDAny(update), msg, null);
		return sendMessage(sm);
	}


}
