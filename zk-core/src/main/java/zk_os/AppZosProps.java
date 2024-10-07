package zk_os;

import mp.utl_odb.tree.AppPropDef;
import mp.utl_odb.tree.UTree;
import mpu.core.ARR;
import mpc.rfl.RFL;
import mpc.str.condition.LogGetterDate;
import zk_page.behaviours.BgImg;

import java.util.List;

public class AppZosProps<T> {

	public static final AppPropDef<String> APR_HOST = new AppPropDef("app.host", "");
	public static final AppPropDef<String> APR_LOG_DATE_FORMAT = new AppPropDef("log.date.format", LogGetterDate.FORMAT_DEFAULT);
	public static final AppPropDef<String> APR_LOG_LINE_MAPPING = new AppPropDef("log.line.mapping", "0,1,2,3,4");
	//	public static final AppPropDef<EnDisAuto> APD_ZOS_NIGHTTHEME_DISABLE = new AppPropDef("zos.nighttheme.mode", EnDisAuto.AUTO);
	public static final AppPropDef<BgImg> APD_ZOS_NIGHTTHEME_DISABLE = new AppPropDef("zos.nighttheme.mode", BgImg.BG_SEC_PNG);
	public static final String LOGO_HEADER_OPEN = "logo.header.open";

	public static List<AppPropDef> getAllProps(UTree treeProps) {
		List<AppPropDef> appLogProps = RFL.fieldValuesSt(AppZosProps.class, AppPropDef.class, true, (List<AppPropDef>) ARR.EMPTY_LIST);
		appLogProps.forEach(p -> p.setTreeProps(treeProps));
		return appLogProps;
	}
}
