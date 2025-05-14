package mpc.env;

import mpc.arr.STREAM;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpe.app.AppCore0;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.STR;
import mpu.str.UST;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class APP {

	public static final String APP_NAME = "app.name";
	public static final String APP_HOST = "app.host";
	public static final String APK_APP_ORG = "app.org";
	public static final String APK_APP_USER = "app.user";


	public static final String APK_PROM_ENABLE = "app.prom.enable";
	public static final String APK_DEV_ENABLE = "app.dev.enable";
	public static final String APK_IS_DEBUG = "app.debug.enable";

	public static final String APK_SUPER_KEY = "super.key.auth";

	//
	//
	//

	public static final String GD_KEY_PATH = "gd.key.path";
	public static final String GD_SHEET_ID = "gd.sheet.id";
	public static final String GD_SHEET_RANGE = "gd.sheet.range";
	public static final String GD_RETURN_TYPE = "gd.return.type";
	public static final String GD_DB_PATH = "gd.db.path";
	public static final String GD_DB_URL = "gd.db.url";
	public static final String GD_DB_USERNAME = "gd.db.username";
	public static final String GD_DB_PASSWORD = "gd.db.password";
	public static final String GD_DB_TABLE = "gd.db.table";
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
	public static final String[] ULP_DEV_LOCAL = {"jdbc:postgresql://localhost:5432/tsm", "tsm", "9b8bf958167dce67ec739604599295ce15a3ab0d793bbc414144ed931b5"};

	static {
		AutoInitClassProperty.initClass(APP.class);
	}

	@AutoInitValue(prop = APK_IS_DEBUG)
	public static Boolean IS_DEBUG_ENABLE = false;

//	static {
//		IS_DEBUG_ENABLE = AP.getAs(APK_IS_DEBUG, Boolean.class, false);
//	}

	@AutoInitValue(prop = APK_PROM_ENABLE)
	public static Boolean IS_PROM_ENABLE;

//	static {
//		IS_PROM_ENABLE = AP.getAs(APK_PROM_ENABLE, Boolean.class, false);
//	}

	@AutoInitValue(prop = APK_DEV_ENABLE)
	public static Boolean IS_DEV_ENABLE;

//	static {
//		IS_DEV_ENABLE = AP.getAs(APK_IS_DEBUG, Boolean.class, false);
//	}

//	public static boolean isDebugEnable() {
//		return AP.getAs(APK_IS_DEBUG, Boolean.class, false);
//	}

//	public static boolean isPromEnable() {
//		return AP.getAs(APK_PROM_ENABLE, Boolean.class, false);
//	}


	public static Path TREE_GNC() {
//		return Env.RPA.resolve("gnc.sqlite");
		return AppCore0.of().path("env").resolve("gnc.sqlite");
	}

	@Deprecated
	public static Path TREE_GND_V1() {
		return AppCore0.of().path("tasks").resolve("gnd.sqlite");
	}

	@Deprecated
	public static Path TREE_GNDD_V1() {
//		return Env.RPA.resolve("gndd.sqlite");
		return AppCore0.of().path("tasks").resolve("gndd.sqlite");
	}

	public static Path TREE_TASKS() {
//		return Env.RPA.resolve("gndd.sqlite");
		return AppCore0.of().path("tasks").resolve("gndd.sqlite");
	}
	//
	//
	//

	public static String getAppHost(String... defRq) {
		return AP.get(null, APP_HOST, defRq);
	}

	public static String getAppName(String... defRq) {
		String appName = System.getProperty(APP_NAME);
		if (appName != null) {
			return appName;
		}
		return AP.get(null, APP_NAME, defRq);
	}

	//
	//

	public static class LOCATION {

		public static Path getAppDataDirOrDef() {
			return getAppDataDir(false);
		}

		public static Path getAppDataDir(boolean requiredOrDefAppname) {
			if (requiredOrDefAppname) {
				return getAppDataDir(Env.getAppName());
			} else {
				return getAppDataDir(Env.getAppNameOrDef());
			}
		}

		public static Path getAppDataDirOrNull() {
			String appName0 = getAppName(null);
			if (appName0 != null) {
				return getAppDataDir(appName0);
			}
			return null;
		}

		public static Path getAppDataDir(String dirNameFromData) {
			return Env.getDefaultDataDir().resolve(dirNameFromData);
		}

	}

	//
	//

	public static Path getPathLOG() {
		return Paths.get(Env.FILE_LOGBACK_XML);
	}

	public static Path getPathAP(int... mode) {
		if (mode.length == 0) {
			return Paths.get(Env.FILE_APPLICATION_PROPERTIES);
		}
		switch (mode[0]) {
			case 0:
				Path resolve = LOCATION.getAppDataDirOrDef().resolve(Env.FILE_APPLICATION_PROPERTIES);
				if (UFS.existFile(resolve)) {
					return LOCATION.getAppDataDirOrDef();
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
				throw new FIllegalStateException("illegal mode:" + ARR.ofInt(mode));
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

	public static String getTgBotId(String... defRq) {
		return AP.get(APK_TG_BT_ID, defRq);
	}

	public static String getTgBotOwnerId(String... defRq) {
		return AP.get(APK_TG_BT_OWNER_ID, defRq);
	}

	public static Integer getVkBotId(Integer... defRq) {
		Integer as = AP.getAs(APK_VK_BT_ID, Integer.class, null);
		if (as != null) {
			return -(Math.abs(as));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("App Prop '%s' required", APK_TG_BT_ID), defRq);
	}

	public static Boolean isEnableBotTg() {
		return AP.getAs(APK_TG_BT_ENABLE, Boolean.class, false);
	}

	public static Boolean isEnableBotVkLPS() {
		return AP.getAs(APK_VK_BT_ENABLE, Boolean.class, false);
	}

	public static String getAppOrg(String... defRq) {
		return AP.get(APK_APP_ORG, defRq);
	}

	public static String getAppMode(String... defRq) {
		return AP.get("app.mode", defRq);
	}


	public static List<String> relativizeAppFile(Collection<Path> paths, String... removePfxPath) {
		return STREAM.mapFilterToList(paths, p -> APP.relativizeAppFile(p, removePfxPath));
	}

	public static String relativizeAppFile(Path path, String... removePfxPath) {
		String relPath = Env.RPA.relativize(path).toString();
		return ARG.isDef(removePfxPath) ? STR.removeStartsWith(relPath, removePfxPath[0]) : relPath;
	}

}
