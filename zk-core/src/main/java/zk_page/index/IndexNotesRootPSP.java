package zk_page.index;

import lombok.RequiredArgsConstructor;
import mpc.fs.ext.EXT;
import mpc.log.L;
import mpu.core.RW;
import zk_com.base.Xml;
import zk_notes.control.MainTbx;
import zk_notes.control.NotesPSP;
import org.zkoss.zul.Window;
import zk_notes.control.NotesSpace;
import zk_os.AFC;
import zk_page.ZKC;
import zk_page.core.SpVM;
import zk_page.index.qview.PlanesQView;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class IndexNotesRootPSP {

	protected final Window window;

	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

		NotesSpace.initPage(window);

		SpVM spVM = SpVM.get();
		FormState pageState = spVM.pageState();
		FormState planeState = spVM.planeState();

		NotesPSP.checkStatableView(pageState);

		window.appendChild(new MainTbx());

		new PlanesQView(window).openInFirstWindow();

//		new PagesView(spVM.subdomain3()).openInFirstWindow();

		initPageHeadLibs(window, spVM);

	}

	public static void initPageHeadLibs(Window window, SpVM spVM) {
		List<Path> rootLibs = AFC.DIR_HEADS_LS();
		applyPageHeadLibs(window, rootLibs);
		applyPageHeadLibs(window, AFC.getRpaHeadsStatePathLs(spVM.subdomain3()));
	}

	public static void applyPageHeadLibs(Window window, List<Path> rootLibs) {
		for (Path rootLib : rootLibs) {
			EXT ext = EXT.of(rootLib);
			L.info("Found head data {} -> {}", ext, rootLib);
			switch (ext) {
				case JS:
					ZKC.getFirstPageCtrl().addAfterHeadTags(RW.readContent(rootLib));
					break;
				case HEAD:
					ZKC.getFirstPageCtrl().addBeforeHeadTags(RW.readContent(rootLib));
					break;
				case XML:
				case HTML:
					window.appendChild(Xml.ofFile(rootLib));
					break;

			}
		}
	}

}
