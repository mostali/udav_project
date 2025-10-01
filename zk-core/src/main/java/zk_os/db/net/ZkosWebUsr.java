package zk_os.db.net;


import mpc.env.Env;

public class ZkosWebUsr extends WebUsr {

	public ZkosWebUsr() {
		super.setFirst_name(Env.getAppName().toUpperCase());
		super.setNet(Env.getAppName().toUpperCase());
	}

}
