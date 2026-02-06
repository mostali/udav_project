package mpc.env;


import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.*;
import mpu.core.QDate;
import mpc.env.boot.AppBoot;
import mpc.log.L;
import mpc.str.sym.SYM;
import mpu.str.STR;
import mpu.str.UST;
import mpc.fs.fd.Fd;
import mpc.fs.fd.UFD;
import mpu.X;
import mpu.core.RW;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Env {

	public static final String FILE_APPLICATION_PROPERTIES = "application.properties";
	public static final String FILE_APPLICATION_PROPERTIES_PROD = "application-prod.properties";
	public static final String FILE_LOGBACK_XML = "logback.xml";
	public static final String PD_RS = "/tmp/rs/";

	public static final Path TMP = Paths.get("/tmp/");
	public static final String DEF = "def";
	public static final Path ROOT_PJM = Paths.get("/home/dav/pjm");

	public static final String APK_RPA = "rpa";

	public static final Path APPVOL_DIR = Paths.get("/opt/appVol");
	public static final Path APPVOL_BIN_DIR = Paths.get("/opt/appVol/.bin");

	public static String getUserName() {
		return System.getProperty("user.name");
	}

	public static boolean isRunFromDevMp() {
		if ("target".equals(RUN_LOCATION.getFileName().toString())) {
			return false;
		}
//		if (!"mp".equals(START_LOCATION.getFileName().toString())) {
//			return false;
//		}
		return UFD.CHILDS.isChildOfParent(ROOT_PJM, RUN_LOCATION);
	}

	public static class Org {

		public static final String DIR_TLP = ".env.tlp/org/";

		public static Path usrOrgDir() throws IOException {
			return UFD.CHILDS.childOfParent(null, "org/", Env.PD_ENV_TLP);
		}

		public static String[] usrOrg(String usr) throws IOException {
			return usrLP(null, "org/" + usr);
		}

		public static String[] usrLP(String parentFolder, String usrRelPath) throws IOException {
			Path folder_ = UFD.CHILDS.childOfParent(parentFolder, usrRelPath, Env.PD_ENV_TLP);
			String l = RW.readLine(folder_.resolve("l"), 0).trim();
			String p = RW.readLine(folder_.resolve("p"), 0).trim();
			return new String[]{IT.notEmpty(l), IT.notEmpty(p)};
		}

		public static @NotNull Path usrOrgDir(String usrName) {
			return HOME_LOCATION.resolve(DIR_TLP + usrName);
		}
	}

	public static boolean isTodoDateEnd() {
		return QDate.now().isAfter(QDate.of(2025, 11, 12));
	}

	public static Path getUserHome(String... childs) {
		return Paths.get(getUserHome(), childs);
	}

	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	public static Path getNativeBinLibsPath(String binFile, boolean... checkExist) {
		Path resolve = getDefaultDataDir().resolve(".bin").resolve(binFile);
		return ARG.isDefEqTrue(checkExist) ? IT.isFileExist(resolve) : resolve;
	}

	public static Path getAppBinLibsPath(String binFile, boolean... checkExist) {
		Path resolve = getDefaultDataDir().resolve("bin").resolve(binFile);
		return ARG.isDefEqTrue(checkExist) ? IT.isFileExist(resolve) : resolve;
	}

	public static boolean isLocalDevMashine() {
		return Files.exists(ROOT_PJM);
	}

	public static class EDIR {
		public static FileVar FILEVAR_TLP = FileVar.of(Env.PD_ENV_TLP);
	}

	public static class FileVar extends Fd {

		public FileVar(String file) {
			super(file);
		}

		public FileVar(Path path) {
			super(path);
		}

		public static FileVar of(String file) {
			return new FileVar(file);
		}

		public static FileVar of(Path path) {
			return new FileVar(path);
		}

		public String readStrRq(String fromFile) {
			try {
				return STR.removeEndStringQk(RW.readContent_(fromFile == null ? toPath() : toPath().resolve(fromFile)), SYM.NEWLINE, false);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		public int readIntRq(String fromFile) {
			try {
				return Integer.parseInt(readStrRq(fromFile));
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		public long readLongRq(String fromFile) {
			try {
				return Long.parseLong(readStrRq(fromFile));
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		public boolean readBoolRq(String fromFile) {
			try {
				return Boolean.parseBoolean(readStrRq(fromFile));
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		public double readDoubleRq(String fromFile) {
			try {
				return Double.parseDouble(readStrRq(fromFile));
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static final Path HOME_LOCATION = getUserHomePath();
	public static final Path RUN_LOCATION = Paths.get(".");
	public static final Path RUN_LOCATION_PARENT = RUN_LOCATION.resolve("..");

	public static final Path RL_PJM = getRunDirPathOfPjm();

	//	public static final Path RL_PJM_OR_PARENT = PD_RUN_LOCATION_PJM == null ? PD_RUN_LOCATION_PARENT : PD_RUN_LOCATION_PJM;
//	public static final Path RL_PPJM_OR_PRL = RL_PJM == null ? RUN_LOCATION_PARENT : UPath.getParentOfPathWithParent(RL_PJM);

	private static final Boolean useVolApp = true;

	public static Path getDefaultDataDir() {
//		return useVolApp ? DIR_VOL_APP : RL_PPJM_OR_PRL_DATA_DEFAULT;
		return DIR_VOL_APP;
	}

	public static Path getDefaultAppDataDir() {
		return getDefaultDataDir().resolve(Env.getAppNameOrDef());
	}

	private static final Path DIR_VOL_APP = Paths.get("/opt/appVol");

//	private static final Path RL_PPJM_OR_PRL_DATA_DEFAULT = RL_PPJM_OR_PRL.resolve(".data");

	public static final Path PD_ENV_TLP = HOME_LOCATION.resolve(".env.tlp");

	public static final Path PF_USER_BASHRC = HOME_LOCATION.resolve(".bashrc");

	/**
	 * *************************************************************
	 * ---------------------------- APP DOMAIN --------------------------
	 * *************************************************************
	 */

//	public static final AppPropDef<String> APR_HOST = new AppPropDef("app.host", Env.APR_HOST.getValue());

	/**
	 * *************************************************************
	 * ---------------------------- APP NAME --------------------------
	 * *************************************************************
	 */
	public static String appName = null;

//	public static String getAppName() {
//		return appName != null ? appName : X.throwException(new RequiredRuntimeException("Set appname"));
//	}

	public static String getAppName(String... defRq) {
		return appName != null ? appName : ARG.toDefThrow(() -> new RequiredRuntimeException("Set appname"), defRq);
	}

	public static String getAppNameOrDef() {
		return X.empty(appName) ? Env.DEF.toLowerCase() : appName;
	}

	public static void setAppName(String appName, boolean... initCall) {
		Env.appName = IT.NE(appName);
		if (ARG.isDefNotEqTrue(initCall)) {
			initRPA();
		}
		AppBoot.INFO("Init AppName:" + appName);

	}

	static {
		try {
			setAppName(APP.loadAppNameFromProps(), true);
		} catch (Exception ex) {
//			L.error("ERROR SET APP NAME ERROR:", ex);
			L.error("ERROR SET APP NAME ERROR:{}", ex.getMessage());
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- RPA --------------------------
	 * *************************************************************
	 */
	public static final Path RPA_DEFAULT = useVolApp ? DIR_VOL_APP : RUN_LOCATION_PARENT.resolve(".data");
	public static Path RPA = RPA_DEFAULT;
	public static Path RPA_TMP = RPA_DEFAULT.resolve("tmp");
	public static Path TMP_RPA = RPA_TMP;

	public static Path TMP_LOCAL = Paths.get("tmp");

	static {
		initRPA();
	}

	public static void initRPA() {
		String rpaSysProp = APP.CORE.getAppRpa(null);
		Path rpa;
		if (rpaSysProp != null) {
			rpa = Paths.get(rpaSysProp);
		} else {
			String appName = APP.getAppName(null);
//			String appName = Env.getAppName(null);
			if (X.notEmpty(appName)) {
				rpa = RPA_DEFAULT.resolve(appName);
			} else {
				rpa = null;
			}
		}
		if (rpa != null) {
			Env.setRPA(rpa, true);
		}
	}


	public static void setRPA(Path rpa, boolean... mkdirsOrSingleDirOrCheck) {
		Boolean isMdirsOrSingleDirOrCheck = ARG.toDefBooleanOrNull(mkdirsOrSingleDirOrCheck);
		AppBoot.INFO("Try Init RPA file://" + rpa + ", checkExist:" + isMdirsOrSingleDirOrCheck);
		if (UFS.existDir(rpa)) {
			RPA = rpa;
			TMP_RPA = rpa.resolve("tmp");
			return;
		}
		if (isMdirsOrSingleDirOrCheck == null) {
			AppBoot.INFO("RPA '{}' not exists", rpa);
			return;
		}
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(rpa, isMdirsOrSingleDirOrCheck);
		RPA = rpa;
		TMP_RPA = rpa.resolve("tmp");
		UFS.MKDIR.mkdirIfNotExist(TMP_RPA);
		AppBoot.INFO("Init RPA file://" + RPA);
	}

	/**
	 * *************************************************************
	 * ----------------------------  --------------------------
	 * *************************************************************
	 */
	private static Path getUserHomePath() {
		return Paths.get(System.getProperty("user.home")).toAbsolutePath();
	}

	public static Path getRunDirPathOfPjm(String child) {
		return getRunDirPathOfPjm().resolve(child);
	}

	private static Path getRunDirPathOfPjm() {
		try {
			return getRunDirPathOfPjmImpl();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return null;
		}
	}

	private static Path getRunDirPathOfPjmImpl() {
		Path runLocationAbsPath = RUN_LOCATION.toAbsolutePath();
		boolean isPjmLocation = isPjmDir(runLocationAbsPath);
		if (isPjmLocation) {
			return RUN_LOCATION;
		}
		Path parent = runLocationAbsPath;
		Path parentRltv = RUN_LOCATION;
		do {
			parent = parent.getParent();
			if (parent == null) {
				return null;
			}
			if (!Files.isDirectory(parent)) {
				throw new IllegalStateException("Not found pjm");
			}
			//			if (L.isInfoEnabled()) {
			//				L.info("Check pjm location:{}", parent);
			//			}
			if (isPjmDir(parent)) {
				return parentRltv;
			}
			parentRltv = Paths.get("..").resolve(parentRltv);
		} while (true);
	}

	private static boolean isPjmDir(Path folder) {
		if (folder == null || folder.getFileName() == null) {
			return false;
		}
		String dirname = folder.getFileName().toString();
		switch (dirname) {
			case "pjm":
			case "genv":
				break;
			default:
				return false;
		}
		Path mp = folder.resolve("mp");
		if (!Files.isDirectory(mp)) {
			return false;
		}
		Path mpPom = mp.resolve("pom.xml");
		if (!Files.isRegularFile(mpPom)) {
			return false;
		}
		if (!Files.isDirectory(folder.resolve(".git"))) {
			return false;
		}
		return true;
	}

	public static String getTlpVal(String key, String... defRq) {
		String val = null;
		Exception ex = null;
		try {
			val = Env.EDIR.FILEVAR_TLP.readStrRq(key);
		} catch (Exception e) {
			ex = e;
		}
		if (val != null) {
			return val;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		if (ex == null) {
			throw new RequiredRuntimeException(key);
		} else {
			throw new RequiredRuntimeException(ex, key);
		}
	}

	public static <T> T getTlpValType(String key, Class<T> type, T... defRq) {
		String val = getTlpVal(key, null);
		return UST.strTo(val, type, defRq);
	}

}
