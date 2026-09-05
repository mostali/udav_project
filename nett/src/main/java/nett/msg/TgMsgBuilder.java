package nett.msg;

import botcore.msg.BotMsgBuilder;
import botcore.clb.IBotButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpu.X;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpz_deprecated.app_event.AppEvent;
import mpz_deprecated.app_event.EventState;
import mpz_deprecated.app_event.IEvent;
import nett.Tgc;
import nett.Tgh;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public abstract class TgMsgBuilder extends BotMsgBuilder<TgMsg> {

	protected abstract TgMsgBuilder buildMessages();

	public static class EventTgMessageBuilder extends TgMsgBuilder implements IEvent {

		@Override
		protected TgMsgBuilder buildMessages() {
			for (AppEvent.Type appEventType : AppEvent.Type.values()) {
				if (!getEventState().has(appEventType)) {
					continue;
				}
				Collection<AppEvent> events = getEventState().get(appEventType);
				addMessage(Tgh.b(appEventType));
				for (AppEvent ev : events) {
					addMessage(Tgh.i(ev.first()));
				}
			}
			return this;
		}


		@Getter
		final EventState eventState = new EventState();

		@Override
		public void EVENT(AppEvent event) {
			eventState.getEvents().put(event.getType(), event);
		}
	}

	public static class DefMessageBuilder extends TgMsgBuilder {
		@Override
		protected TgMsgBuilder buildMessages() {
			return this;
		}
	}

	public BotMsgBuilder addMessage(String... msg) {
		for (String m : msg) {
			getMessages().add(TgMsg.of(m));
		}
		return this;
	}

	public TgMessages getMessages() {
		return (TgMessages) (messages != null ? messages : (messages = new TgMessages()));
	}

	public static BotMsgBuilder of(String... messages) {
		TgMsgBuilder msgBuilder = new TgMsgBuilder() {
			@Override
			protected TgMsgBuilder buildMessages() {
				return this;
			}
		};
		for (String msg : messages) {
			msgBuilder.addMessage(msg);
		}
		return msgBuilder;
	}

	public TgMsg getMsgOrCreate() {
		if (super.msg != null) {
			return super.msg;
		}

		if (X.empty(messages)) {
			buildMessages();
			if (X.empty(messages)) {
				if (messageIfEmpty != null) {
					getMessages().add(messageIfEmpty);
				} else {
					throw new FIllegalStateException("Nothing to send. Set deault empty message or do check before send");
				}
			}
		}
		this.sendedMessages = new ArrayList<>();

		List<List<IBotButton>> keys = getKeyboard();
		TgMessages messages = getMessages();

		if (manyMessage) {
			throw new NI();
		}

		StringBuilder sb = new StringBuilder();
		for (TgMsg bmsg : messages) {
			sb.append(bmsg.get_text()).append("\n");
		}
		TgMsg tgMessage = new TgMsg();
		if (emsgId != null) {
			tgMessage.set_emsgId(emsgId);
		}

		tgMessage.setText(sb.toString());
		tgMessage.setParseMode(ParseMode.HTML);
		if (isPreview() == false) {
			tgMessage.setDisableWebPagePreview(true);
		}
		if (X.notEmpty(keys)) {
			tgMessage.setKeyboard(Tgc.newKeyboard((List) keys));
		}
		return msg = tgMessage;
	}
}
