package zk_page.zpage;

import lombok.RequiredArgsConstructor;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_form.control.breadcrumbs.BreadDiv;
import zk_notes.control.NotesSpace;
import zk_os.core.Sdn;

@RequiredArgsConstructor
public class ZPage {

	public final Sdn sdn;
	public final HtmlBasedComponent window0;

	public static ZPage of(Sdn sdn, HtmlBasedComponent window) {
		return new ZPage(sdn, window);
	}

	public void addBreadDiv() {
		window0.appendChild(new BreadDiv(sdn));
	}

	public void addNotesSpace() {
		NotesSpace.newNotesSpaceIn((Window) window0);
	}
}
