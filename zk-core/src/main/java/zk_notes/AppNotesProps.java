package zk_notes;

import mp.utl_odb.tree.AppPropDef;
import mpc.env.APP;

public class AppNotesProps {

	public static final AppPropDef<Boolean> APR_BOT_TG_ENABLE = new AppPropDef(APP.APK_TG_BT_ENABLE, APP.isEnableBotTg());
	public static final AppPropDef<Boolean> APR_BOT_VK_ENABLE = new AppPropDef(APP.APK_VK_BT_ENABLE, APP.isEnableBotVkLPS());

//	public static final AppPropDef<Boolean> APR_DEV_ENABLE = new AppPropDef(APP.APP_DEV_ENABLE, APP.isPromEnable());

	public static final String PAGE_CONFIG_OPEN = "page.config.open";
}
