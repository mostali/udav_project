package nett.keyboard;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

public class TgKeyboardButton extends KeyboardButton {
	@Getter
	private boolean visible = true;

	public TgKeyboardButton() {
	}

	public TgKeyboardButton(String text) {
		super(text);
	}

	public static boolean isVisible(KeyboardButton key) {
		return !(key instanceof TgKeyboardButton) ? true : ((TgKeyboardButton) key).isVisible();
	}

	public TgKeyboardButton setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
}