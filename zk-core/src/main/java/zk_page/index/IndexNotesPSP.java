package zk_page.index;

import mpc.fs.UF;
import mpu.pare.Pare;
import zk_form.control.BreadDiv;
import zk_notes.ANM;
import zk_notes.AppNotesTheme;
import zk_notes.coms.DoubleLn;
import zk_notes.control.MainTbx;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.control.NotesSpace;
import zk_os.AFC;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.core.SpVM;
import zk_page.index.qview.ItemsQView;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;

public class IndexNotesPSP extends IndexNotesRootPSP {

	private final String sd3;

	public IndexNotesPSP(Window window, String sd3) 	{
		super(window);
		this.sd3 = sd3;
	}


	@Override
	public void buildPage() {

		SpVM spVM = SpVM.get();

		NotesPSP.initStyleWIndowDefault(window);

		window.appendChild(new MainTbx());

		window.appendChild(new BreadDiv(sd3, false).withPlaneLabel());

		NotesSpace.initPage(window);

		List<Path> allSd3_or_PagesSd3 = AFC.DIR_PAGES_LS_CLEAN(sd3);

		for (Path pageDir : allSd3_or_PagesSd3) {

			String pageName = UF.fn(pageDir);

			Pare<String, String> sdn = Pare.of(sd3, pageName);
			FormState pageState = FormState.ofPageState(sdn);

			if (!pageState.isAllowedAccess_View(true)) {
				continue;
			}

			Ln ln = (Ln) new Ln(pageName).decoration_none().font_size(AppNotesTheme.APP_LINK_FONT_SIZE);

			ln.onCLICK((e) -> {
				Sdn sd3pn = Sdn.of(sd3, pageDir.getFileName().toString());
				new ItemsQView(sd3pn).openInFirstWindow();
			});


			if (Sec.isNotAnonim()) {
				Menupopup0 menuLn = ln.getOrCreateMenupopup(window);
				ANM.applyPlaneLink(menuLn, sdn.key());
			}

			DoubleLn child = new DoubleLn(ln, RSPath.PAGE.toPlanPage(sdn), false).randomAbsPosition();

			window.appendChild(child);

		}

		IndexNotesRootPSP.initPageHeadLibs(window, spVM);


	}

	public class PageLn extends Ln {

		public PageLn(String label) {
			super(label);
		}

		@Override
		protected void init() {
			super.init();

		}
	}


}
