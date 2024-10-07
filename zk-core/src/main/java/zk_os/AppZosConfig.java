package zk_os;

import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARG;
import mpe.core.U;
import mpu.IT;
import mpc.env.AP;
import mpc.env.AVI;
import mpu.pare.Pare;
import mpu.str.SPLIT;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AppZosConfig {

	public static final String APK_BEA_TRM_ENABLE = "bea.trm.enable";
	public static final String APK_ZK_LOG_ENABLE = "zk.log.enable";
	public static final String APK_IS_DEBUG = "app.debug.enable";
	public static final String APK_SUPER_KEY = "super.key.auth";
	public static final String APK_SPACE_PATH = "space.path";

//	public static Pare<String, Long> SUPER_KEY = null;

//	public static String getSuperKey() {
////		if (SUPER_KEY.val() > System.currentTimeMillis()) {
////			return SUPER_KEY.getKey();
////		}
////		SUPER_KEY = null;
//		return SUPER_KEY;
//	}

//	public static String setSuperKey(String value) {
//		SUPER_KEY = Pare.of(value, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20));
//		return null;
//	}

	static {
		AP.AutoInit.initClass(AppZosConfig.class);
	}

	@AVI(prop = APK_BEA_TRM_ENABLE, type = Boolean.class, def = "false")
	public static Boolean TRM_ENABLE = null;//AP.getAs(APK_BEA_TRM_ENABLE, Boolean.class, false);
	@AVI(prop = APK_ZK_LOG_ENABLE, type = Boolean.class, def = "false")
	public static Boolean ZK_LOG_ENABLE = null;//AP.getAs(APK_ZK_LOG_ENABLE, Boolean.class, false);
	@AVI(prop = APK_SUPER_KEY, type = String.class, def = U.__NULL__)
	public static String SUPER_KEY = null;//AP.getAs(APK_SUPER_KEY, String.class, null);
	@AVI(prop = APK_IS_DEBUG, type = Boolean.class, def = "false")
	public static Boolean IS_DEBUG = null;

	@AVI(prop = APK_SPACE_PATH, type = String.class, def = "{{RPA}}/.gx")
	public static String SPACE_PATH = null;

	private static Pare<String, List<Path>> _SPACE_PATHS = null;

	public static List<Path> SPACE_PATH(boolean... fresh) {
		if (_SPACE_PATHS == null || ARG.isDefEqTrue(fresh) || _SPACE_PATHS.key().length() != SPACE_PATH.length()) {
			List<String> strings = SPLIT.allBy(SPACE_PATH, ":::");
			_SPACE_PATHS = Pare.of(IT.NE(SPACE_PATH), strings.stream().map(Paths::get).collect(Collectors.toList()));
		}
		return _SPACE_PATHS.val();
	}

	@SneakyThrows
	public static void initProperty(String property, String value) {
		AP.AutoInit.setValue(AppZosConfig.class, property, value);
//		switch (property) {
//			case AppZosConfig.APK_BEA_TRM_ENABLE:
//				AppZosConfig.TRM_ENABLE = UST.strTo(value, Boolean.class);
//				break;
//			case AppZosConfig.APK_ZK_LOG_ENABLE:
//				AppZosConfig.ZK_LOG_ENABLE = UST.strTo(value, Boolean.class);
//				break;
//			default:
//				throw new WhatIsTypeException(property);
//
//		}
	}

	public static String toStringLog() {
		return X.f("Trm(%s), ZLog(%s), SK(%s)", TRM_ENABLE, ZK_LOG_ENABLE, SUPER_KEY);
	}

	public static int getCookieAuthTimeout() {
		return AP.getAs("web.session.timeout.bycookie.sec", Integer.class, (int) TimeUnit.DAYS.toSeconds(30));
	}
}
