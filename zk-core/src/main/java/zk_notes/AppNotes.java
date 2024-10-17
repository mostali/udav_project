package zk_notes;

import mpc.env.APP;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.ext.EXT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.env.AP;
import mpc.fs.LS_SORT;
import mpc.fs.fd.EFT;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import zk_os.AFC;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.List;

public class AppNotes {

	public static Path getPathOfFormNote_ANY(String nodeName, Pare<String, String> sd3pn) {
		return X.notEmpty(sd3pn) ? getPathOfFormNote_NOPPI(sd3pn, nodeName) : getPathOfFormNote_PPI(nodeName);
	}

	public static Path getPathOfFormNote_PPI(String notesName) {
		PagePathInfo ppi = SpVM.get().ppi();
		return getPathOfFormNote_NOPPI(ppi.subdomain3(), ppi.pagename(), notesName);
	}

	public static Path getPathOfFormNote_NOPPI(Pare<String, String> sd3pn, String notesName) {
		return getPathOfFormNote_NOPPI(sd3pn.key(), sd3pn.val(), notesName);
	}

	@Deprecated
	public static Path getPathOfFormNote_NOPPI(String sd3, String pagename, String notesName) {
		return AFC.getRpaFormStatePath(sd3, pagename, notesName);
	}

	public static Path getPathOfComNote(String notesName) {
		PagePathInfo ppi = SpVM.get().ppi();
		String pagename = ppi.pagename();
		String sd3 = ppi.subdomain3();
		return AFC.getRpaComStatePath(sd3, pagename, notesName, EXT.PROPS);
	}

	@Deprecated
	public static List<Path> getAllNotesOfPage(Pare sdn) {
		Path rpaForms = AFC.getRpaForms(sdn);
		return EFT.DIR.ls(rpaForms, LS_SORT.NATURAL, ARR.EMPTY_LIST);
	}


	public static String getTgBotId(String... defRq) {
		return AP.get(APP.APK_TG_BT_ID, defRq);
	}

	public static Integer getVkBotId(Integer... defRq) {
		Integer as = AP.getAs(APP.APK_VK_BT_ID, Integer.class, null);
		if (as != null) {
			return -(Math.abs(as));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("App Prop '%s' required", APP.APK_TG_BT_ID), defRq);
	}

	public static Path getRpaForms_BlankDir(Pare sdn, String name, Integer... randomSfxLen) {
		String namePfx = ARG.isDefNNF(randomSfxLen) && randomSfxLen[0] > 0 ? RANDOM.ALPHA(randomSfxLen[0]) : "";
		return AFC.getRpaForms(sdn).resolve(name + namePfx);
	}
}
