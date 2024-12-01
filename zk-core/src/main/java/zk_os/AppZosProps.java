package zk_os;

import langj.seq.SeqWalk;
import mp.utl_odb.tree.AppPropDef;
import mp.utl_odb.tree.IAppProps;
import mp.utl_odb.tree.UTree;
import mpc.str.condition.LogGetterDate;
import mpc.types.abstype.AbsType;
import mpu.core.ARG;
import zk_notes.AppNotesCore;
import zk_notes.AppNotesProps;
import zk_page.behaviours.BgImg;
import zk_page.core.SpVM;
import zk_page.node_state.FormState;
import zklogapp.AppLogCore;
import zklogapp.AppLogProps;

public class AppZosProps<T> {

	public static final String LOGO_HEADER_OPEN = "logo.header.open";

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	public static void initAppProps() {
		IAppProps.getAllProps(//
				AppZosProps.class, TREE_PROPS,//
				AppNotesProps.class, AppNotesCore.TREE_PROPS,//
				AppLogProps.class, AppLogCore.TREE_PROPS//
		);
	}

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
			pageState(true).update(name(), newValue);
		}

		private FormState pageState(boolean... create) {
			return FormState.ofPageState(SpVM.get().sdn(), ARG.isDefEqTrue(create));
		}

		@Override
		public T getValueOrDefault(T... defRq) {
			return (T) pageState().getAs(name(), getDefaultType(), defRq);
		}

	}

	public static final AppPropDef<Boolean> APP_WEB_SYNC = new AppPropDef("app.web.sync", true);

	public static final AppPropDef<String> AUTO_GRID_PX = new AppPropDef("auto.grid.px", "20,0,0");

	public static final AppPropDef<String> CHARSET = new AppPropDef("app.charset", "utf8");

//	public static final AppPropDef<String> APR_HOST = new AppPropDef("app.host", "");

	public static final AppPropDef<String> APR_LOG_DATE_FORMAT = new AppPropDef("log.date.format", LogGetterDate.FORMAT_DEFAULT);

	public static final AppPropDef<String> APR_LOG_LINE_MAPPING = new AppPropDef("log.line.mapping", "0,1,2,3,4");

	//	public static final AppPropDef<EnDisAuto> APD_ZOS_NIGHTTHEME_DISABLE = new AppPropDef("zos.nighttheme.mode", EnDisAuto.AUTO);

	public static final PageStateAppPropDef<BgImg> APD_ZOS_BGIMG = new PageStateAppPropDef("app.bg.img", BgImg.BG_SEC_PNG);

}
