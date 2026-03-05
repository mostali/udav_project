package zk_pages;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpc.fs.fd.RES;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.StdType;
import mpe.core.P;
import mpe.img.EColor;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.IPerPage;
import zk_notes.factory.NFForm;
import zk_notes.factory.NFNew;
import zk_notes.node.core.NVT;
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

		if (!getDemoNodeOpened(null, StdType.NODE, sdn, false).key().existPropsFile()) {
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


		Div0 separator = Div0.separator(130, EColor.YELLOW.nextColor(), 1, "Demo Node Collection\n", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		Sdn sdn = Sdn.get();

		Set alreadyAdded = new HashSet<>();

		FunctionV1<INodeType> demoComBuilder = new FunctionV1<INodeType>() {
			@Override
			public void apply(INodeType nodeType) {

				alreadyAdded.add(nodeType);

				Div0 sepH3 = Div0.separator(30, null, 2, nodeType.stdProps().shortNameRu(), "class=\"head\"");
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

		demoComBuilder.apply(StdType.NODE);
		demoComBuilder.apply(StdType.HTTP);
		demoComBuilder.apply(StdType.KAFKA);
		demoComBuilder.apply(StdType.SENDMSG);
		demoComBuilder.apply(StdType.QZEVAL);

		separator = Div0.separator(100, EColor.YELLOW.nextColor(), 2, "Поддержка Java", "class=\"head\"", "style=\"padding-top:30px\"");
		window.appendChild(separator);

		demoComBuilder.apply(StdType.JARTASK);
		demoComBuilder.apply(StdType.MVEL);

		separator = Div0.separator(130, EColor.ORANGE.nextColor(), 2, "Поддержка скриптов", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		demoComBuilder.apply(StdType.GROOVY);
		demoComBuilder.apply(StdType.PYTHON);
		demoComBuilder.apply(StdType.SHTASK);
		demoComBuilder.apply(StdType.SQL);


		//
		//

//		separator = Div0.separator(130, ZKColor.LBLUE.nextColor(), 2, "Deprecated", "class=\"head\"", "style=\"padding-top:50px\"");
//		window.appendChild(separator);

//		demoComBuilder.apply(NodeEvalType.QZTASK);


		//
		//


		separator = Div0.separator(130, EColor.BLUE.nextColor(), 2, "FINISH", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		//
		// Other
		//

		INodeType[] values = StdType.values();

		for (INodeType nodeType : values) {
			if (alreadyAdded.contains(nodeType)) {
				continue;
			}

			demoComBuilder.apply(nodeType);

		}
	}

	private static void addAboutCom(INodeType value, Pare<ObjState, Window> parentWindow, Sdn sdn) {

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
		ZKS.BGCOLOR(aboutOpenedWin, value.stdProps().toColor()[0]);
		ZKS.WC_PADDING(aboutOpenedWin, 0, 0);

	}

	@SneakyThrows
	static String loadDefResData(String nameLC, boolean new_about, String... defRq) {
		String resName = (new_about ? "/_com/_demo_note_new/" : "/_com/_demo_note_about/") + nameLC + ".props";
		return RES.of(StdType.class, resName).cat_(defRq);
	}

	public static Pare<ObjState, Window> getDemoNodeOpened(String newAboutType, INodeType value, Sdn sdn, boolean open, Component... parent) {

		String noteName = newAboutType == null ? value.stdTypeUC() : "-" + value.stdTypeUC() + "-";// + "-DEMO";

		ObjState formState = AppStateFactory.forForm(sdn, noteName);
		if (!open) {
			return Pare.of(formState, null);
		}
		Window win;
		if (formState.existPropsFile()) {
			win = NFForm.openFormRequired(sdn, noteName, parent);
		} else {


			String nameLC = value.stdTypeUC().toLowerCase();
			String dataNote = loadDefResData(nameLC, true, null);
			if ("about".equals(newAboutType)) {
				dataNote = loadDefResData(nameLC, false, null);
			}

			if (dataNote == null) {
				P.warnBig(X.f("DemoNote '%s' not found", value));
				return Pare.of(formState, null);
			}

			win = NFNew.openNewRewrite(sdn, noteName, dataNote).val();

		}
		return Pare.of(formState, win);
	}

	public void appendChild(Component child) {
		window.appendChild(child);
	}
}
