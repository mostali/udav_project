package zk_page.index;

import lombok.RequiredArgsConstructor;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_notes.control.NotesSpace;
import zk_page.index.qview.PlanesQView;

@RequiredArgsConstructor
public class IndexNotesRootPSP {

	protected final Window window;

	public void buildPage() {

		NotesPSP.initStyle(window);

		NotesSpace.initPage(window);

		new PlanesQView(window).openInFirstWindow();

	}

}
