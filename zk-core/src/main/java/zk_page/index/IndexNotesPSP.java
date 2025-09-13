package zk_page.index;

import mpc.fs.UF;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import zk_form.control.BreadDiv;
import zk_notes.AxnTheme;
import zk_com.base_ext.DoubleLn;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.control.NotesSpace;
import zk_notes.events.ANMD;
import zk_os.AFC;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.core.SpVM;
import zk_page.index.qview.ItemsQView;
import zk_notes.node_state.FormState;

import java.nio.file.Path;
import java.util.Set;

public class IndexNotesPSP extends IndexNotesRootPSP {

	private final String sd3;

	public IndexNotesPSP(Window window, String sd3) {
		super(window);
		this.sd3 = sd3;
	}

	@Override
	public void buildPage() {

		SpVM spVM = SpVM.get();

		NotesPSP.initStyleWindowDefault(window);

		window.appendChild(new BreadDiv(sd3, false).withPlaneLabel());

		NotesSpace.initPage(window);

		Set<Path> allSd3_or_PagesSd3 = AFC.PAGES.DIR_PAGES_LS_CLEAN(sd3);

		for (Path pageDir : allSd3_or_PagesSd3) {

			String pageName = UF.fn(pageDir);
			if (ItemPath.isAliasIndexPlane(pageName)) {
				continue;
			}

			Pare<String, String> sdn = Pare.of(sd3, pageName);
			FormState pageState = FormState.ofPageState_orCreate(sdn);

			if (!pageState.isAllowedAccess_View(true)) {
				continue;
			}

			Ln ln = (Ln) new Ln(pageName).decoration_none().font_size(AxnTheme.FONT_SIZE_APP_LINK);

			ln.onCLICK((e) -> {
				Sdn sd3pn = Sdn.of(sd3, pageDir.getFileName().toString());
				new ItemsQView(sd3pn).openInFirstWindow();
			});


			if (Sec.isNotAnonim()) {
				Menupopup0 menuLn = ln.getOrCreateMenupopup(window);
				ANMD.applyPlaneLink(menuLn, sdn.key());
			}

			DoubleLn child = new DoubleLn(ln, RSPath.PAGE.toPlanPage(sdn), false).randomAbsPosition();

			window.appendChild(child);

		}

	}

}
