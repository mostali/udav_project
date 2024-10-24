package zk_notes.control;

import lombok.SneakyThrows;
import mpu.X;
import org.zkoss.zul.Window;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_os.AppZos;
import zk_os.AppZosConfig;
import zk_os.AppZosProps;
import zk_os.sec.ROLE;
import zk_os.sec.SecMan;
import zk_page.ZKS;
import zk_page.core.PagePathInfo;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.node_state.FormState;
import zk_notes.AppNotesProps;
import zk_page.index.IndexNotesPSP;
import zk_page.index.IndexNotesRootPSP;
import zklogapp.header.BottomHistoryPanel;

@PageRoute(pagename = "zk_notes", role = ROLE.USER)
public class NotesPSP extends PageSP implements IPerPage, IZState, WithLogo {

	public NotesPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	public static void initStyleWIndowDefault(Window window) {
		ZKS.PADDING0(window);
		FormState formState = SpVM.get().pageState();

		String margin = formState.get("margin", null);
		ZKS.MARGIN(window, margin != null ? margin : "32px 0px 0 0");

		String height_min = formState.get("height-min", null);
		ZKS.HEIGHT_MIN(window, height_min != null ? height_min : "3600px");

		String bgUrl = AppZos.getBgImageViaNigthMode();

		ZKS.BGIMAGE(window, bgUrl, "contain", "top", "repeat");
	}

	@Override
	public LogoCom getLogoOrCreate() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo(null, "/img/xnr16.png");
		return noteLogo;
	}

	@SneakyThrows
	public void buildPageImpl() {

		if (buildPage_isIndexPage()) {
			return;
		}


		//
		//

		initStyleWIndowDefault(window);

		/**
		 * Auth
		 */

		FormState pageState = getPageState();

//		window.appendChild(new AuthTbx());

		window.appendChild(new MainTbx());

		if (AppNotesProps.APR_DEV_ENABLE.getValueOrDefault(false)) {
			window.appendChild(new BottomHistoryPanel());
		}


		{
			checkStatableView(pageState);
		}


		/**
		 * Show Note Space
		 */

		NotesSpace.initPage(window);
	}

	public static void checkStatableView(FormState pageState) {
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
		if (!isEmptyPagename) {
			return false;
		}
		if (X.empty(ppi.subdomain3())) {
			new IndexNotesRootPSP(window).buildPage();
		} else {
			new IndexNotesPSP(window, ppi.subdomain3()).buildPage();
		}
		return true;
	}


}