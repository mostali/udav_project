package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import mpc.ui.UColorTheme;
import mpu.core.ARR;
import mpu.pare.Pare;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_com.base_ext.DoubleLn;
import zk_com.core.IZDnd;
import zk_os.AFC;
import zk_os.core.Sdn;
import zk_page.ZKC;
import zk_page.ZKCFinder;
import zk_page.core.IPageCom;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class PagesView extends Div0M implements IZDnd {

	private final String sd3;

	@Override
	protected Bt getCloseBt() {
		return null;//show't close button
	}

	@Override
	protected void init() {
		super.init();

		IZDnd.initDND(this);

		removeOldPlanes();

		List<Path> getAllPagenamesPaths = AFC.DIR_PAGES_LS_CLEAN(sd3);

		Pare<String, String> sdn = SpVM.get().sdn();
		for (Path pageDirPath : getAllPagenamesPaths) {
			String pagename = pageDirPath.getFileName().toString();
			Pare<String, String> pageSdn = sdn.clone(pagename);
			FormState formState = FormState.ofPageState(pageSdn);
			if (!formState.isAllow_byProp_SECE(false, true, true)) {
				continue;
			}

			PageLn child = new PageLn(this, pageDirPath);

			DoubleLn dblLink = new DoubleLn(child, RSPath.PAGE.toPlanPage(sd3, pagename), false);

			dblLink.draggablePersistensePageCom(pagename);

			dblLink.randomColor(UColorTheme.GREEN);

			dblLink.padding(10);
			dblLink.border("5px");

			appendChild(dblLink);


		}

	}


	public PagesView removeOldPlanes() {
		List<PagesView> allInPage = ZKCFinder.rootsByClass(PagesView.class, true, ARR.EMPTY_LIST);
		allInPage.stream().filter(l -> l.isDaemon(false)).forEach(l -> ZKC.removeMeReturnParentWithEffect(l));
		return this;
	}

	public static class PageLn extends Ln implements IPageCom {
		final PagesView plane;
		final String pageDirPath;
		final String pagename;

		@Override
		public String getPageComName() {
			return pagename;
		}

		public PageLn(PagesView sd3Plane, Path pageDirPath) {
			super(pageDirPath.getFileName().toString());

			this.pagename = pageDirPath.getFileName().toString();
			this.pageDirPath = pageDirPath.toString();

			this.plane = sd3Plane;

			onCLICK(e -> {
				Sdn sd3pn = Sdn.of(sd3Plane.sd3, pageDirPath.getFileName().toString());
				new ItemsQView(sd3pn).openInFirstWindow();
			});


//			Menupopup0 menuLn = getOrCreateMenupopup(sd3Plane);
//			menuLn.addMenuitem_Href("Open page", RSPath.PAGE.toPlanPage(sd3Plane.sd3, pagename), true);

		}


	}


}
