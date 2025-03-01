package zk_notes;

import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import zk_os.AFC;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;

import java.nio.file.Path;

public class AppNotes {

	public static Path getPathOfFormNote_ANY(String nodeName, Pare<String, String> sd3pn) {
		return X.notEmpty(sd3pn) ? getPathOfFormNote_SDN(sd3pn, nodeName) : getPathOfFormNote_PPI(nodeName);
	}

	public static Path getPathOfFormNote_PPI(String notesName) {
		PagePathInfo ppi = SpVM.get().ppi();
		return getPathOfFormNote_SDN(ppi.subdomain30(), ppi.pagename0(), notesName);
	}

	public static Path getPathOfFormNote_SDN(Pare<String, String> sd3pn, String notesName) {
		return getPathOfFormNote_SDN(sd3pn.key(), sd3pn.val(), notesName);
	}

	private static Path getPathOfFormNote_SDN(String sd3, String pagename, String notesName) {
		return AFC.FORMS.getRpaFormStatePath(sd3, pagename, notesName);
	}


	public static Path getFormBlankDir(Pare sdn, String name, Integer... randomSfxLen) {
		String namePfx = ARG.isDefNNF(randomSfxLen) && randomSfxLen[0] > 0 ? RANDOM.ALPHA(randomSfxLen[0]) : "";
		return AFC.FORMS.DIR_FORMS(sdn).resolve(name + namePfx);
	}

	public static Path getCurrentPageDir() {
		return AFC.PAGES.getRpaPageDir(SpVM.get().sdn0());
	}
}
