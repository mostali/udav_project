package zk_os.sec;

import mpc.env.APP;
import mpe.str.CN;

public class SecApp {

	public static final String SECFORALL = "@";
	public static final String SECFORUSER = "@@";

	public static final String SECR = "secr";//run
	public static final String SECE = "sece";//edit
	public static final String SECV = "secv";//view
	public static final String USER = CN.USER;

	public static final int I_USR = 0;
	public static final int I_SECV = 1;
	public static final int I_SECE = 2;
	public static final int I_SECR = 3;

	public static final boolean IS_ALLOWED_IFEMPTY__USR = false;

	public static final int MAX_CACHE_SIZE = 100;
	public static final int CACHE_EXPIRE_AFTER_WRITE = APP.IS_DEBUG_ENABLE ? 30 : 300;

}
