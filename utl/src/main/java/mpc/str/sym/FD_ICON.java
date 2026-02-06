package mpc.str.sym;

import mpc.exception.WhatIsTypeCheckedException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpu.X;

import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

public enum FD_ICON {
	FD, FILE, DIR;
	public static final String FD_ICON = SYMJ.QUEST_RED;
	public static final String FD_FILE = SYMJ.FILE2;
	public static final String FD_DIR = SYMJ.DIR;
	public static final String FD_BASKET = SYMJ.FILE_BASKET;
	//
	public static final String FILE_PROPS = SYMJ.FILE3;
	public static final String FILE_CSS = SYMJ.FILE_IMG2;
	public static final String FILE_HTML = SYMJ.FILE_HTML;
	public static final String FILE_SCRIPT = SYMJ.FILE_IMG5_MAJONG;
//	public static final String DIR_NAME_BASKET = ".rmm";


	public String name(Path path) {
		return getEmojSymbol(path) + " " + path.getFileName().toString();
	}

	public static String toNameFileEntity(Path path) {
		return getEmojSymbol(path) + " " + path.getFileName().toString();
	}

	public static String toNameFile(Path path) {
		return getEmojSymbol(path) + " " + path.getFileName().toString();
	}

	public static String toNameDirQk(Path path) {
		return getEmojSymbolOfDir(path) + " " + getFilename_or_Root_Home_Tmp_Icon(path);
	}

	public static String getFilename_or_Root_Home_Tmp_Icon(Path path) {
		if (UFS.isDirRoot(path)) {
			return SYMJ.FILE_DB_GRAY;
		}
		String fn = UF.fn(path, "");
		switch (fn) {
			case "home":
				return SYMJ.HOME;
			case "tmp":
				return SYMJ.FILE_BASKET;
			default:
				return fn;
		}

	}

	public String toNameFd(String name) {
		return getEmojSymbol() + " " + name;
	}

	public String getEmojSymbol() {
		switch (this) {
			case FD:
				return FD_ICON;
			case FILE:
				return FD_FILE;
			case DIR:
				return FD_DIR;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public static String getEmojSymbol(Path path) {
		return getEmojSymbol(path, SYMJ.FAIL_RED_THINK, FD_ICON, FD_FILE);
	}

	public static String getEmojSymbol(Path path, String symIfNotExist, String symIfFd_NF_ND, String symIfExtUndefined) {
		Boolean isFileOrDir = null;
		try {
			isFileOrDir = UFS.isFileOrDirOrNull(path);
			if (isFileOrDir == null) {
				return symIfNotExist;
			}
		} catch (WhatIsTypeCheckedException e) {
			return symIfFd_NF_ND;
		}
		if (!isFileOrDir) {//DIR
			return getEmojSymbolOfDir(path);
		}
		EXT ext = EXT.of(path, EXT.$$$UND_EXT$$$);
		if (ext == EXT.$$$UND_EXT$$$) {
			return symIfExtUndefined;
		}
		return getEmojSymbol(ext, symIfFd_NF_ND, SYMJ.FILE4, symIfExtUndefined);
	}

	public static String getEmojSymbolOfDir(Path path) {
//		if (DIR_NAME_BASKET.equals(path.getFileName().toString())) {
//			return FD_BASKET;
//		}
		try {
			return UDIR.hasFd(path, false) ? SYMJ.DIR_NE : FD_DIR;
		} catch (Exception e) {
			if (e instanceof AccessDeniedException) {
				return SYMJ.FAIL_STOP;
			}
			return X.throwException(e);
		}
	}

	private static String getEmojSymbol(EXT ext, String symIfNoExt, String symIfEmpExt, String symIfExtUnd) {
		switch (ext) {
			case JPG:
			case PNG:
			case JPEG:
			case BMP:
			case GIF:
			case WEBP:
				return SYMJ.FILE_IMG3;
			case GROOVY:
			case JS:
			case PY:
			case SH:
				return FILE_SCRIPT;
			case CSS:
				return FILE_CSS;
			case ZUL:
			case HTML:
				return SYMJ.FILE_HTML;
			case PROPS:
			case PROPERTIES:
			case XML:
			case JSON:
			case YAML:
				return FILE_PROPS;
			case PROPS$$:
				return SYMJ.FILE_WITH_COLOR;
			case LOG:
			case TXT:
			case MD:
				return SYMJ.FILE_TXT;
			case XLS:
			case XLSX:
			case DOC:
			case DOCX:
			case PDF:
			case ODS:
			case ODT:
				return SYMJ.FILE_MANUSCRIPT;
			case WAV:
			case MP3:
				return SYMJ.FILE_MUSIC;
			case AVI:
			case FLV:
			case MOV:
			case MP4:
			case WMV:
			case _3GP:
			case MPEG:
				return SYMJ.FILE_VIDEO;
			case ZIP:
			case RAR:
			case GZ:
			case _7Z:
				return SYMJ.FILE_ARCHIVE_PACK;
			case JAVA:
				return SYMJ.JAVA_JAR_CUP;
			case JAR:
				return SYMJ.JAVA_JAR;
			case SQLITE:
				return SYMJ.FILE_DB;

			case $$$NO_EXT$$$:
				return symIfNoExt;
			case $$$EMP_EXT$$$:
				return symIfEmpExt;

			case CSV:

			case HEAD:
			case META:
			case STATE:

			default:
			case $$$UND_EXT$$$:
				return symIfExtUnd;
//				GEXT gext = GEXT.of(ext, null);
//				if (gext != null) {
//					switch (gext) {
//						case ARC:
//							return SYMJ.FILE_ARCHIVE;
//					}
//				}
//				throw new WhatIsTypeException(ext);
		}
	}
}