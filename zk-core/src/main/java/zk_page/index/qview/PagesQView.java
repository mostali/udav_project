package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import mpc.ui.UColorTheme;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.control.BreadDiv;
import zk_notes.coms.DoubleLn;
import zk_os.AFC;
import zk_page.ZKC;
import zk_page.ZKCF;
import zk_os.core.Sdn;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class PagesQView extends QView {

	private final String sd3;

	@Override
	protected Component newBreadDiv() {
		return new BreadDiv(sd3);
	}

	@Override
	public String planeName() {
		return sd3;
	}

	@Override
	protected void init() {
		super.init();

		removeOldPlanes();

		List<Path> getAllPagenamesPaths = AFC.DIR_PAGES_LS_CLEAN(sd3);

		Pare<String, String> sdn = SpVM.get().sdn();
		for (Path pageNamePath : getAllPagenamesPaths) {
			Pare<String, String> pageSdn = sdn.clone(pageNamePath.getFileName().toString());
			FormState formState = FormState.ofPageState(pageSdn);
			if (!formState.isAllow_byProp_SECE(false, true, true)) {
				continue;
			}

//			PageLinkDiv formLn = new PageLinkDiv(this, pageNamePath);

			PageLn child = new PageLn(this, pageNamePath);

			DoubleLn dblLink = new DoubleLn(child, RSPath.PAGE.toPlanPage(planeName(), pageNamePath.getFileName().toString()), false).randomAbsPosition();

			dblLink.randomAbs(UColorTheme.GREEN);
			dblLink.padding(10);
			dblLink.border("5px");

			appendChild(dblLink);
		}

	}


	public PagesQView removeOldPlanes() {
		List<PagesQView> allInPage = ZKCF.rootsByClass(PagesQView.class, true, ARR.EMPTY_LIST);
		allInPage.stream().filter(l -> l.isDaemon(false)).forEach(l -> ZKC.removeMeReturnParentWithEffect(l));
		return this;
	}

	public static class PageLn extends Ln {
		final PagesQView plane;
		final String pagenamePath;

		public PageLn(PagesQView sd3Plane, Path pagenamePath) {
			super(pagenamePath.getFileName().toString());

			this.pagenamePath = pagenamePath.toString();

			this.plane = sd3Plane;

			onCLICK(e -> {
				Sdn sd3pn = Sdn.of(sd3Plane.sd3, pagenamePath.getFileName().toString());
				new ItemsQView(sd3pn).openInFirstWindow();
			});

//			Menupopup0 menuLn = getOrCreateMenupopup(sd3Plane);
//			menuLn.addMenuitem_Href("Open page", RSPath.PAGE.toPlanPage(sd3Plane.sd3, pagename), true);

		}

	}


}
