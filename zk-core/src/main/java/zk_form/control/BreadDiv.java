package zk_form.control;

import lombok.Getter;
import lombok.NonNull;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.core.ARR;
import mpu.pare.Pare;
import zk_com.base_ctr.Div0;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_page.index.RSPath;

public class BreadDiv extends Div0 {
	static Double[] RIGTH_TOP = ARR.of(1.0, 88.0);

	public static Double[] getAdptiveTopLeftBreadCrumbs(int level) {

		//TOP LEFT
		switch (level) {

			//ROOT
			case 0:
				return ARR.of(7.7, 5.0);
			case 1:
				return ARR.of(7.7, 6.0);
			case 2:
				return RIGTH_TOP;

			//PLANE
			case 10:
				return ARR.of(9.0, 6.0);
			case 11:
				return ARR.of(9.0, 7.7);
			case 12:
				return RIGTH_TOP;

			//PAGES
			case 100:
				return ARR.of(9.0, 12.9);
			case 101:
				return ARR.of(9.0, 13.8);
			case 102:
				return RIGTH_TOP;

			default:
				throw new WhatIsTypeException(level);

//				return ARR.of(8.0, 12.0);

		}
	}

	final int level;


	public BreadDiv() {
		super();
		appendChild(new BreadLb(APP.getIcon(), level = 0));
	}


	public BreadDiv(String planeName, boolean... withPlaneLabel) {
		super();
		appendChild(new BreadMasterLn(level = 10, RSPath.ROOT.icon()).decoration_none());
		appendChild(new BreadLb(planeName, 11, withPlaneLabel));

	}

	public BreadDiv(Pare sdn) {
		super();
		breadMasterLn = (BreadLn) new BreadMasterLn(level = 100, SYMJ.ARROW_UP_THINK, true).decoration_none();

		if (Sdn.of(sdn).getPathType() != RSPath.ROOT) {
			appendChild(breadMasterLn);
		}

		if (sdn != null) {
			breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), 101);
			appendChild(breadLb);
		}
	}

	@Override
	protected void init() {
		super.init();
//		BreadLb lb = new BreadLb(ROLE.toIcon(), level + 2);
//		if (Sec.isNotAnonim()) {
//			lb.title(Sec.getUser().toInfo());
//		}
//		appendChild(lb);
	}

	@Getter
	@NonNull
	private BreadLn breadMasterLn, breadLn;
	private BreadLb breadLb;

	boolean withPlaneLabel = false;

	public BreadDiv withPlaneLabel() {
		this.withPlaneLabel = withPlaneLabel;
		return this;
	}
}
