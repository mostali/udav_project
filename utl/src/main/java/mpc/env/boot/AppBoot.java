package mpc.env.boot;

import mpc.env.APP;
import mpc.env.Env;
import mpc.env.PidUtils;
import mpc.fs.UFS;
import mpc.map.BootContext;
import mpc.types.opts.SeqOptions;
import mpe.core.P;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class AppBoot {

	public static void main(String[] args) throws Exception {
		boot_app(AppBoot.class, args);
	}

	public static final Logger L = LoggerFactory.getLogger(AppBoot.class);

	public static void INFO(String msg, Object... args) {
		if (L.isInfoEnabled()) {
			Sys.p(X.fl(msg, args));
			L.info(msg, args);
		}
	}


	private static Class appBootClass;
	private static SeqOptions runArgs;

	static {
		PidUtils.writeAppPid();
	}

	public static SeqOptions getBootArgs() {
		return IT.NN(runArgs);
	}

	public static void boot_app(Class appBootClass) throws IOException {
		boot_app(appBootClass, new String[0]);
	}

	public static void boot_app(Class appBootClass, String[] args, Map<String, String>... propsMap) throws IOException {

		INFO(">>>>>>>>>>>>>>>>>>>>RPA>>>>>>>>>>>>>>>>> <<< " + Env.RPA);

		BootRunUtils.checkArgsAll(args);

		BootContext.init(args, propsMap);

		List<String> argsList = ARR.as(args);

		String appName = APP.getAppName(null);
		if (appName == null) {
			String cn = appBootClass.getSimpleName();
			cn = cn.startsWith("App") ? cn.substring(3) : cn;
			cn = cn.endsWith("Boot") ? cn.substring(0, cn.length() - 4) : cn;
			cn = cn.isEmpty() ? appBootClass.getSimpleName() : cn;
			appName = cn;
			P.warnBig("Property AppName not found, set default:" + appName);
			//throw new FIllegalStateException("Set app name");
		}
		INFO(">>>>>>>>>>>>>>>>>>>>GO>>>>>>>>>>>>>>>>> <<< " + QDate.now() + "<<< " + appName + " >>>" + APP.getVersion());
		INFO("boot_app: args " + argsList);
		INFO("app_version:" + BootRunUtils.checkAndRunGetVersion(appBootClass, argsList, true));

		INFO("pwd:" + new File(".").getAbsolutePath());

		IT.isNull(AppBoot.appBootClass);

		AppBoot.appBootClass = appBootClass;
		AppBoot.runArgs = SeqOptions.of(args);

		BootRunUtils.checkSleepBeforeStartApplication(runArgs);

		BootRunUtils.checkRunInfinityAppApplication(runArgs);

		BootRunUtils.checkAndRunGetVersion(appBootClass, argsList);

		BootRunUtils.checkAndRunINIT(appBootClass, argsList);

		BootRunUtils.checkAndRunRocky(appBootClass, argsList);

		BootRunUtils.checkCopyResourcesToRunLocation_CRTRL(runArgs, appBootClass);

		if (argsList.contains("--exit")) {
			Sys.exit();
		}

		if (false && !UFS.existDir(Paths.get("tmp"))) {
			UFS.MKDIR.createDirs(Paths.get("tmp"));
		}

//		if (copyAP || AppBoot.runArgs.hasDouble("--copy-rsrc-to-rl-ap", false)) {
//			AP.copyToRunLocation(appBootClass);
//		}
//
//		if (copyLogback || AppBoot.runArgs.hasDouble("--copy-rsrc-to-rl-lb", false)) {
//			RES.of(appBootClass, "/" + Env.FILE_LOGBACK_XML).copyToRunLocation_(UFS_BASE.COPY.CopyOpt.FD_SKIP_IF_EXIST);
//		}

//		AppBoot.appProps = RuProps.ofRunLocationOrResource(appBootClass, Env.FILE_APPLICATION_PROPERTIES, false);
//		BootToken.init(appBootClass, bootContext());

	}

	public static boolean hasCmdOpt(String arg) {
		return getBootArgs().hasAnyToken(arg);
	}

	public static void runed() {

	}

	public static void bootLog(String msg, Object... args) {
		mpc.log.L.L.info(msg, args);
		L.info(msg, args);
	}
}
