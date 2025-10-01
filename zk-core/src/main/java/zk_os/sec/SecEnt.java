package zk_os.sec;

import mpu.X;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;

public class SecEnt {

	public static boolean isPlaneOwner() {
		return isPlaneOwner(Sdn.SD30());
	}

	public static boolean isPlaneOwner(String plane) {
		if (Sec.isAnonim()) {
			return false;
		}
		WebUsr user = Sec.getUser();
		return X.equals(plane, user.getNetNidNamed(null));
	}
}
