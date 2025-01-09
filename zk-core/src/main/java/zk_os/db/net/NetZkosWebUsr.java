package zk_os.db.net;

import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import zk_os.sec.Sec;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "usr")
public class NetZkosWebUsr extends WebUsr {

	public static NetZkosWebUsr get(NetZkosWebUsr... defRq) {
		WebUsr user = Sec.getUser(defRq);
		if (user != null && NetZkosWebUsr.class.isAssignableFrom(user.getClass())) {
			return (NetZkosWebUsr) user;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException(), defRq);
	}

	public NetZkosWebUsr() {
	}
}
