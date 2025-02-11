package zk_os.walkers;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;
import mpu.pare.Pare;
import zk_os.AFC;
import zk_page.core.FinderPSP;
import zk_notes.node_state.FormState;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RequiredArgsConstructor
public abstract class PagesWalker {

	public final String sd3;

	private boolean withSysPages = false;


	public PagesWalker withSysPages(boolean... withSysPages) {
		this.withSysPages = ARG.isDefNotEqFalse(withSysPages);
		return this;
	}

	public static List<Pare<String, FormState>> doWalkToList(String sd3, boolean withSysPages) {
		List<Pare<String, FormState>> list = new LinkedList<>();
		new PagesWalker(sd3) {
			@Override
			protected Boolean walkPage(String pagename, FormState formState) {
				list.add(Pare.of(pagename, formState));
				return true;
			}
		}.withSysPages(withSysPages).doWalk();
		return list;
	}

	protected abstract Boolean walkPage(String pagename, FormState formState);

	public PagesWalker doWalk() {

//		if (!walkPage(ItemPath.PAGE_INDEX_ALIAS, FormState.ofPageState(Pare.of(sd3, ItemPath.PAGE_INDEX_ALIAS)))) {
//			return this;
//		}

		if (withSysPages) {
			Set<String> allBusyPages = FinderPSP.getAllBusyPages(sd3);
			for (String pagename : allBusyPages) {
				Pare<String, String> pageSdn = Pare.of(sd3, pagename);
				if (!walkPage(pagename, FormState.ofPageState_orCreate(pageSdn))) {
					break;
				}
			}
		}

		TreeSet<Path> getAllPagenamesPaths = AFC.PAGES.DIR_PAGES_LS_CLEAN(sd3);
//

		for (Path pageDirPath : getAllPagenamesPaths) {
			String pagename = pageDirPath.getFileName().toString();
			Pare<String, String> pageSdn = Pare.of(sd3, pagename);
			FormState formState = FormState.ofPageState_orCreate(pageSdn);
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
