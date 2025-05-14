package zk_notes.control;

import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.core.IZWin;
import zk_form.events.DefAction;
import zk_form.head.IHeadCom;
import zk_notes.events.ANMC;
import zk_notes.AppNotesTheme;
import zk_notes.coms.NoteTbxm;
import zk_os.sec.Sec;
import zk_page.ZKC;
import zk_page.ZKPage;
import zk_page.ZKS;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;

import java.util.function.Supplier;

public class NodeCom extends NodeWinBase {

	public NodeCom(NodeDir nodeDir) {
		super(nodeDir);
	}

	public static NodeCom of(NodeDir nodeDir) {
		return new NodeCom(nodeDir);
	}

	private IZWin comText;
	private Window comTextWin;

	public IZWin com() {
		return IT.NN(comText, "before call buildAndAppendChildIn");
	}

	public Window comWin() {
		return IT.NN(comTextWin, "before call buildAndAppendChildIn");
	}

	Boolean isEditorAdminOwner;

	public Pare<HtmlBasedComponent, Window> buildAndAppendChildIn(Component... parent) {

		this.isEditorAdminOwner = Sec.isEditorAdminOwner();

		Supplier<Component> getParent = () -> ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow();

		NodeDir.NVT nvt = _nvt != null ? _nvt : nodeDir.nvt(NodeDir.NVT.TEXT);
//		IZWin comText = NodeFactoryCom.createCom_Text(nodeDir);
		this.comText = NodeFactoryCom.createForm_Text(nodeDir, nvt);

		if (comText instanceof IHeadCom) {
			ZKPage.renderHeadRsrc((IHeadCom) comText);
		}

		if (!nvt.isWindowMode()) {
			getParent.get().appendChild((Component) comText);
			return Pare.of((HtmlBasedComponent) comText, null);
		}
		if (isEditorAdminOwner) {
			comText._sizable()._closable();
		}
		if (_mode != null) {
			boolean fixedOrTrue = nodeDir.state().fields().get_FIXED_orTrue();
			if (fixedOrTrue) {
				comText._modal(_mode);
			}
		}

		this.comTextWin = comText._title(getOrCreateTitle())._showInWindow(getParent.get());

		ZKS.PADDING_WIN(comTextWin, 10, 0);

		ANMC.applyWinTbxCap(nodeDir, comTextWin);

		if (_opts != null && _opts.isOnQView) {
			ZKS.ZINDEX(comTextWin, AppNotesTheme.ZI_QVIEW_WIN);
		}

		//		((HtmlBasedComponent)izWin).setAttribute(NodeID.NAME, nodeDir.nodeId().string());

		applyPersistClose();

		applyPropsAndBehaviours();

		return Pare.of(null, comTextWin);
	}

	private void applyPropsAndBehaviours() {

		IZWin comText = com();

		FormState state = nodeDir.state();

		HtmlBasedComponent comTextNoteTbxm_OrComTextWin = comText instanceof NoteTbxm ? (HtmlBasedComponent) comText : comTextWin;

		FormState.apply_TopLeft_WidthHeigth_Bgc_Titles(comTextWin, comTextNoteTbxm_OrComTextWin, state, !absMode);

		if (absMode && isEditorAdminOwner) {
			FormState.addEventListenerMoveAndResize(comTextWin, comTextNoteTbxm_OrComTextWin, state);
		}

	}

	private void applyPersistClose() {
		if (_opts == null || !_opts.persistState) {
			return;
		}
		DefAction closeAction = NodeFactory.getDefCloseHideAction(nodeDir);
		comTextWin.addEventListener(Events.ON_CLOSE, (SerializableEventListener<Event>) event -> closeAction.onDefAction(event));
	}

	private String getOrCreateTitle() {
		if (_title != null) {
			return _title;
		}
		String title = nodeDir.nodeName() + " ";
		if (_opts == null) {
			return title;
		}
		boolean showFN = _opts.showFilename;
		if (showFN) {
			title += SYMJ.ARROW_RIGHT_SPEC + nodeDir.getPathFormFc();
		} else {
			String title0 = nodeDir.state().get_TITLE(null);
			if (X.notEmpty(title0)) {
				title += SYMJ.ARROW_RIGHT_SPEC + title0;
			}
		}

		return _title = title;

	}


	;
}
