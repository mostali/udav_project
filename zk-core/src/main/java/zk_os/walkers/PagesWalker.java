package zk_os.walkers;

import mpu.core.ARG;
import mpu.func.Function2;
import mpu.func.Function3;
import mpu.pare.Pare;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_page.core.FinderPSP;
import zk_notes.node_state.FormState;

import java.nio.file.Path;
import java.util.*;

public abstract class PagesWalker {

	public final String sd3;

	public PagesWalker(String sd3) {
		this.sd3 = sd3;
	}


	private boolean withSysPages = false;


	public PagesWalker withSysPages(boolean... withSysPages) {
		this.withSysPages = ARG.isDefNotEqFalse(withSysPages);
		return this;
	}

	public static <R> List<R> doWalkFuncAllPlanes(Function3<String, String, FormState, R> applier) {
		return PlaneWalker.doWalkFunc(applier);
	}

	public static <R> List<R> doWalkFunc(String sd3, Function2<String, FormState, R> applier) {
		List<R> r = new ArrayList<>();
		new PagesWalker(sd3) {
			@Override
			protected Boolean walkPage(String pagename, FormState pageState) {
				r.add(applier.apply(pagename, pageState));
				return true;
			}
		}.withSysPages(false).doWalk();
		return r;
	}

	public static List<Pare<String, FormState>> doWalkToList(String sd3, boolean withSysPages) {
		List<Pare<String, FormState>> list = new LinkedList<>();
		new PagesWalker(sd3) {
			@Override
			protected Boolean walkPage(String pagename, FormState pageState) {
				list.add(Pare.of(pagename, pageState));
				return true;
			}
		}.withSysPages(withSysPages).doWalk();
		return list;
	}

	protected abstract Boolean walkPage(String pagename, FormState pageState);

	public PagesWalker doWalk() {

//		if (!walkPage(ItemPath.PAGE_INDEX_ALIAS, FormState.ofPageState(Pare.of(sd3, ItemPath.PAGE_INDEX_ALIAS)))) {
//			return this;
//		}

		if (withSysPages) {
			Set<String> allBusyPages = FinderPSP.getAllBusyPages(sd3);
			for (String pagename : allBusyPages) {
				Pare<String, String> pageSdn = Pare.of(sd3, pagename);
				if (!walkPage(pagename, AppStateFactory.ofPageName_orCreate(pageSdn))) {
					break;
				}
			}
		}

		TreeSet<Path> getAllPagenamesPaths = AFC.PAGES.DIR_PAGES_LS_CLEAN(sd3);
//

		for (Path pageDirPath : getAllPagenamesPaths) {
			String pagename = pageDirPath.getFileName().toString();
			Pare<String, String> pageSdn = Pare.of(sd3, pagename);
			FormState formState = AppStateFactory.ofPageName_orCreate(pageSdn);
			if (!formState.isAllow_byProp_SECE(false, true, true)) {
				continue;
			}
			if (!walkPage(pagename, formState)) {
				break;
			}
		}
		return this;

	}
}
