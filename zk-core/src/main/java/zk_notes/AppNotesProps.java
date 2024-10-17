package zk_notes;

import mp.utl_odb.tree.AppPropDef;
import mpc.env.APP;

public class AppNotesProps<T> {

	//	public static final AppPropDef<Integer> APR_STANDS_DIR = new AppPropDef("stands.dirs", "");
//	public static final AppPropDef<Integer> APR_LOG_CACHE_SEC = new AppPropDef("log.cache.sec", 300);
//	public static final AppPropDef<Integer> APR_LOG_CACHE_VIEW_MAX_MB = new AppPropDef("log.view.mb", 22);
//	public static final AppPropDef<Boolean> APR_DIR_VIEW_ALWAYS_OPENED = new AppPropDef("log.dirview.always_opened", true);
//	public static final AppPropDef<Boolean> APR_DIRVIEW_HIDE_ARC = new AppPropDef("log.dirview.hide_arc", false);
	public static final AppPropDef<String> APR_UNZIP_ENCODING = new AppPropDef("app.unzip.encoding", "CP866");
	public static final AppPropDef<Boolean> APR_BOT_TG_ENABLE = new AppPropDef(APP.APK_TG_BT_ENABLE, false);
	public static final AppPropDef<Boolean> APR_BOT_VK_ENABLE = new AppPropDef(APP.APK_VK_BT_ENABLE, false);


	public static final String PAGE_CONFIG_OPEN = "page.config.open";
}
