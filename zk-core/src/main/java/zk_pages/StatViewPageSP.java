//package zk_pages;
//
//import lombok.SneakyThrows;
//import mpc.fs.UFS;
//import mpe.core.P;
//import mpu.X;
//import mpu.func.FunctionV1;
//import mpu.pare.Pare;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Window;
//import zk_com.base.Bt;
//import zk_com.base.Xml;
//import zk_com.base_ctr.Div0;
//import zk_com.sun_editor.IPerPage;
//import zk_notes.control.NodeFactory;
//import zk_notes.node.NodeDir;
//import zk_notes.node_srv.NodeEvalType;
//import zk_notes.node_srv.fsman.NodeFileTransferMan;
//import zk_notes.node_state.FormState;
//import zk_os.core.Sdn;
//import zk_os.sec.ROLE;
//import zk_page.*;
//import zk_page.core.PageRoute;
//import zk_page.core.PageSP;
//import zk_page.core.SpVM;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@PageRoute(pagename = "statrel", role = ROLE.OWNER)
//public class StatViewPageSP extends PageSP implements IPerPage {
//
//	public StatViewPageSP(Window window, SpVM spVM) {
//		super(window, spVM);
//	}
//
//	//	public static void initNewAndAppend(Window window) {
////
////		BoolEvent html = new BoolEvent() {
////			@Override
////			protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
////				super.doEvent(event, data, coor);
////				X.nothing();
////			}
////		};
////
////		html.setClass("boolCom");
////		window.appendChild(html);
////
////	}
//	public void appendChild(Component child) {
//		window.appendChild(child);
//	}
//
//	@SneakyThrows
//	public void buildPageImpl() {
//
//		ZkPageAuto.initPageHeadLibs(window);
//
//		ZKS.BGCOLOR_WIN(window, "rgba(0,0,0,0.0)", "rgba(0,0,0,0.0)");
//
////		ZKS.BGIMAGE(window, "url(_bg_img/bg-g-vert-dnk.png)", "contain", "top", "repeat");
////		initNewAndAppend(window);
//
//		Sdn sdn = spVM().sdn0();
//
//		Bt bt = (Bt) new Bt("Reset").onCLICK(e -> {
//			UFS.RM.deleteDir(Sdn.getPageDir(sdn));
//			ZKR.restartPage();
//		}).title("Recovery default form state's");
//		ZKS.CENTER(bt);
//		appendChild((Component) bt);
//
////		FunctionV1<Component> fillContainer = (parent) -> {
////			//TODO wth
////
//		initBefore();
////
//		initContainerCom();
////
////		};
////
////		fillContainer.apply(window);
////
////		if (!getDemoNodeOpened(null, NodeEvalType.NODE, sdn, false).key().existPropsFile()) {
////			fillContainer.apply(window);
////		}
//
//
//	}
//
//	private void initBefore() {
////		Div0 separator = Div0.separator(130, ZKColor.GRAY.nextColor(), 1, "Общая информация\n", "class=\"head\"", "style=\"padding-top:50px\"");
////		window.appendChild(separator);
////		window.appendChild(Xml.ofXml("<p class=\"centerH\">В общем случае любая заметка это просто текст</p>"));
//
////		String section = EHtml5.article.with(data);
////		window.appendChild(Xml.ofXml(section));
//
//	}
//
//	private void initContainerCom() {
//
//
//		Div0 separator = Div0.separator(130, ZKColor.YELLOW.nextColor(), 1, "Demo Node Collection\n", "class=\"head\"", "style=\"padding-top:50px\"");
//		window.appendChild(separator);
//
//		Sdn sdn = Sdn.getRq();
//
//		Set alreadyAdded = new HashSet<>();
//
//		FunctionV1<NodeEvalType> demoComBuilder = new FunctionV1<NodeEvalType>() {
//			@Override
//			public void apply(NodeEvalType nodeType) {
//
//				alreadyAdded.add(nodeType);
//
//				Div0 sepH3 = Div0.separator(30, null, 2, nodeType.shortNameRu(), "class=\"head\"");
//				ZKS.MARGIN_BOTTOM(sepH3, 20);
//
//				window.appendChild(sepH3);
//
//				window.appendChild(Xml.HR());
//
////				NodeDir n1;
////				NodeDir n2;
////				window.appendChild(DNode.of(n1, n2));
//				//
//				window.appendChild(Div0.separator(130, null));
//			}
//		};
//
//		demoComBuilder.apply(NodeEvalType.NODE);
//
//
//	}
//
//
//	private static void addAboutCom(NodeEvalType value, Pare<FormState, Window> parentWindow, Sdn sdn) {
//
//		FormState parentState = parentWindow.key();
//
//		Window parentWin = parentWindow.val();
//
////		NodeEvalType nodeEvalType = parentState.nodeDir().evalType(false, NodeEvalType.NODE);
//
//		Pare<FormState, Window> demoAboute = getDemoNodeOpened("about", value, sdn, true, parentWin);
//
//		parentWin.doEmbedded();
//		ZKS.CENTER(parentWin);
//		ZKS.OPACITY(parentWin, 0.9);
//
//		Window aboutOpenedWin = demoAboute.val();
//		if (aboutOpenedWin == null) {
//			return;
//		}
//
//		aboutOpenedWin.doEmbedded();
//
//		demoAboute.key().fields().set_VIEW(NodeDir.NVT.HTML_WIN);
//		demoAboute.key().fields().set_STATE(NodeDir.NVT.HTML_WIN);
//
//		ZKS.INLINE_BLOCK(aboutOpenedWin);
//		ZKS.MARGIN_LEFT(aboutOpenedWin, 180);
//		ZKS.BGCOLOR(aboutOpenedWin, value.toColor()[0]);
//		ZKS.PADDING_WIN(aboutOpenedWin, 0, 0);
//
//	}
//
//	public static Pare<FormState, Window> getDemoNodeOpened(String newAboutType, NodeEvalType value, Sdn sdn, boolean open, Component... parent) {
//
//		String noteName = newAboutType == null ? value.name() : "-" + value.name() + "-";// + "-DEMO";
//
//		FormState formState = FormState.ofFormName_OrCreate(sdn, noteName);
//		if (!open) {
//			return Pare.of(formState, null);
//		}
//		Window win;
//		if (formState.existPropsFile()) {
//			win = NodeFactory.openNoteWin_unstateless(noteName, sdn, parent);
//		} else {
//
//			String dataNote = value.loadDefResData(true, null);
//			if ("about".equals(newAboutType)) {
//				dataNote = value.loadDefResData(false, null);
//			}
//
//			if (dataNote == null) {
//				P.warnBig(X.f("DemoNote '%s' not found", value));
//				return Pare.of(formState, null);
//			}
//
//			win = NodeFileTransferMan.AddNewForm.addNewFormAndOpen(noteName, dataNote).val();
//
//		}
//		return Pare.of(formState, win);
//	}
//
//}
