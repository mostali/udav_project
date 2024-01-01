package mpc.env.boot;

import mpc.Sys;
import mpc.X;
import mpc.arr.Arr;
import mpc.core.P;
import mpc.ERR;
import mpc.env.AP;
import mpc.env.PidUtils;
import mpc.time.QDate;
import mpc.str.STR;
import mpc.fs.fd.RES;
import mpc.types.opts.SeqOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

//	private static RuProps appProps;

	static {
		PidUtils.writeAppPid();
	}

	public static SeqOptions getBootArgs() {
		return ERR.NN(runArgs);
	}

	public static void boot_app(Class appBootClass) throws IOException {
		boot_app(appBootClass, new String[0]);
	}

//	public static void boot_app(Class appBootClass, String[] args) throws IOException {
//		boot_app(appBootClass, args, false, false);
//	}

	public static void boot_app(Class appBootClass, String[] args) throws IOException {

		List<String> argsList = Arr.as(args);

		String appName = AP.getAppName(null);
		if (appName == null) {
			String cn = appBootClass.getSimpleName();
			cn = cn.startsWith("App") ? cn.substring(3) : cn;
			cn = cn.endsWith("Boot") ? cn.substring(0, cn.length() - 4) : cn;
			cn = cn.isEmpty() ? appBootClass.getSimpleName() : cn;
			appName = cn;
			P.warnBig("Property AppName not found, set default:" + appName);
			//throw new FIllegalStateException("Set app name");
		}
		INFO(">>>>>>>>>>>>>>>>>>>>GO>>>>>>>>>>>>>>>>> <<< " + QDate.now() + "<<< " + appName);
		INFO("boot_app: args " + argsList);
		INFO("app_version:" + BootRunUtils.checkAndRunGetVersion(appBootClass, argsList, true));

		INFO("pwd:" + new File(".").getAbsolutePath());

		ERR.isNull(AppBoot.appBootClass);

		AppBoot.appBootClass = appBootClass;
		AppBoot.runArgs = SeqOptions.of(args);

		BootRunUtils.checkSleepBeforeStartApplication(runArgs);

		BootRunUtils.checkRunInfinityAppApplication(runArgs);

		if (false) {
			Sys.p(STR.TAB + RES.of(appBootClass, "/").ls());
		}

		BootRunUtils.checkAndRunGetVersion(appBootClass, argsList);
		BootRunUtils.checkAndRunINIT(appBootClass, argsList);

//		if (copyAP || AppBoot.runArgs.hasDouble("--copy-rsrc-to-rl-ap", false)) {
//			AP.copyToRunLocation(appBootClass);
//		}
//
//		if (copyLogback || AppBoot.runArgs.hasDouble("--copy-rsrc-to-rl-lb", false)) {
//			RES.of(appBootClass, "/" + Env.FILE_LOGBACK_XML).copyToRunLocation_(UFS_BASE.COPY.CopyOpt.FD_SKIP_IF_EXIST);
//		}

		BootRunUtils.checkCopyResourcesToRunLocation_CRTRL(runArgs, appBootClass);

//		AppBoot.appProps = RuProps.ofRunLocationOrResource(appBootClass, Env.FILE_APPLICATION_PROPERTIES, false);
//		BootToken.init(appBootClass, bootContext());

		if (argsList.contains("--exit")) {
			Sys.exit();
		}

	}

//	private static Object[] bootContext() {
//		Object[] bootContext = {runArgs, appProps};
//		return bootContext;
//	}

	public static boolean hasCmdOpt(String arg) {
		return getBootArgs().hasAnyToken(arg);
	}

	public static void runed() {

	}
}
