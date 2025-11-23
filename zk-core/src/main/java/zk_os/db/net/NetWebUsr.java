package zk_os.db.net;

import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;
import zk_os.sec.Sec;

import javax.persistence.Entity;

@Entity
//@Table(name = NetUserModel.TABLE)
public class NetWebUsr extends WebUsr {

	public static NetWebUsr get(NetWebUsr... defRq) {
		WebUsr user = Sec.getUser(defRq);
		if (user != null && NetWebUsr.class.isAssignableFrom(user.getClass())) {
			return (NetWebUsr) user;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException(), defRq);
	}

	public NetWebUsr() {
	}
}
