package zk_os.db.net;


import mpc.env.APP;
import mpc.env.Env;
import mpe.NT;
import mpu.X;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;

@Entity
//@Table(name = NetUserModel.TABLE)
public class AppWebUsr extends WebUsr {

	public AppWebUsr() {
		this(null, null);
	}

	public AppWebUsr(String net, String name) {
		if (X.empty(net)) {
			net = getDefaultNetName();
		}
		if (X.empty(name)) {
			name = getDefaultUserName();
		}
		super.setFirst_name(name);
		super.setNet(net);
	}

	public static @NotNull String getDefaultUserName() {
		return Env.getAppNameOrDef().toLowerCase();
	}

	public static @NotNull String getDefaultNetName() {
		return APP.getNetOfAppName(NT.DEF).nameUC();
	}
}
