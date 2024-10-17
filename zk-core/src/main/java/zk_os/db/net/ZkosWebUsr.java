package zk_os.db.net;


import mpe.NT;
import mpc.env.Env;

public class ZkosWebUsr extends WebUsr {

	public ZkosWebUsr() {
		super.first_name = Env.getAppName().toUpperCase();
		super.net = Env.getAppName().toUpperCase();
	}

}
