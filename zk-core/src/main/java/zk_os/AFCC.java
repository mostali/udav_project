package zk_os;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpe.NT;
import mpu.core.ARG;
import mpu.core.ARR;
import zk_page.node_state.FileState;

import java.nio.file.Path;
import java.util.ArrayList;
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
	public static final String DIR_PAGECOMS = ".pagecoms";
	public static final String DIR_PAGECOM = ".pagecom";
	public static final String DIR_PLANE = ".plane";
	public static final String DIR_HEADS = ".heads";
	public static final String PAGE_INDEX_ALIAS = ".index";
	public static final String SD3_INDEX_ALIAS = ".index";

	public static final String PROPS_FILE_EXT = "..";

	public static boolean isNotTechName(Path path) {
		return !isReservedFileName(path);
	}

	public static boolean isReservedFileName(Path path) {
		return isReservedFileName(path.getFileName().toString());
	}

	public static boolean isReservedFileName(String fn) {
		return SD3_INDEX_ALIAS.equalsIgnoreCase(fn) ||//
				DIR_COMS.equalsIgnoreCase(fn) ||//
				DIR_FORMS.equalsIgnoreCase(fn) || //
				DIR_PAGES.equalsIgnoreCase(fn) ||//
				DIR_PAGECOMS.equalsIgnoreCase(fn) ||//
				DIR_PLANES.equalsIgnoreCase(fn) ||//
				fn.matches("[t|v]\\d++$") //
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

	public static List<Path> getAllExistParts(Path pathForm) {
		List<Path> parts = new ArrayList<>();
		parts.add(pathForm);
		int next = 1;
		Path nextPart;
		while (UFS.existFile(nextPart = FileState.partPath(pathForm, next++))) {
			parts.add(nextPart);
		}
		return parts;
	}

	public static List<Path> getAllPathParts(Path pathForm, int size) {
		List<Path> parts = new ArrayList<>();
		parts.add(pathForm);
		for (int i = 1; i < size; i++) {
			parts.add(FileState.partPath(pathForm, i));
		}
		return parts;
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
