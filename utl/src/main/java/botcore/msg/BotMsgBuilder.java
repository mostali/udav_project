package botcore.msg;

import botcore.BotRoute;
import botcore.clb.IBotButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mpu.IT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public abstract class BotMsgBuilder<M extends IBotMsg> {
	protected M messageIfEmpty;

	public BotMsgBuilder defMessageIfEmpty(M message) {
		this.messageIfEmpty = message;
		return this;
	}


	protected BotMessages messages = null;


	public void addMessageFmt(String msg, Object... args) {
		addMessage(String.format(msg, args));
	}

	public abstract BotMsgBuilder addMessage(String... msg);

	public abstract BotMessages getMessages();

	@Getter
	protected List<Serializable> sendedMessages = null;
	@Getter
	@Setter
	private List<List<IBotButton>> keyboard = null;

	@Getter
	private boolean preview = true;
	protected boolean manyMessage = false;
	protected Integer emsgId = null;

	public BotMsgBuilder preview(boolean preview) {
		this.preview = preview;
		return this;
	}

	public BotMsgBuilder manyMessage(boolean manyMessage) {
		this.manyMessage = manyMessage;
		return this;
	}

	public BotMsgBuilder emsgId(Integer emsgId) {
		this.emsgId = emsgId;
		return this;
	}

	public BotMsgBuilder addKeyboardRow(List keys) {
		if (keyboard == null) {
			keyboard = new ArrayList<>();
		}
		keyboard.add(keys);
		return this;
	}

	public BotMsgBuilder addKeyboardRow(IBotButton... keys) {
		return addKeyboardRow(Arrays.asList(IT.notEmpty(keys)));
	}

	public BotMsgBuilder send(BotRoute tgRouteExt) {
		IBotMsg tgMsg = getMsgOrCreate();
		Serializable msg = tgRouteExt.sendMsg(tgMsg);
		getSendedMessages().add(msg);
		return this;
	}

	protected M msg = null;

	public abstract M getMsgOrCreate();

	protected abstract BotMsgBuilder buildMessages();
}
