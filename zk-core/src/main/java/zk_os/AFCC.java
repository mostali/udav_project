package zk_os;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpe.NT;
import mpu.core.ARG;
import mpu.core.ARR;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

//App File Core Com's
public class AFCC {
	//	public static final String DIR_PAGES_FC = ".pages.fc";
	public static final String DIR_COMS = ".coms";
	public static final String DIR_FORMS = ".forms";
	public static final String DIR_PLANES = ".planes";
	public static final String DIR_PAGES = ".pages";
	public static final String DIR_PAGE = ".page";
	public static final String DIR_PLANE = ".plane";
	public static final String DIR_HEADS = ".heads";
	public static final String PAGE_INDEX_ALIAS = ".index";
	public static final String SD3_INDEX_ALIAS = ".index";

	public static boolean isNotTechName(Path path) {
		return !isReservedFileName(path);
	}

//	public static final Predicate<String> TEST_TECH_FILENAME = ANCF::isReservedFileName;
//	public static final Predicate<Path> TEST_TECH_FILE = ANCF::isReservedFileName;
//	public static final Predicate<Path> TEST_TECH_FILE_or_SD3_VK_TG = ANCF::isReservedFileName_OrVkTg;

	public static boolean isReservedFileName(Path path) {
		return isReservedFileName(path.getFileName().toString());
	}

	public static boolean isReservedFileName(String fn) {
		return SD3_INDEX_ALIAS.equalsIgnoreCase(fn) ||//
				DIR_COMS.equalsIgnoreCase(fn) ||//
				DIR_FORMS.equalsIgnoreCase(fn) || //
				DIR_PAGE.equalsIgnoreCase(fn) ||//
				DIR_PLANE.equalsIgnoreCase(fn) ||//
				fn.startsWith(NT.TPFX) || fn.startsWith(NT.VPFX)//
				;
	}

	public static boolean isReservedFileName_OrVkTg(Path path) {
		return isReservedFileName_OrVkTg(path.getFileName().toString());
	}

	public static boolean isReservedFileName_OrVkTg(String fn) {
		return isReservedFileName(fn) || isTgSd3(fn) || isVkSd3(fn);
	}

	public static boolean isVkSd3(String sd3) {
		return sd3.startsWith(NT.VPFX) || sd3.startsWith("V");
	}

	public static boolean isTgSd3(String sd3) {
		return sd3.startsWith(NT.TPFX) || sd3.startsWith("T");
	}


	public static boolean isPageTechname(String name) {
		return DIR_FORMS.equals(name) || DIR_COMS.equals(name);
	}

	public static boolean isNotPageTechname(Path file) {
		return !isPageTechname(file.getFileName().toString());
	}

	public static boolean isNotPageDir(Path path) {
		return !isPageDir(path);
	}

	public static boolean isPageDir(Path path) {
		return ARR.as(DIR_PAGE, DIR_COMS, DIR_FORMS).stream().anyMatch(fn -> UFS.exist(path.resolve(fn)));
	}

	public static List<Path> filterNoPageDir(List<Path> paths) {
		return paths.stream().filter(AFCC::isNotPageDir).collect(Collectors.toList());
	}

	public enum FileType {
		XML, HEAD_BEFORE, HEAD_AFTER;

		public static FileType of(String filename, FileType... defRq) {
			if (isHtmlNote(filename)) {
				return XML;
			} else if (filename.endsWith("$$HEAD")) {
				return HEAD_AFTER;
			} else if (filename.endsWith("$$head")) {
				return HEAD_BEFORE;
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except Afcc File Type from '%s'", filename), defRq);
		}
	}

	public static boolean isHtmlNote(String noteName) {
		return noteName.endsWith("$$HTML") || noteName.endsWith("$$html");
	}

	public static boolean isHtmlHead(String noteName) {
		return noteName.endsWith("$$HEAD") || noteName.endsWith("$$head");
	}
}
