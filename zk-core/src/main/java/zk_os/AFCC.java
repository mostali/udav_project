package zk_os;

import mpe.NT;

import java.nio.file.Path;

//App File Core Com's
public class AFCC {
	//	public static final String DIR_PAGES_FC = ".pages.fc";
	public static final String DIR_COMS = ".coms";
	public static final String DIR_FORMS = ".forms";
	public static final String DIR_PLANES = ".planes";
	public static final String DIR_PAGES = ".pages";
	public static final String DIR_PAGE = ".page";
	public static final String DIR_PLANE = ".plane";
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
}
