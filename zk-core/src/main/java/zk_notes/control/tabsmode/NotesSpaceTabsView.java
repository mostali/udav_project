package zk_notes.control.tabsmode;

import lombok.RequiredArgsConstructor;
import mpu.X;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_notes.events.ANMF;
import zk_notes.factory.NodeCom;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;
import zk_page.ZKC;
import zk_page.ZKColor;
import zk_page.ZKPage;
import zk_page.ZKS;

import java.util.List;

@RequiredArgsConstructor
public class NotesSpaceTabsView extends Div0 {
	final List<NodeLn> nodeComs;

	LeftSide leftSide = null;
	MainSide mainSide = null;

	@Override
	protected void init() {
		super.init();

		absolute().top_left(100, 30);

		appendChild(leftSide = new LeftSide(nodeComs));
		appendChild(mainSide = new MainSide());

	}

	@RequiredArgsConstructor
	public static class MainSide extends Div0 {

		@Override
		protected void init() {
			super.init();
//			relative();

//			ZKS.MARGIN_LEFT(this, 20.0);

			inlineBlock();

			StdHeadLib.PRETTYFY_JS.addToPage();

			if (X.notEmpty(mainCom().nodeComs)) {
				String nodeName = mainCom().nodeComs.get(0).nodeDir.nodeName();
				initNodeTab(nodeName);
			}

		}

		public void initNodeTab(String nodeName) {
			clear();

			NodeCom nodeCom = NodeCom.of(NodeDir.ofNodeName(sdnAny(), nodeName));

			nodeCom.buildAndAppendChildIn(this);

		}

		private NotesSpaceTabsView mainCom() {
			return (NotesSpaceTabsView) getParent();
		}
	}

	@RequiredArgsConstructor
	public static class LeftSide extends Div0 {
		final List<NodeLn> nodeLns;

		@Override
		protected void init() {
			super.init();

//			relative();
//			float0(true);

			ZKS.MARGIN_RIGHT(this, "20px");
			ZKS.BORDER_RIGHT(this, "3px solid silver");
			inlineBlock();
			height(100.0);
			width(300);
//			padding(10);
//			borderSilver();

			for (NodeLn nodeLnSrc : nodeLns) {

				String nodeName = nodeLnSrc.nodeDir.nodeName();

				Ln newLn = new Ln(nodeName);
//				newLn = nodeLnSrc;
				appendChild(newLn);

				newLn.block();

				Ln finalNewLn = newLn;
				newLn.onCLICK(e -> {
					finalNewLn.attr_put("activetab", true);
					mainSide().initNodeTab(nodeName);
					finalNewLn.color(ZKColor.RED.nextBgColor_asHtmlProp());
				});

				Menupopup0 menu = newLn.getOrCreateMenupopup(ZKC.getFirstWindow());
				ANMF.applyNolCom(menu, nodeLnSrc.nodeDir);

//				appendChild(menu);

			}
		}

		private MainSide mainSide() {
			NotesSpaceTabsView parent = (NotesSpaceTabsView) LeftSide.this.getParent();
			MainSide mainSide0 = parent.mainSide;
			return mainSide0;
		}

	}
}
