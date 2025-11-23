package zk_notes.node_state;

import mpc.fs.ext.EXT;
import mpu.pare.Pare;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;

import java.nio.file.Path;

public class AppStatePath {

	//
	//PLANE
	public static Path getPlanePropsPath(String sd3) {
		return AFC.PLANES.getStatePath_PROPS(sd3);
	}

	//
	//PAGE
	public static Path getPagePropsPath(String sd3, String pagename) {
		return AFC.PAGES.getStatePath_PROPS(sd3, pagename);
	}

	//
	//PAGECOM
	public static Path getPagecomPropsPath(String sd3, String pagename, String comname) {
		return AFC.PAGECOMS.getStatePath(sd3, pagename, comname, EXT.JSON);
	}

	//
	//FORM
	@Deprecated
	public static Path getFormDataPath_PPI(String notesName) {
		PagePathInfo ppi = SpVM.get().ppi();
		return getFormDataPath(ppi.plane(), ppi.pagenameRq(), notesName);
	}

	public static Path getFormDataPathCurrent(String notesName) {
		Sdn sdn = Sdn.get();
		return getFormDataPath(sdn.plane(), sdn.page(), notesName);
	}

	public static Path getFormDataPath(Pare<String, String> sd3pn, String notesName) {
		return getFormDataPath(sd3pn.key(), sd3pn.val(), notesName);
	}

	public static Path getFormDataPath(String sd3, String pagename, String notesName) {
		return AFC.FORMS.getStatePath_DATA(sd3, pagename, notesName);
	}

	public static Path getFormPropsPathCurrent(String notesName) {
		Sdn sdn = Sdn.get();
		return getFormPropsPath(sdn.plane(), sdn.page(), notesName);
	}

	public static Path getFormPropsPath(String sd3, String pagename, String notesName) {
		return AFC.FORMS.getStatePath_PROPS(sd3, pagename, notesName);
	}

	//
	//COM

	public static Path getComPropsPathCurrent(String notesName) {
		Sdn sdn = Sdn.get();
		return getComPropsPath(sdn.plane(), sdn.page(), notesName);
	}

	public static Path getComPropsPath(String sd3, String pagename, String notesName) {
		return AFC.COMS.getStatePath(sd3, pagename, notesName, EXT.JSON);
	}


}
