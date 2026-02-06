package zk_notes.events;

import mpc.str.sym.SYMJ;
import mpe.NT;
import mpu.X;
import mpu.core.ARR;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Cap0;
import zk_com.base_ctr.Span0;
import zk_com.win.Win0;
import zk_form.notify.ZKI;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.node_srv.NodeCapsCom;
import zk_notes.node_srv.PlayContainer;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_os.sec.SecMan;
import zk_os.sec.SecManRMM;
import zk_page.ZKS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//AppNotesMenuCaption
public class ANMCap {

	public static Cap0 applyWinTbxCap(NodeDir node, Window comInWin) {

		ObjState state = node.state();

		Cap0 cap = null;

		NodeCapsCom.FormEditableName editableValue = null;

		boolean isEditorAdminOwner = SecMan.isAllowedEditPlane();

		if (isEditorAdminOwner) {
			cap = (Cap0) Win0.getCap0OrCreate(comInWin);
			cap.getChildren().clear();
			editableValue = new NodeCapsCom.FormEditableName(node);

			if (node.stateCom().isNeedOpenIfHide()) {
				editableValue.addEventListener(Events.ON_DOUBLE_CLICK, w -> ZKS.WC_DISPLAY(comInWin, "block"));
			}

			List<HtmlBasedComponent> links = getLinksFromData(state);

			links.add(editableValue);
//			links.add(0, element);
			cap.appendChild(Span0.of((List) links));
		}

		if (isEditorAdminOwner) {
			cap.appendChild(Ln.uploadTo(SYMJ.UPLOAD, node.toPath()));
		}

		//
		//  CALL's

		if (isEditorAdminOwner) {

			NodeEvalType nodeEvalType = node.evalType(false, null);

			if (nodeEvalType != null) {

				PlayContainer playC = PlayContainer.toPlayContainer(PlayContainer.PlayLn.of(node));

				cap.getChildren().add(0, playC);

			}

			Supplier<String> fc1 = () -> state.readFcDataOk(null);
			Supplier<String> fc2 = () -> state.readFcDataErr(null);
			if (X.notEmpty(fc1.get())) {
				Ln ln = (Ln) new Ln(SYMJ.EYE).onCLICK(e -> {
					String msg = fc1.get();
					if (X.notEmpty(msg)) {
						ZKI.infoEditorDark(msg);
					}
				});
				ln.title("Show last OK response");
				cap.appendChild(ln);
			}
			if (X.notEmpty(fc2.get())) {
				Ln ln = (Ln) new Ln(SYMJ.WARN).onCLICK(e -> {
					String msg = fc2.get();
					if (X.notEmpty(msg)) {
						ZKI.infoEditorDark(msg);
					}
				});
				ln.title("Show last ERR response");
				cap.appendChild(ln);
			}

		}

		//
		// SwapTgPostLn

		if (isEditorAdminOwner) {

			String line0 = state.nodeLine(0, "");

			if (line0 != null && line0.startsWith(NT.TG.HOST_URL()) && UST.URL(line0, null) != null || NodeCapsCom.SwapTgPostLn.isScriptPattern(line0)) {
				cap = (Cap0) Win0.getCap0OrCreate(comInWin);
				cap.appendChild(new NodeCapsCom.SwapTgPostLn(node));
			}

		}

		//
		// FormSizeTbx

		if (isEditorAdminOwner) {
			if (node.fields().get_SIZE(null) != null) {
				cap = (Cap0) Win0.getCap0OrCreate(comInWin);
				cap.appendChild(new NodeCapsCom.FormSizeTbx(node));
			}
		}

		if (editableValue != null) {
			Cap0 finalCap = cap;
			editableValue.enableDisappearComs(() -> finalCap.getComsWithChilds());
		}

		return cap;
	}

	private static @NotNull ArrayList<HtmlBasedComponent> getLinksFromData(ObjState state) {
		List<String> nodeData = state.readFcDataAsLines(ARR.EMPTY_LIST);

		List<String> urls = nodeData.stream().filter(u -> UST.URL(u, null) != null).collect(Collectors.toList());

		ArrayList<HtmlBasedComponent> links = new ArrayList();
		for (int i = 0; i < urls.size(); i++) {
			if (i == 3) {
				break;
			}
			Ln caption = Ln.ofEmojBlank(urls.get(i), i == 0 ? "" : (i + 1) + "");
			if (i > 0) {
				links.add(Xml.NBSP());
			}
			links.add(caption);
		}
		return links;
	}


	//
	//
	//

}
