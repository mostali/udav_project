package zk_notes;

import mp.utl_odb.tree.AppPropDef;
import mpc.env.APP;

public class AppNotesProps {

	public static final AppPropDef<Boolean> APR_BOT_TG_ENABLE = new AppPropDef(APP.APK_TG_BT_ENABLE, APP.isEnableBotTg());
	public static final AppPropDef<Boolean> APR_BOT_VK_ENABLE = new AppPropDef(APP.APK_VK_BT_ENABLE, APP.isEnableBotVkLPS());

	public static final AppPropDef<Boolean> APK_NICE_BGCOLOR_ENABLE = (AppPropDef<Boolean>) new AppPropDef("NICE_BGCOLOR_ENABLE", true).desc("All native component's has nice background color with gradient");
	public static final AppPropDef<Boolean> APR_USE_PUBLIC_MODE = (AppPropDef<Boolean>) new AppPropDef("APR_USE_PUBLIC_MODE", false).desc("All created notes will be visible for all by default (set property secv=@)");

//	public static final AppZosProps.PageStateAppPropDef<Boolean> MAIN_MENU_ENABLE = (AppZosProps.PageStateAppPropDef<Boolean>) new AppZosProps.PageStateAppPropDef("MAIN_MENU_ENABLE", true);

//	public static final AppPropDef<Boolean> APR_DEV_ENABLE = new AppPropDef(APP.APP_DEV_ENABLE, APP.isPromEnable());

//	public static final AppPropDef<Boolean> APP_TASKS_V1_PANEL_ENABLE = new AppPropDef("app.tasks-v1.panel.enable", false);

	//	public static final AppPropDef<Boolean> APP_TASKS_PANEL_ENABLE = new AppPropDef("app.tasks.panel.enable", false);
	public static final AppPropDef<Integer> APP_TASKS_PANEL_UPDATE_SEC = (AppPropDef<Integer>) new AppPropDef("app.tasks.panel.update.sec", 30).desc("Update task panel every N seconds");

	//
	//
	//
	public static final String NAV_MENU_OPENED = "NAV_MENU_OPENED";

	public static final String PP_QUICK_NOTES_OPENED = "PP_QUICK_NOTES_OPENED";

	public static final String PP_CONFIG_OPENED = "PP_CONFIG_OPENED";
	public static final String PP_BOTTOM_HISTORY_OPEN = "PP_BOTTOM_HISTORY_OPEN";

	public static final String PP_TASKS_OPENED = "PP_TASKS_OPENED";
	public static final String PP_TASKS_V1_OPENED = "PP_TASKS_V1_OPENED";


}
