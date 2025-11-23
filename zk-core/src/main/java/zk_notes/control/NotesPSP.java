package zk_notes.control;

import lombok.SneakyThrows;
import org.zkoss.zul.Window;
import mpe.call_msg.core.NodeID;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_notes.AxnTheme;
import zk_os.sec.SecMan;
import zk_os.tasks.TaskPanel;
import zk_os.tasks.v1.TaskPanel_V1;
import zk_page.ZKS;
import zk_page.behaviours.BgImg;
import zk_page.core.*;
import zk_notes.node_state.ObjState;
import zk_notes.AppNotesProps;
import zk_page.index.IndexItemNPSP;
import zk_page.index.IndexNPSP;
import zk_page.index.IndexRootNPSP;
import zk_page.panels.AppConfigPropsTopPanel;
import zk_page.panels.QuickNotesTopPanel;
import zk_page.with_com.WithMainTbx;
import zk_page.with_com.WithSearch;
import zk_page.panels.BottomHistoryPanel;

//@PageRoute(pagename = "zk_notes", role = ROLE.USER)
public class NotesPSP extends PageSP implements IPerPage, IZState, WithLogo, WithMainTbx, WithSearch {

	public NotesPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	public static void initStyleWindowDefault(Window window) {

		ZKS.PADDING0(window);

		ObjState formState = SpVM.get().pageState();

		//
		String margin = formState.get("margin", null);

		ZKS.MARGIN(window, margin != null ? margin : AxnTheme.HEADER_HEIGHT + "px 0px 0 0");

		//

		String height_min = formState.get("height-min", null);

		ZKS.HEIGHT_MIN(window, height_min != null ? height_min : AxnTheme.DEFAULT_PAGE_HEIGHT_MIN + "px");

		//
		String bgUrl = BgImg.getBgImageRelPath(formState);
		ZKS.BGIMAGE(window, bgUrl, "contain", "top", "repeat");

		//
	}

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo();
//		NoteLogo noteLogo = new NoteLogo(null, "/img/xnr16.png");
		return noteLogo;
	}

	@SneakyThrows
	public void buildPageImpl() {

		if (buildPage_isIndexPage()) {
			return;
		}

		if (buildPage_isIndexItemPage()) {
			return;
		}


		//
		//

		initStyleWindowDefault(window);

		/**
		 * Auth
		 */

		ObjState pageState = getPageState();

//		window.appendChild(new AuthTbx());


		{
			checkAndOpenStatablePanels(pageState);
		}


		/**
		 * Show Note Space
		 */

		NotesSpace.initOnPage(window);
	}

	public static void checkAndOpenStatablePanels() {
		checkAndOpenStatablePanels(SpVM.get().pageState());
	}

	public static void checkAndOpenStatablePanels(ObjState pageState) {
		if (SecMan.isOwnerOrAdmin()) {
			if (pageState.hasPropEnable(AppNotesProps.PP_CONFIG_OPENED, false)) {
				AppConfigPropsTopPanel.openSimple();
			}
			if (pageState.hasPropEnable(AppNotesProps.PP_QUICK_NOTES_OPENED, false)) {
				QuickNotesTopPanel.openSimple();
			}

			if (pageState.hasPropEnable(AppNotesProps.PP_BOTTOM_HISTORY_OPEN, false)) {
				BottomHistoryPanel.openSimple();
			}

			if (pageState.hasPropEnable(AppNotesProps.PP_TASKS_OPENED, false)) {
				TaskPanel.openSimple();
			}

			if (pageState.hasPropEnable(AppNotesProps.PP_TASKS_V1_OPENED, false)) {
				TaskPanel_V1.openSimple();
			}

		}
	}

	private boolean buildPage_isIndexPage() {
		PagePathInfo ppi = spVM().ppi();
		boolean isEmptyPagename = ppi.isEmptyPagename();
		if (!isEmptyPagename) {
			return false;
		}
		if (ppi.isEmptySd3()) {
			new IndexRootNPSP(window).buildPage();
		} else {
			new IndexNPSP(window, ppi.plane()).buildPage();
		}
		return true;
	}

	private boolean buildPage_isIndexItemPage() {
		NodeID nodeID = spVM().nodeId(null);
		if (nodeID == null) {
			return false;
		}
		new IndexItemNPSP(window, nodeID).buildPage();
		return true;
	}


}