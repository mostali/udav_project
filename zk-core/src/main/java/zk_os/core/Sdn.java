package zk_os.core;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.X;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import zk_os.AFC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Sdn extends Pare<String, String> {

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

	public static Sdn get() {
		return SpVM.get().ppi().sdnRq();
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
		return AFC.PAGES.getRpaPageDir(sdn);
	}

	public Path path() {
		return Paths.get(X.empty(key()) ? ItemPath.SD3_INDEX_ALIAS : key(),//
				X.empty(val()) ? ItemPath.PAGE_INDEX_ALIAS : val());
	}

	public ItemPath itemPath() {
		return ItemPath.of(path());
	}

	public String toStringPath() {
		return key() + "/" + val();
	}

}
