package zk_os.core;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.X;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import zk_os.AFC;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Sdn extends Pare<String, String> {

	public static Sdn getRq() {
		return SpVM.get().ppi().sdnRq();
	}

//	public static Sdn get0() {
//		return SpVM.get().ppi().sdn0();
//	}

	public static String SD30() {
		return SpVM.get().ppi().subdomain30();
	}

	public static String SD3RQ() {
		return SpVM.get().ppi().subdomain3Rq();
	}

	public static boolean isEmptySd3() {
		return SpVM.get().ppi().isEmptySd3();
	}

	public static boolean isEmptyPagename() {
		return SpVM.get().ppi().isEmptyPagename();
	}

	public Sdn(String sd3, String pagename) {
		super(sd3, pagename);
	}

	public static Sdn of(String sd3, String pagename) {
		return new Sdn(sd3, pagename);
	}

	public static Sdn of(Pare pare) {
		return of(pare.keyStr(), pare.valStr());
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

	public static String plane() {
		return getRq().key();
	}

	public static String pagename() {
		return getRq().val();
	}

	public RSPath getPathType() {
		boolean emptyPage = ItemPath.isAliasIndexOrEmpty(val());
		if (!emptyPage) {
			return RSPath.PAGE;
		}
		boolean emptyPlane = ItemPath.isAliasIndexOrEmpty(key());
		return emptyPlane ? RSPath.ROOT : RSPath.PLANE;

	}

	public String toStringPath() {
		return key() + "/" + val();
	}

	public String toLocalUrl() {
		String url = isEmptySd3() ?
				X.f("http://q.com:8080/%s?ska=go", val()) :
				X.f("http://%s.q.com:8080/%s?ska=go", key(), val()
				);
		return url;
	}
}
