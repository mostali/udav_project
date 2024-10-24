//package zk_os;
//
//import mpc.exception.NotifyMessageRtException;
//import mpc.exception.WhatIsTypeCheckedException;
//import mpc.fs.UFS;
//
//import java.nio.file.Path;
//
//public class UCZK {
//	public static Path isDir(Path dir) {
//		Boolean fileOrDirOrNull = null;
//		try {
//			fileOrDirOrNull = UFS.isFileOrDirOrNull(dir);
//			if (fileOrDirOrNull != null && !fileOrDirOrNull) {
//				return dir;
//			}
//			throw NotifyMessageRtException.LEVEL.RED.I("Except DIR '%s'", dir);
//		} catch (WhatIsTypeCheckedException e) {
//			throw NotifyMessageRtException.LEVEL.RED.I("Except DIR '%s'", dir);
//		}
//	}
//
//	public static Path isFile(Path file) {
//		Boolean fileOrDirOrNull = null;
//		try {
//			fileOrDirOrNull = UFS.isFileOrDirOrNull(file);
//			if (fileOrDirOrNull != null && fileOrDirOrNull) {
//				return file;
//			}
//			throw NotifyMessageRtException.LEVEL.RED.I("Except FILE '%s'", file);
//		} catch (WhatIsTypeCheckedException e) {
//			throw NotifyMessageRtException.LEVEL.RED.I("Except FILE '%s'", file);
//		}
//	}
//}
