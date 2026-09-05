package botcore.clb;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import mpu.core.EQ;
import mpu.X;
import mpc.exception.FIllegalStateException;
import mpc.json.UGson;

public abstract class BotCallback<B extends IBotButton> implements IClb<B> {
	public static final String CALLBACK_ = "_CALLBACK_";
	public static final String PATH_DEL = "/";
	public static final String PATH_END = "@";

	@Getter
	@Setter
	private final String bt_label, bt_data;
	@Setter
	private String routeDesc, path;

	public boolean eq(BotCallback clb) {
		if (clb == null) {
			return false;
		} else if (this == clb) {
			return true;
		}
		return EQ.equalsUnsafe(bt_label, clb.bt_label)//
				&& EQ.equalsUnsafe(bt_data, clb.bt_data)//
				&& EQ.eq(routeDesc, clb.routeDesc);//
	}

	public static String toStringLog(BotCallback botCallback) {
		return botCallback == null ? "BotCallbackNULL" : botCallback.cn() + ":" + botCallback.getBt_label() + ":" + botCallback.getBt_data();
	}

	public String path() {
		return path != null ? path : (routeDesc + PATH_DEL + bt_data + PATH_END);
	}

	public String routeDesc() {
		return routeDesc;
	}

	public String label() {
		return bt_label;
	}

	public String data() {
		return bt_data;
	}

	public JsonObject dataAsJson() {
		return UGson.JO(bt_data);
	}

	protected BotCallback(String bt_label, String bt_data) {
		//UC.state(route_key.indexOf(PATH_DEL) == -1, "ClbKeyRoute contains Del");
		this.bt_label = bt_label;//UC.NE();
		this.bt_data = X.empty(bt_data) ? (bt_label == null ? null : labelToData(bt_label)) : bt_data;
		if (bt_data != null && this.bt_data.length() > getBtDataLimit()) {
			throw new FIllegalStateException("Button data '%s' length more %s characters", this.bt_data, getBtDataLimit());
		}
	}

	protected int getBtDataLimit() {
		throw new UnsupportedOperationException("need impl:" + cn());
	}

	@Override
	public String toString() {
		return cn() + "{" + "clb_route='" + routeDesc + '\'' + ", button_message='" + label() + '\'' + ", button_data='" + data() + '\'' + ", path='" + path() + '\'' + '}';
	}

	public String cn() {
		return getClass().getSimpleName();
	}

	private static String labelToData(String bt_label) {
		return bt_label.trim().toLowerCase().replaceAll(" ", "-");
	}

	public String getCallbackData2From(String data) {
		String clbPath = path();
		if (!data.startsWith(clbPath)) {
			return null;
		}
		if (clbPath.length() == data.length()) {
			return "";
		}
		return data.substring(clbPath.length());
	}

	boolean decode = false;

	public BotCallback decode(boolean decode) {
		this.decode = decode;
		return this;
	}

}
