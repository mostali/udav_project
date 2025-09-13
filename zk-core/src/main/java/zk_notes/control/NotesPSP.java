package zk_notes.control;

import lombok.SneakyThrows;
import mpc.env.AP;
import org.zkoss.zul.Window;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_os.AppZos;
import zk_os.AppZosProps;
import zk_os.sec.ROLE;
import zk_os.sec.SecMan;
import zk_page.ZKS;
import zk_page.behaviours.BgImg;
import zk_page.core.*;
import zk_notes.node_state.FormState;
import zk_notes.AppNotesProps;
import zk_page.index.IndexNotesPSP;
import zk_page.index.IndexNotesRootPSP;
import zklogapp.header.BottomHistoryPanel;

//@PageRoute(pagename = "zk_notes", role = ROLE.USER)
public class NotesPSP extends PageSP implements IPerPage, IZState, WithLogo, WithMainTbx, WithSearch {

	public NotesPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	public static void initStyleWindowDefault(Window window) {

		ZKS.PADDING0(window);
		FormState formState = SpVM.get().pageState();

		String margin = formState.get("margin", null);
		ZKS.MARGIN(window, margin != null ? margin : "46px 0px 0 0");

		String height_min = formState.get("height-min", null);
//		if (height_min == null) {
//			height_min = AP.get("height-min");
//		}
		ZKS.HEIGHT_MIN(window, height_min != null ? height_min : "3900px");

		String bgUrl = BgImg.getBgImageViaNigthMode(formState);
		ZKS.BGIMAGE(window, bgUrl, "contain", "top", "repeat");

//		ZKS.BGCOLOR_WIN(window, "rgba(0,0,0,0.0)", "rgba(0,0,0,0.0)");

//		ZKS.PADDING_WIN(window, null, null);

	}

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
//		NoteLogo noteLogo = new NoteLogo(null, "/img/xnr16.png");
		NoteLogo noteLogo = new NoteLogo(ROLE.toIcon(), null);
		return noteLogo;
	}

	@SneakyThrows
	public void buildPageImpl() {

		if (buildPage_isIndexPage()) {
			return;
		}


		//
		//

		initStyleWindowDefault(window);

		/**
		 * Auth
		 */

		FormState pageState = getPageState();

//		window.appendChild(new AuthTbx());

		if (AppZos.isDebugEnable()) {
			window.appendChild(new BottomHistoryPanel());
		}

		{
			checkAndOpenStatablePanels(pageState);
		}


		/**
		 * Show Note Space
		 */

		NotesSpace.initPage(window);
	}

	public static void checkAndOpenStatablePanels() {
		checkAndOpenStatablePanels(SpVM.get().pageState());
	}

	public static void checkAndOpenStatablePanels(FormState pageState) {
		if (SecMan.isNotAnonim()) {
			if (pageState.hasPropEnable(AppZosProps.LOGO_HEADER_OPEN, false)) {
				NotesPageHeader.openSimple();
			}

			if (pageState.hasPropEnable(AppNotesProps.PAGE_CONFIG_OPEN, false)) {
				NotesHeaderProps.openSimple();
			}
		}
	}

	private boolean buildPage_isIndexPage() {
		PagePathInfo ppi = spVM().ppi();
		boolean isEmptyPagename = ppi.isEmptyPagename();
//		boolean isIndexPage = ItemPath.PAGE_INDEX_ALIAS.equals(ppi.pagename0());
		if (!isEmptyPagename) {
			return false;
		}
		if (ppi.isEmptySd3()) {
			new IndexNotesRootPSP(window).buildPage();
		} else {
			new IndexNotesPSP(window, ppi.subdomain30()).buildPage();
		}
		return true;
	}


}