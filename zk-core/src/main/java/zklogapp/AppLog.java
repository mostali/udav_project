package zklogapp;

import mpc.fs.UFS;
import mpu.core.ARR;
import mpu.str.STR;

import java.nio.file.Path;

public class AppLog {

//	public static final String APK_LOG_CACHE_SEC_NAME = "log.cache.sec";
//	public static final String APK_MAX_LOG_SIZE_MB_NAME = "log.max_size.mb";
//	public static final int APK_MAX_LOG_SIZE_MB = 22;
//	public static int APK_LOG_CACHE_SEC = 300;

	public static boolean isAllowedFile(Path path) {
		if (UFS.isDir(path)) {
			return false;
		}
		String fn = path.getFileName().toString();
		if (STR.endsWith(fn, ".log",true)) {
			return true;
		}
		return ARR.contains(fn, ".log.",true);
	}
}
