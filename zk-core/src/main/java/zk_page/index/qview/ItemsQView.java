package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import org.zkoss.zk.ui.Component;
import zk_form.control.BreadDiv;
import zk_form.control.BreadLn;
import zk_page.node.NodeDir;
import zk_notes.control.NodeLn;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class ItemsQView extends QView {

	private final Sdn sdn;

	@Override
	protected Component newBreadDiv() {
		return new BreadDiv(sdn);
	}

	@Override
	public String planeName() {
		return BreadLn.toName(sdn);
	}

	@Override
	protected void init() {
		super.init();

		removeOldPlanes();

		List<Path> formsDirs = getAllForms(sdn);

		for (Path formDir : formsDirs) {
			NodeDir nodeDir = NodeDir.ofDir(formDir, sdn);
			if (!SecMan.isAllowedView(nodeDir)) {
				if (L.isDebugEnabled()) {
					L.debug("NodeDir '%s' skip ( view is disable )");
				}
				continue;
			}

			NodeLn nodeLn = new NodeLn(this, nodeDir);

			nodeLn.checkAndOpenIfStateOpened(false);

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
