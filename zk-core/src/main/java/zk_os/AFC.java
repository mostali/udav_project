package zk_os;

import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import zk_notes.AppNotes;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

//App File Core
public class AFC {

	public static final Class NODE_FILE_NAME = AppNotes.class;

	//
	//
	//Planes
	//

	public static List<Path> DIR_PLANES_LS_CLEAN() {
		return filterNoTechName(DIR_PLANES_LS());
	}

	public static List<Path> DIR_PLANES_LS() {
		return EFT.DIR.ls(DIR_PLANES(), ARR.EMPTY_LIST);
	}

	public static Path DIR_PLANES() {
		return AppZosCore.getRpa().resolve(AFCC.DIR_PLANES);
	}

//	public static Path getRpaPlane_Item_(String sd3) {
//		return DIR_PLANES().resolve(ANCF.DIR_PLANES).resolve(sd3 + ".plane");
//	}

//	public static Path getRpaPlaneItem(String sd3, String pagename, String itemname) {
//		return getRpaPlane(sd3, pagename).resolve(itemname);
//	}

	public static Path getRpaPlaneDir(String sd3) {
		return DIR_PLANES().resolve(sd3);
	}

	public static Path getRpaPlaneStatePath(String sd3) {
		return getRpaPlaneDir(sd3).resolve(".plane");
	}

	//
	//
	//Pages
	//

	public static List<Path> DIR_PAGES_LS_CLEAN(String sd3) {
		return filterNoTechName(AFC.DIR_PAGES_LS(sd3));
	}

	public static List<Path> DIR_PAGES_LS(String sd3) {
		return EFT.DIR.ls(getRpaPageOrIndex(sd3), ARR.EMPTY_LIST);
	}

	public static Path getRpaPageOrIndex(String sd3) {
		return DIR_PLANES().resolve(wrapSd3(sd3));
	}


	public static Path getRpaPageStatePath(Pare sdn) {
		return getRpaPageStatePath(sdn.keyStr(), sdn.valStr());
	}

	//	public static Path DIR_PAGES(String sd3) {
//		return getRpaPageOrIndex(sd3).resolve(ANCF.DIR_PAGES);
//	}

	public static Path getRpaPageStatePath(String sd3, String pagename) {
//		return DIR_PAGES(sd3).resolve(pagename + ".page");
		return DIR_PLANES().resolve(sd3).resolve(normEntName(pagename)).resolve(".page");
	}

	private static @NotNull String normEntName(String entityname) {
//		return UF.clearStringCyrRemoveSlash(pagename);
		return UF.clearStringCyrRemoveSlash(entityname);
	}

	//
	//
	public static Path getRpaPageDir(Pare sdn) {
		return getRpaPageDir(sdn.keyStr(), sdn.valStr());
	}

	public static Path getRpaPageDir(String sd3, String pagename) {
		return DIR_PLANES().resolve(sd3).resolve(pagename);
	}

	//
	//
	// Forms
	//

	public static List<Path> DIR_FORMS_LS_CLEAN(String sd3, String pagename) {
		return filterNoTechName(DIR_FORMS_LS(sd3, pagename));
	}

	public static List<Path> DIR_FORMS_LS(String sd3, String pagename) {
		return EFT.DIR.ls(DIR_FORMS(sd3, pagename), ARR.EMPTY_LIST);
	}

	public static Path DIR_FORMS(String sd3, String pagename) {
		return getRpaPageOrIndex(sd3).resolve(pagename).resolve(AFCC.DIR_FORMS);
	}

	public static Path getRpaForms(Pare<String, String> sdn) {
		return DIR_FORMS(sdn.key(), sdn.val());
	}


	public static Path getRpaFormStatePath(Pare<String, String> sdn, String formname) {
		return getRpaFormStatePath(sdn.key(), sdn.val(), formname);
	}

	public static Path getRpaFormStatePath(String sd3, String pagename, String formname) {
		return getRpaFormStatePath(sd3, pagename, (formname), EXT.PROPS);
	}

	public static Path getRpaFormStatePath(String sd3, String pagename, String formname, EXT ext) {
		return DIR_FORMS(sd3, pagename).resolve(normEntName(formname)).resolve(toFileName_Default(ext));
	}


	//
	// Coms

	public static List<Path> DIR_COMS_LS_CLEAN(String sd3, String pagename) {
		return filterNoTechName(DIR_COMS_LS(sd3, pagename));
	}

	public static List<Path> DIR_COMS_LS(String sd3, String pagename) {
		return EFT.DIR.ls(DIR_COMS(sd3, pagename));
	}

	public static Path DIR_COMS(String sd3, String pagename) {
		return getRpaPageOrIndex(sd3).resolve(pagename).resolve(AFCC.DIR_COMS);
	}

	public static Path getRpaComStatePath(String sd3, String pagename, String comname, EXT ext) {
		return DIR_COMS(sd3, pagename).resolve(normEntName(comname)).resolve(toFileName_Default(ext));
	}

	//
	//
	//

	public static String toFileName_Default(EXT ext) {
		return toFileName(NODE_FILE_NAME, ext);
	}

	public static String toFileName(Class com, EXT json) {
		return json.toFileName(com.getSimpleName());
	}

	//
	//
	private static List<Path> filterNoTechName(List<Path> files) {
		return files.stream().filter(AFCC::isNotTechName).collect(Collectors.toList());
	}


	public static String wrapSd3(String... sd3) {
		return sd3 == null || sd3.length == 0 || sd3[0] == null || sd3[0].length() == 0 ? AFCC.PAGE_INDEX_ALIAS : sd3[0];
	}

	public static String unwrapSd3(String... sd3) {
		return sd3 == null || sd3.length == 0 || sd3[0] == null ? "" : sd3[0];
	}

	public static String unwrapSd3(Pare<String, String> sd3pn) {
		return unwrapSd3(sd3pn.key());
	}
}
