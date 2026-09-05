package zk_com.base_ext;

import org.zkoss.zk.ui.Page;
import org.zkoss.zul.*;
import zk_com.base_ctr.Div0;
import zk_page.ZKS;

public class SimpleBorderLayout extends Borderlayout {

	Div0 _north;

	public North getNorth(boolean create) {
		if (create) {
			NORTH();
		}
		return super.getNorth();
	}

	public Div0 NORTH() {
		if (_north == null) {
			if (getNorth() == null) {
				appendChild(new North());
			}
			getNorth().appendChild(_north = new Div0());
		}
		return _north;
	}

	Div0 _east;

	public East getEast(boolean create) {
		if (create) {
			EAST();
		}
		return super.getEast();
	}

	public Div0 EAST() {
		if (_east == null) {
			if (getEast() == null) {
				appendChild(new East());
			}
			getEast().appendChild(_east = new Div0());
		}
		return _east;
	}

	Div0 _west;

	public West getWest(boolean create) {
		if (create) {
			WEST();
		}
		return super.getWest();
	}

	public Div0 WEST() {
		if (_west == null) {
			if (getWest() == null) {
				appendChild(new West());
			}
			getWest().appendChild(_west = new Div0());
		}
		return _west;
	}

	Div0 _center;

	public Center getCenter(boolean create) {
		if (create) {
			CENTER();
		}
		return super.getCenter();
	}

	public Div0 CENTER() {
		if (_center == null) {
			if (getCenter() == null) {
				appendChild(new Center());
			}
			getCenter().appendChild(_center = new Div0());
		}
		return _center;
	}

	Div0 _south;

	public South getSouth(boolean create) {
		if (create) {
			SOUTH();
		}
		return super.getSouth();
	}

	public Div0 SOUTH() {
		if (_south == null) {
			if (getSouth() == null) {
				appendChild(new South());
			}
			getSouth().appendChild(_south = new Div0());
		}
		return _south;
	}

	public static SimpleBorderLayout buildCom(boolean height100) {
		SimpleBorderLayout b = new SimpleBorderLayout();
//		if (height100) {
//			b.defaultDimsHeight100();
//		}
		return b;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public void init() {

	}

	public SimpleBorderLayout defaultDimsHeight100() {
		return ZKS.HEIGHT_ADAPTIVE_0(this);
	}

}
