package nett.keyboard;

import botcore.clb.IBotButton;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class TgInlineKeyboardButton extends InlineKeyboardButton implements IBotButton<TgInlineKeyboardButton> {

	@Getter
	private boolean visible = true;

	public TgInlineKeyboardButton() {
		super();
	}

	public static boolean isVisible(InlineKeyboardButton key) {
		return !(key instanceof TgInlineKeyboardButton) ? true : ((TgInlineKeyboardButton) key).isVisible();
	}

	public static TgInlineKeyboardButton of(String text) {
		TgInlineKeyboardButton bt = new TgInlineKeyboardButton();
		bt.setCallbackData(text);
		bt.setText(text);
		return bt;
	}

	public TgInlineKeyboardButton setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
}