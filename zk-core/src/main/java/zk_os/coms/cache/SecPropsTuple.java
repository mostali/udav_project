package zk_os.coms.cache;

import mpc.str.sym.SYMJ;
import mpu.pare.Tuple;
import mpu.str.JOIN;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.UO;

public class SecPropsTuple extends Tuple {

	public static final String ICON_RUN = SYMJ.ARROW_REPEAT_TRIANGLE_GREEN;
	//		public static final String ICON_EDIT = SYMJ.EDIT;
	public static final String ICON_EDIT = SYMJ.GRID_CORNER;
	public static final String ICON_VIEW = SYMJ.EYE;
	public static final String ICON_USER = SYMJ.USER;

	public static SecPropsTuple ofEmptyProps() {
		return new SecPropsTuple(new Object[]{null, null, null, null});
	}

	@Override
	public String toString() {
		String usr = ICON_USER + SYMJ.toStringOrFail(usrName(null));
		String view = ICON_VIEW + SYMJ.toStringOrFail(secv(null));
		String edit = ICON_EDIT + SYMJ.toStringOrFail(sece(null));
		String run = ICON_RUN + SYMJ.toStringOrFail(secr(null));
		return JOIN.argsBySpace(usr, view, edit, run);
	}

	public static SecPropsTuple of(Tuple tuple) {
		return new SecPropsTuple(tuple.obs);
	}

	public SecPropsTuple(Object[] objects) {
		super(objects);
	}

	public String usrName(String... defRq) {
		return getAsString(SecApp.I_USR, defRq);
	}

	public String secv(String... defRq) {
		return getAsString(SecApp.I_SECV, defRq);
	}

	public String sece(String... defRq) {
		return getAsString(SecApp.I_SECE, defRq);
	}

	public String secr(String... defRq) {
		return getAsString(SecApp.I_SECR, defRq);
	}

	public SecBool toNewSecBool(WebUsr usr) {
		return toNewSecBool(usr, this);
	}

	private static SecBool toNewSecBool(WebUsr usr, SecPropsTuple secProps) {
		SecBool secBool = new SecBool();

		for (UO oper : UO.values()) {
			secBool.set(oper.index(), SecBool.isAllowed(usr, secProps, oper));
		}
//			secBool.set(SecApp.I_USR, SecBool.isAllowed(usr, secProps, UO.USR));
//			secBool.set(SecApp.I_SECV, SecBool.isAllowed(usr, secProps, SecApp.I_SECV));
//			secBool.set(SecApp.I_SECE, SecBool.isAllowed(usr, secProps, SecApp.I_SECE));
//			secBool.set(SecApp.I_SECR, SecBool.isAllowed(usr, secProps, SecApp.I_SECR));
		return secBool;
	}
}
