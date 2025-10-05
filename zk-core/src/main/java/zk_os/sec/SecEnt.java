package zk_os.sec;

import mpu.X;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;

public class SecEnt {

	public static boolean isPlaneOwner() {
		return isPlaneOwner(Sdn.PLANE());
	}

	public static boolean isPlaneOwner(String plane) {
		return !Sec.isAnonimUnsafe() && isPlaneOwner(Sec.getUser(), plane);
	}

	public static boolean isPlaneOwner(WebUsr user, String plane) {
		return X.equalsSafe(plane, user.getNetNidNamed(null));
	}
}
