package zk_os.walkers;

import mpc.fs.UF;
import mpu.core.ARG;
import mpu.func.Function3;
import mpu.pare.Pare3;
import mpe.call_msg.core.NodeID;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.coms.AFC;
import zk_os.coms.AFCC;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//new PlaneWalker() {
//				@Override
//				protected Boolean walkSd3(String sd3, Path dir, FormState planeState) {
//					new PagesWalker(sd3) {
//						@Override
//						protected Boolean walkPage(String pagename, FormState pageState) {
//							applierPage.apply(sd3, pagename, pageState);
//							return true;
//						}
//					}.withSysPages(false).doWalk();
//					return true;
//				}
//			}.withIndex(true).withSysSd3(true).withUserDomain(true).doWalk();
public abstract class PlaneWalker {

	public PlaneWalker() {
	}

	private boolean withUserDomain = false;

	public PlaneWalker withUserDomain(boolean... withUserDomain) {
		this.withUserDomain = ARG.isDefNotEqFalse(withUserDomain);
		return this;
	}

	private boolean withSysSd3 = false;

	public PlaneWalker withSystemPlanes(boolean... withSysSd3) {
		this.withSysSd3 = ARG.isDefNotEqFalse(withSysSd3);
		return this;
	}

	private boolean withIndex = false;

	public PlaneWalker withIndex(boolean... withIndex) {
		this.withIndex = ARG.isDefNotEqFalse(withIndex);
		return this;
	}

	private boolean skipSecurity = false;

	public PlaneWalker withSkipSecurity(boolean... skipSecurity) {
		this.skipSecurity = ARG.isDefNotEqFalse(skipSecurity);
		return this;
	}

	protected abstract Boolean walkPlane(String plane, Path dir, PlaneState planeState);

	public static <R> List<R> doWalkFunc(Function3<String, String, PageState, R> applierPage) {
		List results = new ArrayList<>();
		new PlaneWalker() {
			@Override
			protected Boolean walkPlane(String plane, Path dir, PlaneState planeState) {
				new PagesWalker(plane) {
					@Override
					protected Boolean walkPage(String pagename, PageState pageState) {
						results.add(applierPage.apply(_plane, pagename, pageState));
						return true;
					}
				}.withSysPages(false).doWalk();
				return true;
			}
		}.withIndex(true).withSystemPlanes(true).withUserDomain(true).doWalk();
		return results;
	}

	public static List<Pare3<String, Path, PlaneState>> doWalkToList(boolean withIndex, boolean withSystemPlanes, boolean withUserDomain) {
		List<Pare3<String, Path, PlaneState>> list = new LinkedList<>();
		new PlaneWalker() {
			@Override
			protected Boolean walkPlane(String plane, Path dir, PlaneState planeState) {
				list.add(Pare3.of(plane, dir, planeState));
				return true;
			}
		}.withIndex(withIndex).withSystemPlanes(withSystemPlanes).withUserDomain(withUserDomain).doWalk();
		return list;
	}

	public PlaneWalker doWalk() {
		Set<Path> allSd3Dir = AFC.PLANES.DIR_PLANES_LS_CLEAN(withUserDomain);

//		if (withIndex && !walkSd3(ItemPath.PAGE_INDEX_ALIAS, AFC.DIR_PLANES().resolve(ItemPath.PAGE_INDEX_ALIAS), FormState.ofPlaneState(ItemPath.PAGE_INDEX_ALIAS))) {
//			return this;
//		}

		for (Path dirSd3 : allSd3Dir) {
			String sd3 = UF.fn(dirSd3);
			if (NodeID.isPlaneAliasIndex(sd3) && !withIndex) {
				continue;
			}
			if (!withSysSd3 && AFCC.Filter.IS_SYS_SD3.test(sd3)) {
				continue;
			}
			PlaneState planeState = AppStateFactory.forPlane(sd3);
			if (!skipSecurity && !planeState.isAllowedAccess_VIEW()) {
				continue;
			}
			if (!walkPlane(sd3, dirSd3, planeState)) {
				break;
			}
		}
		return this;

	}

}
