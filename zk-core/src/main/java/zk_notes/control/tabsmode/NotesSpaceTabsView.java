package zk_notes.control.tabsmode;

import lombok.RequiredArgsConstructor;
import mpu.X;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_notes.control.NodeCom;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;
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

		appendChild(leftSide = new LeftSide(nodeComs));
		appendChild(mainSide = new MainSide());

	}

	@RequiredArgsConstructor
	public static class MainSide extends Div0 {

		@Override
		protected void init() {
			super.init();
			ZKS.MARGIN_LEFT(this, 20.0);
			IHeadRsrc rsrc = StdHeadLib.PRETTYFY_JS.toRsrc();
			ZKPage.renderHeadRsrc(rsrc);

			if (X.notEmpty(mainCom().nodeComs)) {
				String nodeName = mainCom().nodeComs.get(0).nodeDir.nodeName();
				initNodeTab(nodeName);
			}

		}

		public void initNodeTab(String nodeName) {
			clear();

			NodeCom nodeCom = NodeCom.of(NodeDir.ofNodeName(sdn(), nodeName));

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

			absolute().top_left(0, 0);
//			float0(true);
			height(100.0);
			width(20.0);
			borderSilver();

			for (NodeLn nodeLnSrc : nodeLns) {

				String nodeName = nodeLnSrc.nodeDir.nodeName();

				Ln newLn = new Ln(nodeName);
				appendChild(newLn);

				newLn.block();

				newLn.onCLICK(e -> {
					newLn.attr_put("activetab", true);
					mainSide().initNodeTab(nodeName);
					newLn.color(ZKColor.REDS.nextBgColor_asHtmlProp());
				});


			}
		}

		private MainSide mainSide() {
			NotesSpaceTabsView parent = (NotesSpaceTabsView) LeftSide.this.getParent();
			MainSide mainSide0 = parent.mainSide;
			return mainSide0;
		}

	}
}
