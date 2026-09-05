//package zk_pages.zznsi_pages;
//
//import lombok.RequiredArgsConstructor;
//import zk_com.base.Cb;
//import zk_com.base_ctr.Div0;
//import zk_form.head.IHeadRsrc;
//import zk_form.head.StdHeadLib;
//import zk_page.ZKPage;
//import zk_page.ZKS;
//
//@RequiredArgsConstructor
//public class CicdView extends Div0 {
////	final List<NodeLn> nodeComs;
//
//	LeftSide leftSide = null;
//	TargetSide mainSide = null;
//
//	//bus entity
//	public enum BE {
//		ROLES, ENUMS, QA, MODEL, FUNC, PIPES
//	}
//
//	@Override
//	protected void init() {
//		super.init();
//
//		absolute().top_left(100, 30);
//
//		appendChild(leftSide = new LeftSide());
//		appendChild(mainSide = new TargetSide());
//
//	}
//
//	@RequiredArgsConstructor
//	public static class LeftSide extends Div0 {
////		List<BeCb> beCbs;
//
//		@Override
//		protected void init() {
//			super.init();
//
////			relative();
////			float0(true);
//
//			ZKS.MARGIN_RIGHT(this, "20px");
//			ZKS.BORDER_RIGHT(this, "3px solid silver");
//			inlineBlock();
//			height(100.0);
//			width(300);
////			padding(10);
////			borderSilver();
//
////			beCbs = new ArrayList<>();
//
//			for (BE value : BE.values()) {
//				BeCb beCb = new BeCb(value);
//				appendChild(beCb);
////				beCbs.add(beCb);
//			}
//
////			for (NodeLn nodeLnSrc : nodeLns) {
////
////				String nodeName = nodeLnSrc.nodeDir.nodeName();
////
////				Ln newLn = new Ln(nodeName);
//////				newLn = nodeLnSrc;
////				appendChild(newLn);
////
////				newLn.block();
////
////				Ln finalNewLn = newLn;
////				newLn.onCLICK(e -> {
////					finalNewLn.attr_put("activetab", true);
////					mainSide().initNodeTab(nodeName);
////					finalNewLn.color(ZKColor.REDS.nextBgColor_asHtmlProp());
////				});
////
////				Menupopup0 menu = newLn.getOrCreateMenupopup(ZKC.getFirstWindow());
////				AN0M.applyNolCom(menu, nodeLnSrc.nodeDir);
////
//////				appendChild(menu);
////
////			}
//		}
//
//
//		private TargetSide mainSide() {
//			CicdView parent = (CicdView) LeftSide.this.getParent();
//			TargetSide mainSide0 = parent.mainSide;
//			return mainSide0;
//		}
//
//		private static class BeCb extends Cb {
//			public BeCb(BE value) {
//				super(value.name());
//				block();
//				setChecked(false);
//			}
//		}
//
//		//		}
//	}
//
//	@RequiredArgsConstructor
//	public static class TargetSide extends Div0 {
//
//		@Override
//		protected void init() {
//			super.init();
////			relative();
//
////			ZKS.MARGIN_LEFT(this, 20.0);
//
//			inlineBlock();
//
//			IHeadRsrc rsrc = StdHeadLib.PRETTYFY_JS.toRsrc();
//			ZKPage.renderHeadRsrc(rsrc);
//
////			if (X.notEmpty(mainCom().nodeComs)) {
////				String nodeName = mainCom().nodeComs.get(0).nodeDir.nodeName();
////				initNodeTab(nodeName);
////			}
//
//		}
//
////		public void initNodeTab(String nodeName) {
////			clear();
////
////			NodeCom nodeCom = NodeCom.of(NodeDir.ofNodeName(sdn(), nodeName));
////
////			nodeCom.buildAndAppendChildIn(this);
////
////		}
////
////		private CicdView mainCom() {
////			return (CicdView) getParent();
////		}
//	}
//}
