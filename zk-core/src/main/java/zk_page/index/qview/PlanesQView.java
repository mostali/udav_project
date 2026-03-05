package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_form.control.breadcrumbs.qview.QBreadDiv;
import zk_notes.AxnTheme;
import zk_com.base_ext.DoubleLn;
import zk_notes.events.ANMD;
import zk_notes.node_state.impl.PlaneState;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_os.walkers.PlaneWalker;
import zk_page.index.RSPath;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;

@RequiredArgsConstructor
public class PlanesQView extends QView {

	@Override
	protected Component newBreadDiv() {
		return new QBreadDiv(Sdn.ofRootPlane());
	}

	private final Window window;

	@Override
	public String planeName() {
		return Env.getAppName();
	}

	@Override
	protected void init() {
		super.init();

//		appendChild(new BreadDiv());

		new PlaneWalker() {

			@Override
			protected Boolean walkPlane(String plane, Path dir, PlaneState planeState) {
//				String sd3 = UF.fn(dirSd3);

//				FormState formState = FormState.ofPlaneState(sd3);

				if (!planeState.isAllowedAccess_VIEW()) {
					return true;
				}

				PlaneLn child = new PlaneLn(window, dir);

				final String planeName = dir.getFileName().toString();
				DoubleLn dblLink = new DoubleLn(child, RSPath.PLANE.toPlaneLink(planeName), false).randomAbsPosition();

				window.appendChild(dblLink);

				return true;
			}

		}.withUserDomain(false).withSystemPlanes(false).withIndex(false).doWalk();

//		Set<Path> paths = AFC.DIR_PLANES_LS_CLEAN(false);

//		for (Path dirSd3 : paths) {

//		}
	}

	public static class PlaneLn extends Ln {
		private final Window window;

		public PlaneLn(Window window, Path dirSd3) {
			super(dirSd3.getFileName().toString());
			this.window = window;

			final String planeName = dirSd3.getFileName().toString();

			onCLICK(e -> new PagesQView(planeName).openInFirstWindow());

			decoration_none();

			font_size(AxnTheme.FONT_SIZE_APP_LINK);

			if (SecMan.isNotAnonimUnsafe()) {
				Menupopup0 menuLn = getOrCreateMenupopup(window);
				ANMD.applyPlaneLink(menuLn, planeName);
			}

		}

	}
}
