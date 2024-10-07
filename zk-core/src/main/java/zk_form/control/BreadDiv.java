package zk_form.control;

import lombok.Getter;
import lombok.NonNull;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import zk_com.base_ctr.Div0;
import zk_os.db.net.WebUsr;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

public class BreadDiv extends Div0 {
	static Double[] RIGTH_TOP = ARR.of(1.0, 88.0);

	public static Double[] getAdptiveTopLeftBreadCrumbs(int level) {

		//TOP LEFT
		switch (level) {

			//ROOT
			case 0:
				return ARR.of(5.0, 5.0);
			case 1:
				return ARR.of(5.0, 6.0);
			case 2:
				return RIGTH_TOP;

			//PLANE
			case 10:
				return ARR.of(8.0, 6.0);
			case 11:
				return ARR.of(8.0, 7.7);
			case 12:
				return RIGTH_TOP;

			//PAGES
			case 100:
				return ARR.of(8.0, 12.9);
			case 101:
				return ARR.of(8.0, 13.8);
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
		appendChild(new BreadLb(SYMJ.STARMANY, level = 0));
	}


	public BreadDiv(String planeName, boolean... withPlaneLabel) {
		super();
		appendChild(new BreadMasterLn(level = 10, SYMJ.FLOWER).decoration_none());
		appendChild(new BreadLb(planeName, 11, withPlaneLabel));

	}

	public BreadDiv(Pare sdn) {
		super();
		breadMasterLn = (BreadLn) new BreadMasterLn(level = 100, SYMJ.ARROW_UP_THINK, true).decoration_none();
		appendChild(breadMasterLn);

		if (sdn != null) {
			breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), 101);
			appendChild(breadLb);
		}
	}

	@Override
	protected void init() {
		super.init();
		BreadLb lb = new BreadLb(ROLE.toIcon(), level + 2);
		if (Sec.isNotAnonim()) {
			WebUsr user = Sec.getUser();
			String title = (X.empty(user.getUserName()) ? "" : user.getUserName() + ":") + user.getLogin() + ":" + user.getRolesList();
			lb.title(title);
		}
		appendChild(lb);
	}

	@Getter
	@NonNull
	private BreadLn breadMasterLn, breadLn;
	private BreadLb breadLb;
//
//	public Component currentLbCom() {
//		return breadLn == null ? breadLb : breadLn;
//	}

	boolean withPlaneLabel = false;

	public BreadDiv withPlaneLabel() {
		this.withPlaneLabel = withPlaneLabel;
		return this;
	}
}
