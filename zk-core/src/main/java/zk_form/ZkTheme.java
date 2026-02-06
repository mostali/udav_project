package zk_form;

import mpc.exception.WhatIsTypeException;
import mpu.pare.Pare;
import mpu.pare.Tuple;
import zk_form.notify.ZKI;
import zk_page.ZKColor;

public class ZkTheme {

	public static final String CTRL_BG_COLOR = "#f3efe9";
	public static final String CTRL_BORDER_COLOR = "#f3efe9";

	public static final Pare TOP_PANEL_LINK_BG_COLOR = Pare.of(ZKColor.BLUE, ZKColor.WHITE);
//	public static final Tuple TOP_PANEL_LINK_BG_COLOR = Tuple.ofObjs(ZKColor.ORANGE, ZKColor.WHITE);


	public static final String DIV_NOTIFY_CHILD = "z-notification-content";
	public static final String DIV_NOTIFY_PARENT_BR = "z-notification-info z-notification-open";


	public static final String DIV_NOTIFY_PARENT_BR_ERROR = "z-notification-error z-notification-open";
	public static final String DIV_NOTIFY_PARENT_BR_WARNING = "z-notification-warning z-notification-open";

	public static final String DIV_NOTIFY_PARENT = "z-notification z-notification-info z-notification-open";

	public static final String INFO_DIV_STYLE = "position: absolute; left: 779px; top: 215px; z-index: 1800; visibility: visible;";
	public static final String INFO_HTML_ICON = "<i class=\"z-notification-icon z-icon-info-circle\"></i>";

	public static final String INFO_COLOR = "#4aa81b";
	public static final int NOTIFY = 100_000;
	public static final int CONFIRMATION = 10_000;
	public static final int ZLOG = 90_000;
	public static final int MAX_INDEX = 9999;

	public static String getClassStyle(ZKI.Level level) {
		switch (level) {
			case INFO:
				return DIV_NOTIFY_PARENT_BR;
			case ERR:
				return DIV_NOTIFY_PARENT_BR_ERROR;
			case WARN:
				return DIV_NOTIFY_PARENT_BR_WARNING;
			default:
				throw new WhatIsTypeException(level);
		}
	}
}
