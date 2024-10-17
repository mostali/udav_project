package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.fs.UF;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_form.control.BreadDiv;
import zk_notes.ANM;
import zk_notes.AppNotesTheme;
import zk_notes.coms.DoubleLn;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.sec.Sec;
import zk_page.index.RSPath;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class PlanesQView extends QView {

	@Override
	protected Component newBreadDiv() {
		return new BreadDiv();
	}

	private final Window window;

	@Override
	public String planeName() {
		return Env.getAppName();
	}

	@Override
	protected void init() {
		super.init();

		appendChild(new BreadDiv());

		List<Path> paths = AFC.DIR_PLANES_LS_CLEAN();

		for (Path dirSd3 : paths) {
			String sd3 = UF.fn(dirSd3);
			FormState formState = FormState.ofPlaneState(sd3);
			if (!formState.isAllowedAccess_View(true)) {
				continue;
			}

			PlaneLn child = new PlaneLn(window, dirSd3);

			final String planeName = dirSd3.getFileName().toString();
			DoubleLn dblLink = new DoubleLn(child, RSPath.PLANE.toPlaneLink(planeName), false).randomAbsPosition();

			window.appendChild(dblLink);
		}
	}

	public static class PlaneLn extends Ln {
		private final Window window;

		public PlaneLn(Window window, Path dirSd3) {
			super(dirSd3.getFileName().toString());
			this.window = window;

			final String planeName = dirSd3.getFileName().toString();

			onCLICK(e -> new PagesQView(planeName).openInFirstWindow());

			decoration_none();

			font_size(AppNotesTheme.APP_LINK_FONT_SIZE);

			if (Sec.isNotAnonim()) {
				Menupopup0 menuLn = getOrCreateMenupopup(window);
				ANM.applyPlaneLink(menuLn, planeName);
			}

		}

	}
}
