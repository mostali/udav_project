package zk_page.index;

import mpc.fs.UF;
import mpu.pare.Pare;
import mpe.call_msg.core.NodeID;
import zk_notes.AxnTheme;
import zk_com.base_ext.DoubleLn;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.control.NotesSpace;
import zk_notes.events.ANMD;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_page.index.qview.ItemsQView;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;
import java.util.Set;

public class IndexNPSP extends IndexRootNPSP {

	private final String sd3;

	public IndexNPSP(Window window, String sd3) {
		super(window);
		this.sd3 = sd3;
	}

	@Override
	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

//		window.appendChild(new BreadDiv(sd3, false).withPlaneLabel());

		NotesSpace.initOnPage(window);

		Set<Path> allSd3_or_PagesSd3 = AFC.PAGES.DIR_PAGES_LS_CLEAN(sd3);

		for (Path pageDir : allSd3_or_PagesSd3) {

			String pageName = UF.fn(pageDir);
			if (NodeID.isPlaneAliasIndex(pageName)) {
				continue;
			}

			Pare<String, String> sdn = Pare.of(sd3, pageName);
			ObjState pageState = AppStateFactory.forPage(sdn);

			if (!pageState.isAllowedAccess_VIEW()) {
				continue;
			}

			Ln ln = (Ln) new Ln(pageName).decoration_none().font_size(AxnTheme.FONT_SIZE_APP_LINK);

			ln.onCLICK((e) -> {
				Sdn sd3pn = Sdn.of(sd3, pageDir.getFileName().toString());
				new ItemsQView(sd3pn).openInFirstWindow();
			});


			if (SecMan.isNotAnonimUnsafe()) {
				Menupopup0 menuLn = ln.getOrCreateMenupopup(window);
				ANMD.applyPlaneLink(menuLn, sdn.key());
			}

			DoubleLn child = new DoubleLn(ln, RSPath.PAGE.toPageLink(sdn), false).randomAbsPosition();

			window.appendChild(child);

		}

	}

}
