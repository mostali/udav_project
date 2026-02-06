package mpc.env.boot;

import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.fs.UFS;
import mpe.ftypes.core.FDate;
import mpu.SysExec;
import mpu.core.*;
import mpc.env.AppProfile;
import mpc.env.Env;
import mpe.rt.SLEEP;
import mpu.str.STR;
import mpu.str.UST;
import mpc.types.opts.SeqOptions;
import mpc.fs.fd.RES;
import mpu.Sys;
import mpu.X;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class BootRunUtils {

	//	public static final Logger L = LoggerFactory.getLogger(BootRunUtils.class);
	public static final String RA_VERSION = "version";
	public static final String RA_COPY_RSRC_TO_RL = "crtrl";

	@SneakyThrows
	public static void checkArgs(String[] args, Class formResource, boolean debug, boolean res, boolean sleep, boolean infinity) {

		if (ARR.contains(args, "--sleep")) {
			SLEEP.ms(5000, "wait debug");
		}
		SeqOptions seqOpts = SeqOptions.of(args);

		if (seqOpts.getSingle("exita", null) != null) {
			Long sleepa = seqOpts.getSingleAs("exita", Long.class, null);
			if (sleepa > 0) {
				SLEEP.ms(sleepa);
			}
			Sys.exit("exita:" + sleepa + ":ok");
		}
		if (debug) {
			checkDebugOptions(args);
		}
		if (res) {
			checkCopyResourcesToRunLocation_CRTRL(seqOpts, formResource);
		}

		if (sleep) {
			checkSleepBeforeStartApplication(seqOpts);
		}

		if (infinity) {
			checkRunInfinityAppApplication(seqOpts);
		}

	}

	public static void checkCopyResourcesToRunLocation_CRTRL(SeqOptions seqOptions, Class fromResource) throws IOException {
		Sys.p("Check SINGLE options -> COPY RESOURCES -> '-" + RA_COPY_RSRC_TO_RL + "'");
		List<String> resources = seqOptions.getSingleAll(RA_COPY_RSRC_TO_RL, Collections.EMPTY_LIST);
		if (X.empty(resources)) {
			return;
		}
		RES.copyToRunLocation_(fromResource, resources, UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
		Sys.exit("ok");
	}

	public static void checkSleepBeforeStartApplication(SeqOptions seqOptions) {
		Sys.p("Check SINGLE options -> SLEEP BEFORE START -> '-sleepbs'...");
		String sleepms = seqOptions.getSingle("sleepbs", null);
		if (sleepms != null) {
			Sys.p("sleepbs ACTIVE, sleep:" + sleepms);
			SLEEP.ms(UST.LONG(sleepms));
		}
	}

	public static void checkRunInfinityAppApplication(SeqOptions seqOptions) {
		Sys.p("Check SINGLE options -> RUN INFINITY APP -> '-runia'...");
		String sleepms = seqOptions.getSingle("-runia", null);
		if (sleepms == null) {
			return;
		}
		boolean run = true;
		while (run) {
			Sys.p(UUID.randomUUID());
			SLEEP.ms(UST.LONG(sleepms));
		}
	}

	public static void checkDebugOptions(String[] args) {
		Sys.p("Check DBL options -> DEBUG MODE (sleep 20 sec for connecting) -> '--debug'...");
		if (Arrays.asList(args).contains("--debug")) {
			Sys.p("Debug active, wait 20sec to connect...");
			SLEEP.ms(20000);
			Sys.p("init...");
		}
	}

	@SneakyThrows
	public static String checkAndRunINIT(Class res, List<String> args, boolean... returnStringOrExit) {
		Sys.p("Check INIT DEPLOYMENT R-SCRIPT '--init'");
		if (args.contains("--init")) {
			SLEEP.sec(3);
			RES.copyToRunLocation_(res, Arrays.asList("/r.sh"), UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
			RES.copyToRunLocation_(res, Arrays.asList("/" + Env.FILE_APPLICATION_PROPERTIES), UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
			RES.copyToRunLocation_(res, Arrays.asList("/" + AppProfile.prod.toFilenameWithProfile()), UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
//			for (AppProfile appProfile : AppProfile.values()) {
//				try {
//					String s = "/" + appProfile.toFilenameWithProfile();
//					boolean exist = RES.of(res, s, EFT.FILE).exist();
//					if (exist) {
//						RES.copyToRunLocation_(res, Arrays.asList(s), UFS_BASE.COPY.CopyOpt.FD_SKIP_IF_EXIST);
//					} else {
//						L.trace("not found:" + s);
//					}
//				} catch (Exception ex) {
//					L.error("Err write from rsrc", ex);
//				}
//			}
			RES.copyToRunLocation_(res, Arrays.asList("/" + Env.FILE_LOGBACK_XML), UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
			STR.replaceInFile(Paths.get("r.sh"), "APP_NAME=DEF", "APP_NAME=" + Env.getAppName());
			SysExec.exec_SafeSpace("chmod", "+x", "r.sh");
			Sys.exit("ok --init");
		}
		return null;
	}

	@SneakyThrows
	public static String checkAndRunRocky(Class res, List<String> args, boolean... returnStringOrExit) {
		Sys.p("Check INIT DEPLOYMENT R-SCRIPT '--rockyapp'");
		if (args.contains("--rockyapp")) {
			RES.copyToRunLocation_(res, Arrays.asList("/Rocky.Dockerfile"), UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
			Sys.exit("ok --rockyapp");
		}
		return null;
	}

	public static String checkAndRunGetVersion(Class res, List<String> args, boolean... returnStringOrExit) {
		if (args.contains("--v") || args.contains("-" + RA_VERSION)) {
			String v = APP.getVersion(res, null);
			if (ARG.isDefEqTrue(returnStringOrExit)) {
				return v == null ? "noversion" : v;
			}
			Sys.p(v);
			if (ARG.isDefNotEqTrue(returnStringOrExit)) {
				System.exit(0);
			}
		}
		return null;
	}

	public static String getVersionFromAny(Class rsrc, String... defRq) {
		Exception error = null;
		String v = null;
		try {
			v = getVersionFromResources(rsrc);
		} catch (Exception ex) {
			error = ex;
		}
		if (v == null || error != null) {
			try {
				v = getVersionFromBuildInfo();
				error = null;
			} catch (Exception ex) {
				error = ex;
			}
		}
		if (v != null) {
			return v;
		}
		return ARG.toDefThrow(error, defRq);
	}

	public static String getVersionFromResources(Class rsrc, String... defRq) {
		try {
			String v = RES.of(rsrc == null ? RES.class : rsrc, "/version.bn").cat();
			return v;
		} catch (Exception ex) {
			if (AppBoot.L.isWarnEnabled()) {
				AppBoot.L.warn("getVersionFromBuildInfo ( If JunitTest, That is OK - nojar or def class rsrc '{}'? )", ex.getMessage(), rsrc);
			}
			return ARG.toDefThrow(ex, defRq);
		}
	}

	private static String APP_VERSION = null;

	public static String getVersionFromBuildInfo(String... defRq) {
		if (APP_VERSION != null) {
			return APP_VERSION;
		}
		try {
			Properties props = RES.readProperties(RES.class, "/META-INF/build-info.properties");
			String property = props.getProperty("build.time");
			QDate qDate = QDate.of(property, FDate.APP_FORMATS, null);
			APP_VERSION = Env.getAppNameOrDef() + "." + (qDate == null ? property : MDate.formatDateToString(qDate)+"."+qDate.f(QDate.F.HH__mm));
			return APP_VERSION;
		} catch (Exception ex) {
			if (AppBoot.L.isWarnEnabled()) {
				AppBoot.L.warn("getVersionFromBuildInfo ( If JunitTest, That is OK - nojar)", ex.getMessage());
			}
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static void checkArgsAll(String[] args) {
		checkArgs(args, BootRunUtils.class, true, true, true, true);
	}

	@SneakyThrows
	public static boolean checkAllowedR_SH() {
		String s = RW.readString(Paths.get("r.sh"), null);
		if (s == null) {
			return false;
		}
		return s.contains(" --R)");
	}
}
