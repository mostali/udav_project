package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import org.zkoss.zk.ui.Component;
import zk_form.control.breadcrumbs.qview.QBreadDiv;
import zk_form.control.breadcrumbs.qview.QBreadLn;
import zk_notes.factory.NFOpen;
import zk_os.coms.AFC;
import zk_notes.node.NodeDir;
import zk_notes.control.NodeLn;
import zk_os.sec.UO;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public class ItemsQView extends QView {

	private final Sdn sdn;

	@Override
	protected Component newBreadDiv() {
		return new QBreadDiv(sdn);
	}

	@Override
	public String planeName() {
		return QBreadLn.toName(sdn);
	}

	@Override
	protected void init() {
		super.init();

		removeOldPlanes();

		Set<Path> formsDirs = AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn);

		for (Path formDir : formsDirs) {
			NodeDir nodeDir = NodeDir.ofDir(sdn, formDir);
			if (!UO.isAllowed_VIEW(nodeDir, false)) {
				if (L.isDebugEnabled()) {
					L.debug("NodeDir '%s' skip ( view is denied )");
				}
				continue;
			}

			NodeLn nodeLn = new NodeLn(this, nodeDir);

//			nodeLn.checkAndOpenIfStateOpened(false);
//			NFOpen.openFormInit(nodeDir);

			nodeLn.onQView();

			appendChild(nodeLn);
		}

	}

	public ItemsQView removeOldPlanes() {
//		List<PagePlane> allInPage = ZKComFinder.findAllInPage(PagePlane.class, true, ARR.EMPTY_LIST);
//		allInPage.stream().filter(l -> l.isDaemon(false)).forEach(l -> ZKC.removeMeReturnParentWithEffect(l));
		return this;
	}

//	public static class FormLn extends Ln {
//		final PagePlane pagePlane;
//
//		public FormLn(PagePlane pagePlane, Path nodeDirPath) {
//			super(nodeDirPath.getFileName().toString());
//			this.pagePlane = pagePlane;
//
//			block();
//			padding(10);
//			border("5px");
//			randomAbs(UColorTheme.BLUE);
//
//			FormState formState = FormState.ofFormDir(nodeDirPath, pagePlane.sdn);
////
//			NodeDir.NVT view = formState.nodeViewType(NodeDir.NVT.TEXT);
//
//			onCLICK(view.buildEventListener(pagePlane.sdn, nodeDirPath));
//
//
//		}
//	}

}
