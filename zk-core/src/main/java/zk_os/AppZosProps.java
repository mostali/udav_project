package zk_os;

import mp.utl_odb.tree.AppPropDef;
import mp.utl_odb.tree.IAppProps;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.str.ObjTo;
import mpc.str.condition.LogGetterDate;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import zk_notes.AppNotesCore;
import zk_notes.AppNotesProps;
import zk_page.ZKSession;
import zk_page.behaviours.BgImg;
import zk_page.core.SpVM;
import zk_notes.node_state.FormState;
import zklogapp.AppLogCore;
import zklogapp.AppLogProps;

import java.util.List;

public class AppZosProps<T> {

	public static final String LOGO_HEADER_OPEN = "logo.header.open";

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	public static List<AppPropDef> initAppProps() {
		return IAppProps.getAllProps(//
				AppZosProps.class, TREE_PROPS,//
				AppNotesProps.class, AppNotesCore.TREE_PROPS,//
				AppLogProps.class, AppLogCore.TREE_PROPS//
		);
	}

	public static final AppPropDef<Boolean> APP_WEB_SYNC = new AppPropDef("app.web.sync", true).desc("Restart page after adding item's");

	public static final AppPropDef<String> AUTO_GRID_PX = new AppPropDef("auto.grid.px", "20,0,0").desc("[GRID_SIZE, TOP, LEFT]");

	public static final AppPropDef<String> CHARSET = new AppPropDef("app.charset", "utf8");

//	public static final AppPropDef<String> APR_HOST = new AppPropDef("app.host", "");

	public static final AppPropDef<String> APR_LOG_DATE_FORMAT = new AppPropDef("log.date.format", LogGetterDate.FORMAT_DEFAULT);

	public static final AppPropDef<String> APD_LOG_LINE_MAPPING = new AppPropDef("log.line.mapping", "0,1,2,3,4");
	public static final AppPropDef<Boolean> APD_IS_DEBUG_ENABLE = new AppPropDef(APP.APK_IS_DEBUG, false).autoInitHolder(APP.class);
	public static final AppPropDef<Boolean> APD_IS_PROM_ENABLE = new AppPropDef(APP.APK_PROM_ENABLE, true).autoInitHolder(APP.class);
	public static final AppPropDef<Boolean> APD_IS_DEV_ENABLE = new AppPropDef(APP.APK_DEV_ENABLE, true).autoInitHolder(APP.class);

	public static final AppPropDef<Boolean> APD_IS_TRACE_HTTP_CALL = new AppPropDef("httpmsg.trace", false);

	//	public static final AppPropDef<EnDisAuto> APD_ZOS_NIGHTTHEME_DISABLE = new AppPropDef("zos.nighttheme.mode", EnDisAuto.AUTO);

	//	public static final PageStateAppPropDef<BgImg> APD_ZOS_BGIMG = new PageStateAppPropDef("app.bg.img", BgImg.BG_SEC_PNG);
	public static final PageStateAppPropDef<List<String>> APD_ZOS_BGIMG = (PageStateAppPropDef<List<String>>) new PageStateAppPropDef("app.bg.img", ARR.as(BgImg.BGIMG_DEFAULT)).setDefValueSupplier(BgImg::getAllDefaultBgImages);//.viewType(AppPropDef.ViewType.DD);


	//
	//
	//

//	public static class GridAppPropDef extends AppPropDef<String> {
//
//		public GridAppPropDef(Integer gridPx) {
//			super("auto.grid.px", gridPx + ",0,0");
//		}
//
//		public static int[] getGrid(String pat) {
//			String[] grid = SPLIT.argsByComma(pat);
//			return new int[]{UST.INT(grid[0]), UST.INT(grid[1]), UST.INT(grid[2])};
//		}
//	}

//	public static final GridAppPropDef AUTO_GRID_PX = new GridAppPropDef(100);

	public static class PageStateAppPropDef<T> extends AppPropDef<T> {

		public PageStateAppPropDef(String key, T val) {
			super(key, val);
		}

		@Override
		public void update(Object newValue) {
			FormState formState = pageState(true);
			if (isTypeCollection()) {
				formState.set(getPropName(), ARRi.first((List) newValue, null));
			} else {
				formState.set(getPropName(), newValue);
			}
		}

		private FormState pageState(boolean... create) {
			return FormState.ofPageState_orCreate(SpVM.get().sdn0(), ARG.isDefEqTrue(create));
		}

		@Override
		public T getValueOrDefault(T... defRq) {
			FormState formState = pageState();
			String key = getPropName();
			if (!isTypeCollection()) {
				Object as = formState.getAs(key, getDefaultType(), defRq);
				return (T) as;
			}
			//it Collection
			String pageStateValue = formState.get(key, null);
			return X.notEmpty(pageStateValue) ? (T) ARR.as(pageStateValue) : ARG.toDefThrowMsg(() -> X.f("Not found value '%s' (not check store)", key), defRq);
		}

		private boolean isTypeCollection() {
			return List.class.isAssignableFrom(getDefaultType());
		}

	}

	public static class SessionStateAppPropDef<T> extends AppPropDef<T> {

		public SessionStateAppPropDef(String key, T val) {
			super(key, val);
		}

		@Override
		public void update(Object newValue) {
			ZKSession.getSessionAttrsMap().put(getPropName(), ObjTo.objTo(newValue, getDefaultType()));
		}

		@Override
		public T getValueOrDefault(T... defRq) {
			return ZKSession.getSessionAttrs().getAs(getPropName(), getDefaultType(), defRq);
		}

		private boolean isTypeCollection() {
			return List.class.isAssignableFrom(getDefaultType());
		}

	}
}

