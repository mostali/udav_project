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

	public static final String PAGE_CONFIG_OPEN = "page.config.open";
	public static final String TASKS_OPEN = "tasks.open";
}
