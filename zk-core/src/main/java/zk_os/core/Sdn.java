package zk_os.core;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.pare.Pare;
import zk_os.AFCC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Sdn extends Pare<String, String> {
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
		Path itemPathDst;
		switch (itemPath.mode) {
			case SINGLY:
				if (spVM != null) {
					itemPathDst = spVM.sdnPath(itemPath.name());
				} else {
					itemPathDst = ItemPath.rootParent().resolve(itemPath.name()).path;
				}
				break;
			case PARE:
				Path parent;
				if (spVM != null) {
					parent = Paths.get(spVM.subdomainOrIndex());
				} else {
					parent = Paths.get(AFCC.SD3_INDEX_ALIAS);
				}
				itemPathDst = parent.resolve(itemPath.page()).resolve(itemPath.name());
				break;
			case ALL:
				itemPathDst = itemPath.path;
				break;
			default:
				throw new WhatIsTypeException(itemPath.mode);
		}

		return ItemPath.of(itemPathDst);
	}

	public java.nio.file.Path path() {
		return Paths.get(X.empty(key()) ? AFCC.SD3_INDEX_ALIAS : key(),//
				X.empty(val()) ? AFCC.PAGE_INDEX_ALIAS : val());
	}

	public ItemPath sdp() {
		return ItemPath.of(path());
	}

}
