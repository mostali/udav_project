package mpc.env;

import mpc.arr.STREAM;
import mpc.env.boot.BootRunUtils;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.map.BootContext;
import mpc.url.QueryArg;
import mpc.fs.UFS;
import mpc.url.UUrl;
import mpc.net.CON;
import mpe.NT;
import mpe.app.AppCore0;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.STR;
import mpu.str.UST;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class APP {

	public static final String DEFAULT_APP_LOCAL_HOST = "http://localhost:8080";

	public static final String APK_APP_NAME = "app.name";
	public static final String APK_APP_HOST = "app.host";
	public static final String APK_APP_ORG = "app.org";
	public static final String APK_APP_USER = "app.user";


	public static final String APK_USE_HTTPS = "app.https";

	public static final String APK_PROM_ENABLE = "app.prom.enable";
	public static final String APK_DEV_ENABLE = "app.dev.enable";
	public static final String APK_IS_DEBUG = "app.debug.enable";

	public static final String APK_SUPER_KEY = "super.key.auth";
	public static final String APK_SUPER_HEADER = "super.header.auth";

	public static final String APK_SIMPLE_AUTH_ENABLE = "SIMPLE_AUTH_ENABLE";

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
	public static final String APK_SUPER_KEY_DEGAULT = "go";


	static {
		AutoInitClassProperty.initClass(APP.class);
	}

//	public static final AppPropDef0<Integer> APP_TOP_MAXSLEEPSEC = new AppPropDef0("app.top.maxSleepSec", (int) TimeUnit.DAYS.toSeconds(1)).desc("maxSleepSec for updating fetch posts");

	@AutoInitValue(prop = APK_IS_DEBUG)
	public static Boolean IS_DEBUG_ENABLE = false;

	@AutoInitValue(prop = APK_PROM_ENABLE)
	public static Boolean IS_PROM_ENABLE;

	@AutoInitValue(prop = APK_DEV_ENABLE)
	public static Boolean IS_DEV_ENABLE;

	@AutoInitValue(prop = APK_USE_HTTPS, def = "false")
	public static Boolean USE_HTTPS = false;

	@AutoInitValue(prop = APK_APP_NAME)
	public static String APP_NAME;

	@AutoInitValue(prop = APK_APP_HOST, bash_call = "hostname")
	public static String APP_HOST;

	@AutoInitValue(prop = APK_SIMPLE_AUTH_ENABLE)
	public static Boolean SIMPLE_AUTH_ENABLE;

//	@AutoInitValue(prop = Env.APK_RPA)
//	public static String RPA;


	//global name context
	public static Path TREE_GNC() {
//		return Env.RPA.resolve("gnc.sqlite");
		return AppCore0.of().path("env").resolve("gnc.sqlite");
	}

	//global user log
	public static Path TREE_GUL() {
		return AppCore0.of().path("env").resolve("gul.sqlite");
	}

	@Deprecated //global daemon's
	public static Path TREE_GND_TASKS_V1() {
		return AppCore0.of().path("tasks").resolve("gnd.sqlite");
	}

	@Deprecated //global daemons
	public static Path TREE_GNDD_V1() {
		return AppCore0.of().path("tasks").resolve("gndd.sqlite");
	}

	public static Path TREE_TASKS() {
		return AppCore0.of().path("tasks").resolve("gndd.sqlite");
	}

	public static String getNetName() {
		return getNetOfAppName(NT.DEF).name();
	}

	public static NT getNetOfAppName(NT... defRq) {
//		Env.getAppName().toUpperCase()
		return NT.of(Env.getAppNameOrDef(), defRq);
	}

	public static String getVersion() {
		return getVersion(APP.class, null);
	}

	public static String getVersion(Class fromRsrc, String... defRq) {
		return BootRunUtils.getVersionFromAny(fromRsrc, defRq);
	}

//	public static String getSystemProp(String key) {
//		return System.getProperty(key);
//	}
//
//	public static String getSystemEnvProp(String key) {
//		return System.getenv(key);
//	}

//	public static String getAppProp(String key) {
//		return System.getenv(key);
//	}

	public static String getAppName(String... defRq) {
//		String appName = getSystemProp(APK_APP_NAME);
//		return appName != null ? appName : AP.get(null, APK_APP_NAME, defRq);
		return APP_NAME != null ? APP_NAME : loadAppNameFromProps(defRq);
	}

	public static String loadAppNameFromProps(String... defRq) {
		String appName = getPropFrom_Sys_Env_AP(APK_APP_NAME, null);
		return appName != null ? appName : ARG.toDefThrowMsg(() -> X.f("Except app.name (from sys|env|application.properties)"), defRq);
	}

	public static String getPropFrom_Sys_Env_AP(String key, String... defRq) {
		String appName = Sys.getSysProp(key, null);
		if (X.empty(appName)) {
			appName = Sys.getEnvProp(key, null);
			if (X.empty(appName)) {
				appName = AP.get(key, null);
			}
		}
		return appName != null ? appName : ARG.toDefThrowMsg(() -> X.f("Except app.name (from sys|env|application.properties)"), defRq);
	}

	public enum PDDM {
		PROM, DEBUG, DEV;

		public static PDDM get() {
			return IS_PROM_ENABLE ? PROM : (IS_DEBUG_ENABLE ? DEBUG : (IS_DEV_ENABLE ? DEV : PROM));
		}
	}

	//TODO - one view for peroperty
	public static class CORE {

		//
		//
		//

		//
		//
		//

		public static Integer getPort_ReturnIf80(Integer... defRq) {
//			String dPortStr = Sys.getSysProp("server.port", null);
			String portStr = BootContext.get().get("server.port", null);
//			String portStr = dPortStr != null ? dPortStr : AP.get("server.port", null);
			if (portStr == null) {
				return ARG.toDefThrowMsg(() -> X.f("Property 'server.port' not found"), defRq);
			}
			portStr = AP.getValueWoDef(portStr);
			return UST.INT(portStr, defRq);
		}

		//		public static String getAppRpa(String... defRq) {
//			String appName = Sys.getSysProp(Env.APK_RPA, null);
//			return appName != null ? appName : AP.get(null, Env.APK_RPA, defRq);
//		}
		public static String getAppRpa(String... defRq) {
			String appName = Sys.getSysProp(Env.APK_RPA, null);
			return appName != null ? appName : AP.get(null, Env.APK_RPA, defRq);
		}

	}


	//
	//

	public static class HOST {

		public static String getAppHost0(String... defRq) {
//			String appHost = getSystemProp(APK_APP_HOST);
//			return appHost != null ? appHost : AP.get(null, APK_APP_HOST, defRq);
			return APP_HOST;
		}

		public static String getAppHostWithPath(String path, QueryArg... args) {
			return QueryArg.joinToUrl(UUrl.normUrl(getAppHostWithProtocol(), path), args);
		}

		public static String getAppHostWithPath(String path) {
			return UUrl.normUrl(getAppHostWithProtocol(), path);
		}

		public static String getAppHostWithProtocol() {
			return getUsedHttpProtocol() + getAppHost0();
		}

		public static String getUsedHttpProtocol() {
			return USE_HTTPS ? CON.HTTPS : CON.HTTP;
		}

		public static String getAppHostWithPlane(String plane, Pare... args) {
			String url2host = getUsedHttpProtocol() + IT.NE(plane, "set plane") + "." + getAppHost0();
			return QueryArg.joinToUrl(url2host, args);
		}


//		public static String getAppUrlWithPath(String path) {
//			return getAppUrlWithPlaneAndPath(null, path);
//		}

		public static String getAppUrlWithPlaneAndPath(String plane, String path) {
			return getUsedHttpProtocol() + (X.notEmpty(plane) ? plane + "." : "") + UUrl.normUrl(getAppHost0(), path);
		}

		public static String getAppUrlWithPath(String path) {
			return getUsedHttpProtocol() + UUrl.joinUrlPaths(getAppHost0(), path);
		}

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

	public static Path getPathLogbackXml() {
		return Paths.get(Env.FILE_LOGBACK_XML);
	}

	public static Path getPathServerLog() {
		return Paths.get("logs/server.log");
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

	@Deprecated
	public static Path getPathGtrDb() {
//		return IT.isFileExist(Env.RPA.resolve("data/db$projects.sqlite"));
//		return IT.isFileExist(Env.HOME_LOCATION.resolve(".data/gsv/data/db$projects.sqlite"));
		return IT.isFileExist(Env.APPVOL_DIR.resolve("gsv/data/db$projects.sqlite"));
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
