package nett.keyboard;

import botcore.clb.IBotKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Iterator;
import java.util.List;


public class TgReplyKeyboardMarkup extends ReplyKeyboardMarkup implements IBotKeyboard {

	@Override
	public void setKeyboard(List<KeyboardRow> keyboard) {
		Iterator<KeyboardRow> itRow = keyboard.iterator();
		while (itRow.hasNext()) {
			KeyboardRow row = itRow.next();
			Iterator<KeyboardButton> itButton = row.iterator();
			while (itButton.hasNext()) {
				KeyboardButton bt = itButton.next();
				if (!TgKeyboardButton.isVisible(bt)) {
					itButton.remove();
				}
			}
			if (row.isEmpty()) {
				itRow.remove();
			}
		}
		if (keyboard.isEmpty()) {
			return;
		}
		super.setKeyboard(keyboard);
	}
}
