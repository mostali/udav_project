package nett.appb;


import mp.utl_odb.netapp.mdl.NetActivityModel;
import mpc.*;
import mpc.arr.Arr;
import nett.Tgc;
import nett.keyboard.TgInlineKeyboardButton;
import nett.msg.TgMsg;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AppTgRoute extends TgRoute {

	public AppTgRoute() {
		super();
	}

	public AppTgRoute(TgRoute route) {
		super(route);
	}

	@Override
	protected void beforeUpdate() {
		super.beforeUpdate();

		if (getTgApp().isActivityAllow()) {
			String msg = getMessageOrCallBackData();
			String clb = getIsCallbackOrMessage() ? msg : null;
			msg = getIsCallbackOrMessage() ? null : msg;
			NetActivityModel.createNewActivity(getTgApp().getActivityDb(), getUsrId().getUserUid(), msg, clb, cn());
		}
	}

	public Object sendMessage(String message, TgInlineKeyboardButton... keys) {
		if (X.empty(keys)) {
			return sendMsg(message);
		}
		List<List<TgInlineKeyboardButton>> lines = new ArrayList();
		for (TgInlineKeyboardButton key : keys) {
			lines.add(Arrays.asList(key));
		}
		return sendMessage(message, lines);
	}

	public Object sendMessage(String message, List<TgInlineKeyboardButton> keys, List<TgInlineKeyboardButton>... otherKeys) {
		return sendMessage(message, ParseMode.HTML, keys, otherKeys);
	}

	public Object sendMessage(String message, String html, List<TgInlineKeyboardButton> keys, List<TgInlineKeyboardButton>... otherKeys) {
		if (X.empty(keys)) {
			return sendMessage(message, html);
		}
		List<List<TgInlineKeyboardButton>> lines = new ArrayList();
		lines.add(keys);
		for (List l : otherKeys) {
			lines.add(l);
		}
		return sendMessage(message, lines);
	}

	public Message sendMessage(String message, List... keys2) {
		if (X.empty(keys2)) {
			return sendMsg(message);
		}
		List<List<TgInlineKeyboardButton>> keys = Arr.as(keys2);
		return sendMessage(message, ParseMode.HTML, keys);
	}

	public Message sendMessage(TgMsg message, List<List<TgInlineKeyboardButton>> lines) {
		return sendMessage(message.getText(), message.getParseMode(), lines);
	}

	public Message sendMessageSimple(String message) {
		SendMessage msg = Tgc.newSendMessageSimple(Tgc.getChatIDAny(getUpdate().update), message);
		return sendMsg(msg);
	}

	public Message sendMessage(String message, String isHtml, List<List<TgInlineKeyboardButton>> lines) {
		SendMessage sm = Tgc.newSendMessage(getChatIdAny(), message, isHtml, true, true, lines);
		return sendMsg(sm);
	}

	public Serializable sendEditMessage(int message_id, String message) {
		return sendEditMessage(message_id, message, ParseMode.HTML, null);
	}

	public Serializable sendEditMessage(Integer msg_id, String message, String isHtml, List<List<TgInlineKeyboardButton>> lines) {
		return sendEditMessage(msg_id, message, isHtml, true, lines);
	}

	public Message sendMessageFmt(String message, Object... args) {
		return sendMsg(X.f(message, args));
	}

	public Message sendMessagePreview(String message) {
		return sendMessage(message, ParseMode.HTML, true, true);
	}

	public Message sendMessage(String message, String isHtml) {
		return sendMessage(message, isHtml, false, true);
	}

	public Message sendMessage(String message, TgCallback clb) {
		return sendMessage(message, Arr.toListList(clb.toKey()));
	}

	public Message sendMessage(String message, String isHtml, boolean enablePreview, boolean enableNotification) {
		return sendMsg(message, isHtml, enablePreview, enableNotification, null);
	}

}
