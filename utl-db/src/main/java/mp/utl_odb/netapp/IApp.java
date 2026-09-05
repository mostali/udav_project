package mp.utl_odb.netapp;

import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.env.Env;
import mpc.exception.FIllegalStateException;

import java.nio.file.Path;

public interface IApp {

	default String getAppName() {
		String appName = APP.getAppName(null);
		if (appName != null) {
			return appName;
		}
		String cn = getClass().getSimpleName();
		if (cn.endsWith("NetApp") && cn.length() > 6) {
			return cn.substring(0, cn.length() - 6).toUpperCase();
		} else if (cn.endsWith("App") && cn.length() > 3) {
			return cn.substring(0, cn.length() - 3).toUpperCase();
		}
		throw new FIllegalStateException("AppName '%s' must be have a correct suffix class name [ 'NetApp' or 'App' ]", cn);
	}

	@SneakyThrows
	default Path getStoreRoot() {
		return Env.RPA;
	}

}
