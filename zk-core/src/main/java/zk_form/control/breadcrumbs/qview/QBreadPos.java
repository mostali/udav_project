package zk_form.control.breadcrumbs.qview;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;

public enum QBreadPos {
	UNDEFINED, //
	ROOT0, ROOT1, ROOT2, //
	PLANE10, PLANE11, PLANE12, //
	PAGE100, PAGE101, PAGE102,
	; //

	static Double[] RIGTH_TOP = ARR.of(1.0, 88.0);

	public static Double[] getAdptiveTopLeftBreadCrumbs(QBreadPos level) {
		switch (level) {

			//ROOT
			case ROOT0:
				return ARR.of(7.7, 5.0);
			case ROOT1:
				return ARR.of(7.7, 6.0);
			case ROOT2:
				return RIGTH_TOP;

			//PLANE
			case PLANE10:
				return ARR.of(9.0, 6.0);
			case PLANE11:
				return ARR.of(9.0, 7.7);
			case PLANE12:
				return RIGTH_TOP;

			//PAGES
			case PAGE100:
				return ARR.of(9.0, 12.6);
			case PAGE101:
				return ARR.of(9.0, 13.8);
			case PAGE102:
				return RIGTH_TOP;

			default:
				throw new WhatIsTypeException(level);

		}


	}
}
