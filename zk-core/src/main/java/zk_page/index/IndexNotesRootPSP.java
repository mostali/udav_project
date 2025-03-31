package zk_page.index;

import lombok.RequiredArgsConstructor;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_notes.control.NotesSpace;
import zk_page.ZkPageAuto;
import zk_page.core.SpVM;
import zk_page.core.WithMainTbx;
import zk_page.index.qview.PlanesQView;
import zk_notes.node_state.FormState;

@RequiredArgsConstructor
public class IndexNotesRootPSP implements WithMainTbx {

	protected final Window window;

	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

		NotesSpace.initPage(window);

		SpVM spVM = SpVM.get();
		FormState pageState = spVM.pageState();
		FormState planeState = spVM.planeState();

		NotesPSP.checkStatableView(pageState);

		new PlanesQView(window).openInFirstWindow();

//		new PagesView(spVM.subdomain3()).openInFirstWindow();

//		ZkPageAuto.initPageHeadLibs(window);

	}

}
