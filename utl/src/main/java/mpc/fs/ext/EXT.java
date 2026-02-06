package mpc.fs.ext;

import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpu.IT;
import mpe.core.P;
import mpc.url.UUrl;
import mpu.X;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.str.TKN;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum EXT {
	$$$NO_EXT$$$, $$$EMP_EXT$$$, $$$UND_EXT$$$,//
	TXT, PROPS, PROPS$$, PROPERTIES, LOG, YAML, MD, HTML, CSS, JS, JAVA, GROOVY, XML, ZUL, JSON, SH, PY, META, STATE, HEAD, CSV,//
	PDF, DOC, DOCX, ODT, ODS, XLS, XLSX, SQLITE, //
	PNG, JPG, JPEG, WEBP, BMP, GIF,//
	AVI, MP4, _3GP("3GP"), MPEG, MOV, FLV, WMV,//
	MP3, WAV,//
	ZIP, RAR, _7Z("7Z"), GZ,  //
	JAR,  //
	;

	public static final EXT[] G_IMG = {PNG, JPG, JPEG, WEBP, BMP};
	public static final EXT[] G_GIF = {GIF};
	public static final EXT[] G_VIDEO = {AVI, MP4, _3GP, MPEG, MOV, FLV, WMV};
	public static final EXT[] G_AUDIO = {MP3, WAV};
	public static final EXT[] G_ARC = {ZIP, RAR, _7Z, GZ};
	public static final EXT[] G_EDITABLE = {GROOVY, JAVA, JS, SH, PY, CSS, ZUL, HTML, PROPS, PROPERTIES, XML, JSON, YAML, LOG, TXT, MD};
	public static final EXT[] G_RUNNABLE = {GROOVY, JAVA, JS, SH, PY,};
	public static final EXT[] G_DOC = {DOC, DOCX, ODT, PDF,};

	public static final EXT[] G_BINARY = {PDF, DOC, DOCX, ODT, ODS, XLS, XLSX, SQLITE,//
			ZIP, RAR, _7Z, GZ, JPG, PNG, BMP, JPEG, WEBP, GIF, AVI, MP4, _3GP, MPEG, MOV, FLV, WMV, MP3, WAV};

	public static final String SPEC_EXT_PROPS__ = "props..";

	public static Set<String> toNamesSet(EXT[] exts) {
		return Arrays.stream(exts).map(s -> s.name().toLowerCase()).collect(Collectors.toSet());
	}

	public static String[] toNames(EXT[] exts) {
		return Arrays.stream(exts).map(s -> s.name().toLowerCase()).toArray(String[]::new);
	}

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
		return TKN.twoGreedy(filename, ".", defRq);
	}

	public static String[] two(Path comFile) {
		return twoRq(comFile.getFileName().toString());
	}

	public static String[] two(String filename) {
		String[] two = TKN.twoGreedy(filename, ".", null);
		return two != null ? two : new String[]{filename, null};
	}

	public static String getFilenameWoExt(String file, String... defRq) {
		return TKN.firstGreedy(file, '.', defRq);
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
		return TKN.last(file, '.', defRq);
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
		return of(file.getFileName().toString(), defRq);
	}

	public static EXT of(String file, EXT... defRq) {
		if (file.endsWith("." + SPEC_EXT_PROPS__)) {
			return EXT.PROPS$$;
		}
		String extFrom = getExtFromFilename(file, null);
		if (extFrom != null) {
//			if (file.toString().endsWith(".props..")) {
//				return EXT.PROPS$$;
//			}
			return ofExt(extFrom);
		}
		return ARG.toDef(defRq);
	}

	public static EXT ofExtRq(String ext, EXT... defRq) {
		EXT e = ofExt(ext);
		switch (e) {
			case $$$EMP_EXT$$$:
				return ARG.toDefThrowMsg(() -> "File without extension", defRq);
			case $$$UND_EXT$$$:
				return ARG.toDefThrowMsg(() -> X.f("File has undefined extension '%s'", ext), defRq);
			case $$$NO_EXT$$$:
				return ARG.toDefThrowMsg(() -> "File with empty extension", defRq);
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

	public boolean notHas(Path file) {
		return !has(file);
	}

	public boolean has(Path file) {
		return this == of(file, null);
	}

	public boolean has(GEXT gext) {
		return gext.has(this);
	}

	public String toFileName(String simpleName) {
		return simpleName + "." + nameAsExt();
	}

	private String nameAsExt() {
		return isNumFirstExt() ? ext.toLowerCase() : (this == PROPS$$ ? SPEC_EXT_PROPS__ : name().toLowerCase());
	}

	private boolean isNumFirstExt() {
		return name().charAt(0) == '_' && ext != null;
	}

	public Path replaceExtInFile(Path dstFile, boolean checkExistExt) {
		return Paths.get(replaceExtInFile(dstFile.toString(), checkExistExt));
	}

	public String replaceExtInFile(String dstFile, boolean checkExistExt) {
		String string = dstFile.toString();
		String nameWoExt = checkExistExt ? TKN.firstGreedy(string, ".") : TKN.firstGreedy(string, ".", string);
		return nameWoExt + "." + nameAsExt();
	}

	public List<Path> ls_filter(List<Path> ls) {
		return ls.stream().filter(this::has).collect(Collectors.toList());
	}

	public List<Path> lsAll(Path pathFc, List<Path>... defRq) {
		if (ARG.isNotDef(defRq)) {
			IT.isDirExist(pathFc);
		}
		return ls_filter(UFS.ls(pathFc));
	}
}
