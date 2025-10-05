package zk_notes;

import mp.utl_odb.tree.AppPropDef;
import mpc.env.APP;
import zk_os.AppZosProps;

public class AppNotesProps {

	public static final AppPropDef<Boolean> APR_BOT_TG_ENABLE = new AppPropDef(APP.APK_TG_BT_ENABLE, APP.isEnableBotTg());
	public static final AppPropDef<Boolean> APR_BOT_VK_ENABLE = new AppPropDef(APP.APK_VK_BT_ENABLE, APP.isEnableBotVkLPS());

	public static final AppPropDef<Boolean> APR_USE_PUBLIC_MODE = new AppPropDef("APR_USE_PUBLIC_MODE", false);

	public static final AppZosProps.PageStateAppPropDef<Boolean> MAIN_MENU_ENABLE = (AppZosProps.PageStateAppPropDef<Boolean>) new AppZosProps.PageStateAppPropDef("MAIN_MENU_ENABLE", true);

//	public static final AppPropDef<Boolean> APR_DEV_ENABLE = new AppPropDef(APP.APP_DEV_ENABLE, APP.isPromEnable());

	public static final AppPropDef<Boolean> APP_TASKS_V1_PANEL_ENABLE = new AppPropDef("app.tasks-v1.panel.enable", false);

//	public static final AppPropDef<Boolean> APP_TASKS_PANEL_ENABLE = new AppPropDef("app.tasks.panel.enable", false);
	public static final AppPropDef<Integer> APP_TASKS_PANEL_UPDATE_SEC = new AppPropDef("app.tasks.panel.update.sec", 12);

	//
 	//
 	//
	public static final String PAGE_PANEL_CONFIG_OPENED = "panel.config.open";
	public static final String PAGE_PANEL_BOTTOM_HISTOTY_PANEL_OPEN = "panel.bhp.open";
	public static final String LOGO_HEADER_OPEN = "logo.header.open";

	public static final String PAGE_PANEL_TASKS_OPENED = "panel.tasks.open";


}
