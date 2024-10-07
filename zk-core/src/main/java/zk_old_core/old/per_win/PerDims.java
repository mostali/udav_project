package zk_old_core.old.per_win;

import mpc.types.ruprops.RuProps;

public class PerDims {
	public static String read_left(RuProps props) {
		return props.getString("left", "10px");
	}

	public static String read_top(RuProps props) {
		return props.getString("top", "10px");
	}

	public static String read_width(RuProps props) {
		return props.getString("width", "100px");
	}

	public static String read_height(RuProps props) {
		return props.getString("height", "100px");
	}

	public static void write_left(RuProps props, String left) {
		props.setString("left", left);
	}

	public static void write_top(RuProps props, String top) {
		props.setString("top", top);
	}

	public static void write_width(RuProps props, String width) {
		props.setString("width", width);
	}

	public static void write_height(RuProps props, String height) {
		props.setString("height", height);
	}

}
