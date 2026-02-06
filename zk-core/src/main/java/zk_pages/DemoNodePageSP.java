package zk_pages;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpe.core.P;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.IPerPage;
import zk_notes.factory.NFOpen;
import zk_notes.node.core.NVT;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_page.*;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

import java.util.HashSet;
import java.util.Set;

@PageRoute(pagename = "demo-notes", role = ROLE.ANONIM)
public class DemoNodePageSP extends PageSP implements IPerPage {

	public DemoNodePageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

//	public static void initNewAndAppend(Window window) {
//
//		BoolEvent html = new BoolEvent() {
//			@Override
//			protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
//				super.doEvent(event, data, coor);
//				X.nothing();
//			}
//		};
//
//		html.setClass("boolCom");
//		window.appendChild(html);
//
//	}

	@SneakyThrows
	public void buildPageImpl() {

		ZkPageInitHeads.initPageHeadLibs(window);

		ZKS.BGCOLOR_WIN(window, "rgba(0,0,0,0.0)", "rgba(0,0,0,0.0)");

//		ZKS.BGIMAGE(window, "url(_bg_img/bg-g-vert-dnk.png)", "contain", "top", "repeat");
//		initNewAndAppend(window);

		Sdn sdn = spVM().sdn();

		Bt bt = (Bt) new Bt("Reset").onCLICK(e -> {
			UFS.RM.deleteDir(Sdn.getPageDir(sdn));
			ZKR.restartPage();
		}).title("Recovery default form state's");
		ZKS.CENTER(bt);
		appendChild((Component) bt);

		FunctionV1<Component> fillContainer = (parent) -> {
			//TODO wth


			initBefore();
			initContainerCom();

		};

		fillContainer.apply(window);

		if (!getDemoNodeOpened(null, NodeEvalType.NODE, sdn, false).key().existPropsFile()) {
			fillContainer.apply(window);
		}


	}

	private void initBefore() {
//		Div0 separator = Div0.separator(130, ZKColor.GRAY.nextColor(), 1, "Общая информация\n", "class=\"head\"", "style=\"padding-top:50px\"");
//		window.appendChild(separator);
//		window.appendChild(Xml.ofXml("<p class=\"centerH\">В общем случае любая заметка это просто текст</p>"));

//		String section = EHtml5.article.with(data);
//		window.appendChild(Xml.ofXml(section));

	}

	private void initContainerCom() {


		Div0 separator = Div0.separator(130, ZKColor.YELLOW.nextColor(), 1, "Demo Node Collection\n", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		Sdn sdn = Sdn.get();

		Set alreadyAdded = new HashSet<>();

		FunctionV1<NodeEvalType> demoComBuilder = new FunctionV1<NodeEvalType>() {
			@Override
			public void apply(NodeEvalType nodeType) {

				alreadyAdded.add(nodeType);

				Div0 sepH3 = Div0.separator(30, null, 2, nodeType.shortNameRu(), "class=\"head\"");
				ZKS.MARGIN_BOTTOM(sepH3, 20);

				window.appendChild(sepH3);

				window.appendChild(Xml.HR());

//				Xml h = Xml.H(2, nodeType.shortNameRu());
//				ZKS.CENTER(h);
//				ZKS.FONT_FAMILY(h,"Noto Sans Mono\", monospace;");
//				Div0.separator()
//				window.appendChild(h);

				Pare<ObjState, Window> demoNoteOpened = getDemoNodeOpened(null, nodeType, sdn, true);

				if (demoNoteOpened.val() == null) {
					P.warnBig("Return null window:" + nodeType);
					return;
				}

				ZKS.WC_PADDING(demoNoteOpened.val(), 0, 0);
				ZKS.WC_PADDING_TOP(demoNoteOpened.val(), 0, 100);

//				ZKS.MARGIN_TOP_WIN(demoNoteOpened.val(), null, 100);

				addAboutCom(nodeType, demoNoteOpened, sdn);

				window.appendChild(Div0.separator(130, null));
			}
		};

		demoComBuilder.apply(NodeEvalType.NODE);
		demoComBuilder.apply(NodeEvalType.HTTP);
		demoComBuilder.apply(NodeEvalType.KAFKA);
		demoComBuilder.apply(NodeEvalType.SENDMSG);
		demoComBuilder.apply(NodeEvalType.QZEVAL);

		separator = Div0.separator(100, ZKColor.YELLOW.nextColorSlow(), 2, "Поддержка Java", "class=\"head\"", "style=\"padding-top:30px\"");
		window.appendChild(separator);

		demoComBuilder.apply(NodeEvalType.JARTASK);
		demoComBuilder.apply(NodeEvalType.MVEL);

		separator = Div0.separator(130, ZKColor.ORANGE.nextColorSlow(), 2, "Поддержка скриптов", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		demoComBuilder.apply(NodeEvalType.GROOVY);
		demoComBuilder.apply(NodeEvalType.PYTHON);
		demoComBuilder.apply(NodeEvalType.SHTASK);
		demoComBuilder.apply(NodeEvalType.SQL);


		//
		//

//		separator = Div0.separator(130, ZKColor.LBLUE.nextColorSlow(), 2, "Deprecated", "class=\"head\"", "style=\"padding-top:50px\"");
//		window.appendChild(separator);

//		demoComBuilder.apply(NodeEvalType.QZTASK);


		//
		//


		separator = Div0.separator(130, ZKColor.BLUE.nextColorSlow(), 2, "FINISH", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		//
		// Other
		//

		NodeEvalType[] values = NodeEvalType.values();

		for (NodeEvalType nodeType : values) {
			if (alreadyAdded.contains(nodeType)) {
				continue;
			}

			demoComBuilder.apply(nodeType);

		}
	}

	private static void addAboutCom(NodeEvalType value, Pare<ObjState, Window> parentWindow, Sdn sdn) {

		ObjState parentState = parentWindow.key();

		Window parentWin = parentWindow.val();

//		NodeEvalType nodeEvalType = parentState.nodeDir().evalType(false, NodeEvalType.NODE);

		Pare<ObjState, Window> demoAboute = getDemoNodeOpened("about", value, sdn, true, parentWin);

		parentWin.doEmbedded();
		ZKS.CENTER(parentWin);
		ZKS.OPACITY(parentWin, 0.9);

		Window aboutOpenedWin = demoAboute.val();
		if (aboutOpenedWin == null) {
			return;
		}

		aboutOpenedWin.doEmbedded();

		demoAboute.key().fields().set_VIEW(NVT.HTML_WIN);
		demoAboute.key().fields().set_STATE(NVT.HTML_WIN);

		ZKS.INLINE_BLOCK(aboutOpenedWin);
		ZKS.MARGIN_LEFT(aboutOpenedWin, 180);
		ZKS.BGCOLOR(aboutOpenedWin, value.toColor()[0]);
		ZKS.WC_PADDING(aboutOpenedWin, 0, 0);

	}

	public static Pare<ObjState, Window> getDemoNodeOpened(String newAboutType, NodeEvalType value, Sdn sdn, boolean open, Component... parent) {

		String noteName = newAboutType == null ? value.name() : "-" + value.name() + "-";// + "-DEMO";

		ObjState formState = AppStateFactory.forForm(sdn, noteName);
		if (!open) {
			return Pare.of(formState, null);
		}
		Window win;
		if (formState.existPropsFile()) {
			win = NFOpen.openFormRequired(noteName, sdn, parent);
		} else {

			String dataNote = value.loadDefResData(true, null);
			if ("about".equals(newAboutType)) {
				dataNote = value.loadDefResData(false, null);
			}

			if (dataNote == null) {
				P.warnBig(X.f("DemoNote '%s' not found", value));
				return Pare.of(formState, null);
			}

			win = NodeFileTransferMan.AddNewForm.addNewFormAndOpen(noteName, dataNote).val();

		}
		return Pare.of(formState, win);
	}

	public void appendChild(Component child) {
		window.appendChild(child);
	}
}
