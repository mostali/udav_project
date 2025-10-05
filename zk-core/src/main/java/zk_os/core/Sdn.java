package zk_os.core;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.core.ARG;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import zk_os.coms.AFC;
import zk_page.ZKR;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Sdn extends Pare<String, String> {

	public static Sdn getRq() {
		return SpVM.get().ppi().sdnRq();
	}

	public static Sdn get() {
		return SpVM.get().ppi().sdn0();
	}

//	public static Sdn get0() {
//		return SpVM.get().ppi().sdn0();
//	}

	public static String PLANE() {
		return SpVM.get().ppi().plane();
	}

	public static String PLANERQ() {
		return SpVM.get().ppi().planeRq();
	}

	public Sdn(String plane, String pagename) {
		super(plane, pagename);
	}

	public static Sdn of(String plane, String pagename) {
		return new Sdn(plane, pagename);
	}

	public static Sdn of(Pare pare) {
		return pare instanceof Sdn ? (Sdn) pare : of(pare.keyStr(), pare.valStr());
	}

	public static ItemPath getPathViaWebContext(ItemPath itemPath) {
		SpVM spVM = SpVM.get(null);
		return spVM != null ?//
				getPathRelativeWithIndex_viaWebContext(itemPath, spVM) ://
				ItemPath.getPathRelativeWithIndex(itemPath);
	}

	public static ItemPath getPathRelativeWithIndex_viaWebContext(ItemPath itemPath, SpVM spVM) {
		Path path;
		switch (itemPath.mode) {
			case SINGLY:
				path = Paths.get(spVM.subdomain3Rq()).resolve(spVM.pagenameRq()).resolve(itemPath.nodeName());
				break;
			case PARE:
				path = Paths.get(spVM.subdomain3Rq()).resolve(itemPath.pageName()).resolve(itemPath.nodeName());
				break;
			case ALL:
				path = itemPath.path;
				break;
			default:
				throw new WhatIsTypeException(itemPath.mode);
		}
		return ItemPath.of(path);
	}

	public static boolean existPage(Pare<String, String> sdn) {
		return UFS.existDir(getPageDir(sdn));
	}

	public static Path getPageDir(Pare<String, String> sdn) {
		return AFC.PAGES.getDir(sdn);
	}

	public static Sdn ofPlane(String sd3) {
		return of(sd3, ItemPath.PAGE_INDEX_ALIAS);
	}

	public static Sdn ofRootPlane() {
		return of(ItemPath.PLANE_INDEX_ALIAS, ItemPath.PAGE_INDEX_ALIAS);
	}

	public String plane() {
		return key();
	}

	public static String planeCurrent() {
		return getRq().key();
	}

	public String page() {
		return val();
	}

	public static String pageCurrent() {
		return getRq().val();
	}

	public RSPath getPathType() {
		boolean emptyPage = ItemPath.isPlaneAliasIndexOrEmpty(val());
		if (!emptyPage) {
			return RSPath.PAGE;
		}
		boolean emptyPlane = ItemPath.isPlaneAliasIndexOrEmpty(key());
		return emptyPlane ? RSPath.ROOT : RSPath.PLANE;

	}

	public String toStringPath(String... withItem) {
		return key() + "/" + val() + (ARG.isDef(withItem) ? "/" + ARG.toDef(withItem) : "");
	}

	public static String toCurrentUrl(Sdn sdn, String... queryUrlPart) {
		String query = ARG.toDefOr("", queryUrlPart);
		RSPath pathType = sdn.getPathType();
		switch (pathType) {
			case PAGE:
				return pathType.toPageLink(sdn.plane(), sdn.page()) + query;
			case ROOT:
				return pathType.toRootLink() + query;
			case PLANE:
				return pathType.toPlaneLink(sdn.planeCurrent()) + query;
			default:
				throw new WhatIsTypeException(pathType);
		}
	}

	public String toCurrentUrl(String... queryUrlPart) {
		return toCurrentUrl(this, queryUrlPart);
	}

//	public Sdn toSdnRoot() {
//		return Sdn.ofRootPlane();
//	}

	public Sdn toSdnPlane() {
		return Sdn.ofPlane(plane());
	}

	public Sdn toSdnPlaneWithPage(String page) {
		return Sdn.of(plane(), page);
	}

	public boolean isEmptyOrIndexPlane() {
		return ItemPath.isPlaneAliasIndexOrEmpty(plane());
	}

	public static boolean isEmptyPlane_WoCheckIndex() {
		return SpVM.get().ppi().isEmptySd3();
	}

	public static boolean isEmptyPagename() {
		return SpVM.get().ppi().isEmptyPagename();
	}


	public void redirectTo(boolean... blank) {
		ZKR.redirectToLocation(toLink(), ARG.isDefEqTrue(blank));
	}

	public String toLink() {
		return RSPath.toLink(this);
	}

}
