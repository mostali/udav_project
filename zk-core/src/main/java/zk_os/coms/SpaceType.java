package zk_os.coms;

import mpc.exception.WhatIsTypeException;
import mpe.cmsg.ns.INodeID;
import mpe.cmsg.ns.IPageID;
import mpe.cmsg.ns.ISpaceID;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.UO;
import mpe.img.EColor;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;

public enum SpaceType {
	SPACES, PAGES, NODES;

	public static SpaceType of(ISpaceID objId) {
		if (objId instanceof INodeID) {
			return NODES;
		} else if (objId instanceof IPageID) {
			return PAGES;
		} else if (objId instanceof ISpaceID) {
			return SPACES;
		}
		throw new WhatIsTypeException(objId.getClass());
	}

	public String bgColorNext() {
		return color().nextColor();
	}

	public EColor color() {
		switch (this) {
			case SPACES:
				return EColor.LBLUE;
			case PAGES:
				return EColor.GREEN;
			case NODES:
				return EColor.YELLOW;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public Set<Path> ls(Sdn sdn, UO... policy) {
		return ls(sdn, null, policy);
	}

	public Set<Path> ls(Sdn sdn, Predicate<Path> pathFilter, UO... policy) {
		return AFCSec.getItemPaths(WebUsr.get(), this, sdn, policy, pathFilter);
	}

	public Set<Path> lsView(Sdn sdn, Predicate<Path>... pathFilter) {
		return AFCSec.getItemPaths(WebUsr.get(), this, sdn, UO.as(UO.VIEW), pathFilter);
	}

	public String toChar() {
		switch (this) {
			case SPACES:
				return "@";
			case PAGES:
				return "#";
			case NODES:
				return "*";
			default:
				throw new WhatIsTypeException(this);
		}
	}

//	public String nameUC() {
//		return name().toLowerCase();
//	}
}
