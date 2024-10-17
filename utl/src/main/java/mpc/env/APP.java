package mpc.env;

import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;
import mpu.IT;
import mpu.core.ARR;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class APP {

	public static final String APP_NAME = "app.name";
	public static final String APP_DOMAIN = "app.host";
	public static final String APP_DEBUG_ENABLE = "app.debug";
	public static final String APP_PROM_ENABLE = "app.prom";
//
//	public static boolean isPromMode() {
//		return AP.getAs(APP_PROM_MODE, Boolean.class, false);
//	}

	//
	//
	public static final String GD_KEY_PATH = "gd.key.path";
	public static final String GD_SHEET_ID = "gd.sheet.id";
	public static final String GD_DB_PATH = "gd.db.path";
	public static final String GD_DB_MAPPING = "gd.db.mapping";
	public static final String GD_PROM = "gd.prom";

	//
	//
	public static final String APK_TG_BT_ENABLE = "tg.bt.enable";
	public static final String APK_TG_BT_ID = "tg.bt.id";
	public static final String APK_TG_BT_TK = "tg.bt.tk";
	public static final String APK_TG_BT_OWNER_ID = "tg.bt.owner.id";
	public static final String APK_TG_BT_WAIT = "tg.bt.wait";

	//VK
	public static final String APK_VK_BT_ENABLE = "vk.bt.enable";
	public static final String APK_VK_BT_ID = "vk.bt.gt.id";
	public static final String APK_VK_BT_TK = "vk.bt.gt.tk";
	public static final String APK_VK_BT_OWNER_ID = "vk.bt.owner.id";
	public static final String APK_VK_BT_WAIT = "vk.bt.wait";

	public static final String APK_TSM_SERVER_URL = "tsm.server.url";
	public static final String APK_TSM_SERVER_NAME = "tsm.server.name";
	public static final String APK_TSM_SERVER_APP_CODE = "tsm.server.app_code";


	public static Boolean IS_DEBUG_ENABLE;

	static {
		IS_DEBUG_ENABLE = isDebugEnable();
	}

	public static Boolean IS_PROM_ENABLE;

	static {
		IS_PROM_ENABLE = isPromEnable();
	}


	public static String getAppDomainName(String... defRq) {
		return AP.get(null, APP_DOMAIN, defRq);
	}

	public static boolean isDebugEnable() {
		return isPromEnable() ? false : "true".equals(AP.get(APP_DEBUG_ENABLE, null));
	}

	public static boolean isPromEnable() {
		return "true".equals(AP.get(APP_PROM_ENABLE, null));
	}

	public static String getAppName(String... defRq) {
		return AP.get(null, APP_NAME, defRq);
	}

	@NotNull
	public static Path getAppDataDir() {
		String appName = Env.getAppNameOrDef();
		return Env.getAppDataDir(appName);
	}

	public static Path getPathLOG() {
		return Paths.get(Env.FILE_LOGBACK_XML);
	}

	public static Path getPathAP(int... mode) {
		if (mode.length == 0) {
			return Paths.get(Env.FILE_APPLICATION_PROPERTIES);
		}
		switch (mode[0]) {
			case 0:
				Path resolve = getAppDataDir().resolve(Env.FILE_APPLICATION_PROPERTIES);
				if (UFS.existFile(resolve)) {
					return getAppDataDir();
				}
				return null;
			case 1:
				//			map = getApplicationProperties_RSRC(appProfile, null);
				AppProfile appProfile = AppProfile.getFirstUseful();
				if (appProfile == null) {
					return null;
				}
				return Paths.get(appProfile.toFilenameWithProfile());
			default:
				throw new FIllegalStateException("illegal mode:" + ARR.asInt(mode));
		}
	}

	public static Path getPathGdKey() {
		return IT.isFileExist(Env.RPA.resolve("__GD").resolve("gd.key.json"));
	}

	public static Path getPathGtrDb() {
//		return IT.isFileExist(Env.RPA.resolve("data/db$projects.sqlite"));
		return IT.isFileExist(Env.HOME_LOCATION.resolve(".data/gsv/data/db$projects.sqlite"));
	}

	public static Integer getPort_ReturnIf80(Integer... defRq) {
		String portStr = AP.get("server.port", null);
		if (portStr == null) {
			return null;
		}
		portStr = AP.getValueWoDef(portStr);
		return UST.INT(portStr, defRq);
	}
}
