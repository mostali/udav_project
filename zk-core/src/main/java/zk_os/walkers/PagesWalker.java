package zk_os.walkers;

import mpu.core.ARG;
import mpu.func.Function2;
import mpu.func.Function3;
import mpu.pare.Pare;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_page.core.FinderPSP;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;
import java.util.*;

public abstract class PagesWalker {

	public final String _plane;

	public String plane() {
		return _plane;
	}

	public PagesWalker(String plane) {
		this._plane = plane;
	}


	private boolean withSysPages = false;


	public PagesWalker withSysPages(boolean... withSysPages) {
		this.withSysPages = ARG.isDefNotEqFalse(withSysPages);
		return this;
	}

	private boolean skipSecurity = false;

	public PagesWalker withSkipSecurity(boolean... skipSecurity) {
		this.skipSecurity = ARG.isDefNotEqFalse(skipSecurity);
		return this;
	}

	public static <R> List<R> doWalkFuncAllPlanes(Function3<String, String, ObjState, R> applier) {
		return PlaneWalker.doWalkFunc(applier);
	}

	public static <R> List<R> doWalkFunc(String sd3, Function2<String, ObjState, R> applier) {
		List<R> r = new ArrayList<>();
		new PagesWalker(sd3) {
			@Override
			protected Boolean walkPage(String pagename, ObjState pageState) {
				r.add(applier.apply(pagename, pageState));
				return true;
			}
		}.withSysPages(false).doWalk();
		return r;
	}

	public static List<Pare<String, ObjState>> doWalkToList(String sd3, boolean withSysPages) {
		List<Pare<String, ObjState>> list = new LinkedList<>();
		new PagesWalker(sd3) {
			@Override
			protected Boolean walkPage(String pagename, ObjState pageState) {
				list.add(Pare.of(pagename, pageState));
				return true;
			}
		}.withSysPages(withSysPages).doWalk();
		return list;
	}

	protected abstract Boolean walkPage(String pagename, ObjState pageState);

	public PagesWalker doWalk() {

//		if (!walkPage(ItemPath.PAGE_INDEX_ALIAS, FormState.ofPageState(Pare.of(sd3, ItemPath.PAGE_INDEX_ALIAS)))) {
//			return this;
//		}

		if (withSysPages) {
			Set<String> allBusyPages = FinderPSP.getAllBusyPages(_plane);
			for (String pagename : allBusyPages) {
				Pare<String, String> pageSdn = Pare.of(_plane, pagename);
				if (!walkPage(pagename, AppStateFactory.forPage(pageSdn))) {
					break;
				}
			}
		}

		TreeSet<Path> getAllPagenamesPaths = AFC.PAGES.DIR_PAGES_LS_CLEAN(_plane);
//

		for (Path pageDirPath : getAllPagenamesPaths) {
			String pagename = pageDirPath.getFileName().toString();
			Pare<String, String> pageSdn = Pare.of(_plane, pagename);
			ObjState formState = AppStateFactory.forPage(pageSdn);
			if (!skipSecurity && !formState.isAllowedAccess_EDIT()) {
				continue;
			}
			if (!walkPage(pagename, formState)) {
				break;
			}
		}
		return this;

	}
}
