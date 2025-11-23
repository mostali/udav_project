package zk_os.core;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.core.ARG;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import mpe.call_msg.core.NodeID;
import zk_os.coms.AFC;
import zk_os.db.net.WebUsr;
import zk_page.ZKR;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Sdn extends Pare<String, String> {

	public static Sdn get() {
		return ppi().sdn();
	}

	private static PagePathInfo ppi() {
		return SpVM.get().ppi();
	}

	public static Sdn getUnsafe() {
		return ppi().sdnUnsafe();
	}

	public static String PLANE() {
		return ppi().plane();
	}

	public static String PLANERQ() {
		return ppi().planeRq();
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
		return of(sd3, NodeID.PAGE_INDEX_ALIAS);
	}

	public static Sdn ofRootPlane() {
		return of(NodeID.PLANE_INDEX_ALIAS, NodeID.PAGE_INDEX_ALIAS);
	}

	public String plane() {
		return key();
	}

	public static String planeCurrent() {
		return get().key();
	}

	public String page() {
		return val();
	}

	public static String pageCurrent() {
		return get().val();
	}

	public RSPath getPathType() {
		boolean emptyPage = NodeID.isPlaneAliasIndexOrEmpty(val());
		if (!emptyPage) {
			return RSPath.PAGE;
		}
		boolean emptyPlane = NodeID.isPlaneAliasIndexOrEmpty(key());
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
		return NodeID.isPlaneAliasIndexOrEmpty(plane());
	}

	public static boolean isEmptyPlane_WoCheckIndex() {
		return ppi().isEmptySd3();
	}

	public static boolean isEmptyPagename() {
		return ppi().isEmptyPagename();
	}


	public void redirectTo(boolean... blank) {
		ZKR.redirectToLocation(toLink(), ARG.isDefEqTrue(blank));
	}

	public String toLink() {
		return RSPath.toLink(this);
	}

	public Sdnu withUser(WebUsr usr) {
		return Sdnu.of(usr.getSid(), this);
	}
}
