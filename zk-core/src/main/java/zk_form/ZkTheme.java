package zk_form;

import mpc.exception.WhatIsTypeException;
import zk_form.notify.NtfLevel;

public class ZkTheme {

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

	public static String getClassStyle(NtfLevel level) {
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
