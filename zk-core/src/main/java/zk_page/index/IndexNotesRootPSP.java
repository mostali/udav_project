package zk_page.index;

import lombok.RequiredArgsConstructor;
import udav_net.apis.zznote.ItemPath;
import zk_form.ext.MenuPicker;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_notes.control.NotesSpace;
import zk_notes.node_state.libs.PageState;
import zk_page.core.WithMainTbx;
import zk_page.index.qview.PlanesQView;

@RequiredArgsConstructor
public class IndexNotesRootPSP implements WithMainTbx {

	protected final Window window;

	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

		NotesSpace.initPage(window);

		NotesPSP.checkAndOpenStatablePanels();

//		window.
//		if (PageState.NavMenuMode.mnd.isEnableBlankParam()) {
//			window.appendChild(MenuPicker.ofAllSd3());
//		}
//
//		if (PageState.NavMenuMode.mnp.isEnableBlankParam()) {
//			window.appendChild(MenuPicker.ofAllPages(ItemPath.SD3_INDEX_ALIAS));
//		}
//
//		if (PageState.NavMenuMode.mnf.isEnableBlankParam()) {
//			window.appendChild(MenuPicker.ofAllForms(ItemPath.SD3_INDEX_ALIAS, ItemPath.PAGE_INDEX_ALIAS));
//		}

		new PlanesQView(window).openInFirstWindow();

	}

}
