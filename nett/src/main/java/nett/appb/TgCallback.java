package nett.appb;

import nett.Tgc;
import botcore.clb.BotCallback;
import nett.keyboard.TgInlineKeyboardButton;

import java.util.Map;

public class TgCallback extends BotCallback<TgInlineKeyboardButton> {

	@Override
	protected int getBtDataLimit() {
		return 26;
	}

	private TgCallback(String bt_label, String button_data) {
		super(bt_label, button_data);
	}

	public static TgCallback of(String bt_label) {
		return new TgCallback(bt_label, null);
	}

	public TgInlineKeyboardButton toKey(Object data2, String name, String... color) {
		return Tgc.newSingleInlineKeyboardButton(this, name, data2);
	}

}
