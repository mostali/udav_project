package zk_os;

import mp.utl_odb.tree.AppPropDef;
import mp.utl_odb.tree.IAppProps;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.net.CON;
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

	public static final AppPropDef<Boolean> APP_TASKS_V1_PANEL_ENABLE = new AppPropDef("app.tasks-v1.panel.enable", false);
	public static final AppPropDef<Boolean> APP_TASKS_PANEL_ENABLE = new AppPropDef("app.tasks.panel.enable", false);
	public static final AppPropDef<Integer> APP_TASKS_PANEL_UPDATE_SEC = new AppPropDef("app.tasks.panel.update.sec", 12);

	public static List<AppPropDef> initAppProps() {
		return IAppProps.getAllProps(//
				AppZosProps.class, TREE_PROPS,//
				AppNotesProps.class, AppNotesCore.TREE_PROPS,//
				AppLogProps.class, AppLogCore.TREE_PROPS//
		);
	}

	public static final AppPropDef<Boolean> APP_WEB_SYNC = (AppPropDef<Boolean>) new AppPropDef("app.web.sync", true).desc("Restart page after adding item's");

	public static final AppPropDef<String> AUTO_GRID_PX = (AppPropDef<String>) new AppPropDef("auto.grid.px", "20,0,0").desc("[GRID_SIZE, TOP, LEFT]");

	public static final AppPropDef<String> CHARSET = new AppPropDef("app.charset", "utf8");

	public static final AppPropDef<String> APR_LOG_DATE_FORMAT = new AppPropDef("log.date.format", LogGetterDate.FORMAT_DEFAULT);

	public static final AppPropDef<String> APD_LOG_LINE_MAPPING = new AppPropDef("log.line.mapping", "0,1,2,3,4");

	public static final AppPropDef<Boolean> APD_IS_DEBUG_ENABLE = (AppPropDef<Boolean>) new AppPropDef(APP.APK_IS_DEBUG, false).autoInitHolder(APP.class);
	public static final AppPropDef<Boolean> APD_IS_PROM_ENABLE = (AppPropDef<Boolean>) new AppPropDef(APP.APK_PROM_ENABLE, true).autoInitHolder(APP.class);
	public static final AppPropDef<Boolean> APD_IS_DEV_ENABLE = (AppPropDef<Boolean>) new AppPropDef(APP.APK_DEV_ENABLE, true).autoInitHolder(APP.class);

	public static final AppPropDef<Boolean> APD_IS_TRACE_HTTP_CALL = new AppPropDef("http.msg.trace", false);

	public static final AppPropDef<Boolean> APD_HTTP_ONOFF_SSL = new OnOffSslAppPropDef("http.ssl.enable", CON.onOffSsl);

	public static final PageStateAppPropDef<List<String>> APD_ZOS_BGIMG = (PageStateAppPropDef<List<String>>) new PageStateAppPropDef("app.bg.img", ARR.as(BgImg.BGIMG_DEFAULT)).setDefValueSupplier(BgImg::getAllDefaultBgImages);//.viewType(AppPropDef.ViewType.DD);

	//
 	//

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

	}

	public static class OnOffSslAppPropDef<T> extends AppPropDef<T> {

		public OnOffSslAppPropDef(String key, T val) {
			super(key, val);
		}

		@Override
		public void update(Object newValue) {
			CON.onOffSSl((Boolean) ObjTo.objTo(newValue, getDefaultType()));
		}

		@Override
		public T getValueOrDefault(T... defRq) {
			return (T) CON.onOffSsl;
		}

	}
}

