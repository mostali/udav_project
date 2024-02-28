package mpc.fs.ext;

import mpu.IT;
import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UUrl;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.str.USToken;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Predicate;

public enum EXT {
	$$$NO_EXT$$$, $$$EMP_EXT$$$, $$$UND_EXT$$$, TXT, LOG, PROPERTIES, YAML, MD, PDF, DOC, DOCX, ODT, ODS, XLS, XLSX, SQLITE, ZIP, RAR, _7Z("7Z"), GZ, PROPS, JPG, PNG, BMP, JPEG, WEBP, GIF, AVI, MP4, _3GP("3GP"), MPEG, MOV, FLV, WMV, MP3, HTML, CSS, JS, JAVA, GROOVY, XML, ZUL, JSON, SH, PY, META, STATE, HEAD, CSV,//
	;

	public static void main(String[] args) {
		testTwo();
		String[] two = two(".");
		P.exit(two);
	}

	public static void testTwo() {
		IT.state(Arrays.equals(two("0"), new String[]{"0", null}), "0");
		IT.state(Arrays.equals(two("0."), new String[]{"0", ""}), "0.");
		IT.state(Arrays.equals(two("0.1"), new String[]{"0", "1"}), "0.1");
		IT.state(Arrays.equals(two(".1"), new String[]{"", "1"}), ".1");
		IT.state(Arrays.equals(two("."), new String[]{"", ""}), ".");
		P.exit();
	}


	EXT() {
		ext = null;
	}

	final String ext;

	EXT(String ext) {
		this.ext = ext;
	}


	public static String twoJoin(String filename, String ext) {
		return ext == null ? filename : filename + "." + ext;
	}

	public static String[] twoRq(Path comFile, String[]... defRq) {
		return twoRq(comFile.getFileName().toString(), defRq);
	}

	public static String[] twoRq(String filename, String[]... defRq) {
		return USToken.twoGreedy(filename, ".", defRq);
	}

	public static String[] two(Path comFile) {
		return twoRq(comFile.getFileName().toString());
	}

	public static String[] two(String filename) {
		String[] two = USToken.twoGreedy(filename, ".", null);
		return two != null ? two : new String[]{filename, null};
	}

	public static String getFilenameWoExt(String file, String... defRq) {
		return USToken.firstGreedy(file, '.', defRq);
	}

	public static String getExtFromFile(Path file, String... defRq) {
		return EXT.getExtFromFilename(file.getFileName().toString(), defRq);
	}

	public static String getExtFromUrlPath(String url, String... defRq) {
		return UUrl.getExtFromUrlPath(url, defRq);
	}

	public static String getExtFromUrlPath_First(String url, String... defRq) {
		return UUrl.getExtFromUrlPath_First(url, defRq);
	}

	public static String getExtFromFilename(String file, String... defRq) {
		return USToken.last(file, '.', defRq);
	}

	public static String fn(Path file, String... defRq) {
		return getFilenameWoExt(file.getFileName().toString(), defRq);
	}

	public static String fn(String file, String... defRq) {
		return getFilenameWoExt(file, defRq);
	}

	public static String fn(String file, boolean isFile, String... defRq) {
		return getFilenameWoExt(isFile ? Paths.get(file).getFileName().toString() : file, defRq);
	}

	public static String ext(Path file, String... defRq) {
		return getExtFromFilename(file.getFileName().toString(), defRq);
	}

	public static String ext(String file, String... defRq) {
		return getExtFromFilename(file, defRq);
	}

	public static String ext(String file, boolean isFile, String... defRq) {
		return getExtFromFilename(isFile ? Paths.get(file).getFileName().toString() : file, defRq);
	}

	public static EXT of(Path file) {
		return ofExt(getExtFromFile(file, null));
	}

	public static EXT of(Path file, EXT... defRq) {
		String extFrom = EXT.getExtFromFile(file, null);
		if (extFrom != null) {
			return ofExt(extFrom);
		}
		return ARG.toDef(defRq);
	}

	public static EXT of(String file, EXT... defRq) {
		String extFrom = getExtFromFilename(file, null);
		if (extFrom != null) {
			return ofExt(extFrom);
		}
		return ARG.toDef(defRq);
	}

	public static EXT ofExtRq(String ext, EXT... defRq) {
		EXT e = ofExt(ext);
		switch (e) {
			case $$$EMP_EXT$$$:
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new RequiredRuntimeException("File without extension");
			case $$$UND_EXT$$$:
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new RequiredRuntimeException("File has undefined extension '%s'", ext);
			case $$$NO_EXT$$$:
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new RequiredRuntimeException("Empty extension");
			default:
				return e;
		}
	}

	public static EXT ofExt(String ext) {
		if (ext == null) {
			return $$$NO_EXT$$$;
		} else if (ext.isEmpty()) {
			return $$$EMP_EXT$$$;
		}
		for (EXT t : values()) {
			String extName = t.name();
			if (extName.equalsIgnoreCase(ext)) {
				return t;
			} else if (extName.charAt(0) == '_' && t.ext != null && t.ext.equalsIgnoreCase(ext)) {
				return t;
			}
		}
		return $$$UND_EXT$$$;
	}

	public static Predicate<Path> buildPredicate(boolean ignoreCase, String... exts) {
		return path -> {
			String name = path.getFileName().toString();
			int ind = name.lastIndexOf('.');
			if (ind == -1) {
				return false;
			}
			String ext = name.substring(ind);
			if (ext == null) {
				return false;
			}
			for (String en : exts) {
				if ((en.length() + 1 == ext.length()) && STR.endsWith(ext, en, ignoreCase)) {
					return true;
				}
			}
			return false;
		};
	}

	public static Predicate<Path> buildPredicate(boolean ignoreCase, Enum... ext) {
		return path -> {
			EXT extPath = EXT.of(path, null);
			if (extPath == null) {
				return false;
			}
			for (Enum en : ext) {
				if (ENUM.eq(extPath, en, ignoreCase)) {
					return true;
				}
			}
			return false;
		};
	}


	public boolean has(String name) {
		return this == of(name, null);
	}

	public boolean has(Path file) {
		return this == of(file, null);
	}

	public boolean hasIn(GEXT gext) {
		return gext.has(this);
	}

	public String toFileName(String simpleName) {
		return simpleName + "." + nameAsExt();
	}

	private String nameAsExt() {
		return isSpecExt() ? ext.toLowerCase() : name().toLowerCase();
	}

	private boolean isSpecExt() {
		return name().charAt(0) == '_' && ext != null;
	}

}
