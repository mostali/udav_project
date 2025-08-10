package zk_os.walkers;

import mpc.fs.UF;
import mpu.core.ARG;
import mpu.func.Function3;
import mpu.pare.Pare3;
import udav_net.apis.zznote.ItemPath;
import zk_notes.node_state.libs.PlaneState;
import zk_os.AFC;
import zk_os.AFCC;
import zk_notes.node_state.FormState;

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

	public PlaneWalker withSysSd3(boolean... withSysSd3) {
		this.withSysSd3 = ARG.isDefNotEqFalse(withSysSd3);
		return this;
	}

	private boolean withIndex = false;

	public PlaneWalker withIndex(boolean... withIndex) {
		this.withIndex = ARG.isDefNotEqFalse(withIndex);
		return this;
	}

	protected abstract Boolean walkSd3(String sd3, Path dir, FormState planeState);

	public static <R> List<R> doWalkFunc(Function3<String, String, FormState, R> applier) {
		List results = new ArrayList<>();
		new PlaneWalker() {
			@Override
			protected Boolean walkSd3(String sd3, Path dir, FormState planeState) {
				new PagesWalker(sd3) {
					@Override
					protected Boolean walkPage(String pagename, FormState pageState) {
						results.add(applier.apply(sd3, pagename, pageState));
						return true;
					}
				}.withSysPages(false).doWalk();
				return true;
			}
		}.withIndex(true).withSysSd3(true).withUserDomain(true).doWalk();
		return results;
	}

	public static List<Pare3<String, Path, FormState>> doWalkToList(boolean withIndex, boolean withSysSd3, boolean withUserDomain) {
		List<Pare3<String, Path, FormState>> list = new LinkedList<>();
		new PlaneWalker() {
			@Override
			protected Boolean walkSd3(String sd3, Path dir, FormState planeState) {
				list.add(Pare3.of(sd3, dir, planeState));
				return true;
			}
		}.withIndex(withIndex).withSysSd3(withSysSd3).withUserDomain(withUserDomain).doWalk();
		return list;
	}

	public PlaneWalker doWalk() {
		Set<Path> allSd3Dir = AFC.PLANES.DIR_PLANES_LS_CLEAN(withUserDomain);

//		if (withIndex && !walkSd3(ItemPath.PAGE_INDEX_ALIAS, AFC.DIR_PLANES().resolve(ItemPath.PAGE_INDEX_ALIAS), FormState.ofPlaneState(ItemPath.PAGE_INDEX_ALIAS))) {
//			return this;
//		}

		for (Path dirSd3 : allSd3Dir) {
			String sd3 = UF.fn(dirSd3);
			if (ItemPath.isAliasIndexPlane(sd3) && !withIndex) {
				continue;
			}
			if (!withSysSd3 && AFCC.Filter.IS_SYS_SD3.test(sd3)) {
				continue;
			}
			PlaneState formState = FormState.ofPlaneState_orCreate(sd3);
			if (!formState.isAllowedAccess_View(true)) {
				continue;
			}
			if (!walkSd3(sd3, dirSd3, formState)) {
				break;
			}
		}
		return this;

	}

}
