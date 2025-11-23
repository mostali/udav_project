package zk_notes.factory;

import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Cap0;
import zk_com.core.IZComFadeIO;
import zk_com.core.IZWin;
import zk_form.events.DefAction;
import zk_form.head.IHeadCom;
import zk_notes.control.NodeLn;
import zk_notes.events.ANMCap;
import zk_notes.AxnTheme;
import zk_notes.coms.NoteTbxm;
import zk_notes.node.core.NVT;
import zk_os.AppZosProps;
import zk_os.sec.SecMan;
import zk_os.sec.SecManRMM;
import zk_page.*;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

import java.util.List;
import java.util.function.Supplier;

public class NodeCom extends NodeWinBase {

	public NodeCom(NodeDir nodeDir) {
		super(nodeDir);
	}

	public static @NotNull NodeCom ofCurrent(String name, boolean... createIfnOtExists) {
		NodeDir shNode = NodeDir.ofCurrentPage(name);
		if (ARG.isDef(createIfnOtExists)) {
			shNode.createIfNotExist();
		}
//		shNode.state().set("bg-color", "#aec6cf");
		NodeCom nodeCom = of(shNode);
		return nodeCom;
	}

	public static NodeCom of(NodeDir nodeDir) {
		return new NodeCom(nodeDir);
	}

	private IZWin comForm;
	private Window comWin;


	public IZWin comDataText() {
		return IT.NN(comForm, "before call buildAndAppendChildIn");
	}

//	private boolean skipParent;
//
//	public NodeCom withSkipParent(boolean... skipPArent) {
//		this.skipParent = ARG.isDefEqFalse(skipPArent);
//		return this;
//	}

	public Window comWin() {
		return IT.NN(comWin, "before call buildAndAppendChildIn");
	}

	Boolean isEditorAdminOwner;

	public Pare<HtmlBasedComponent, Window> buildAndAppendChildIn(Component... parent) {

		ObjState stateCom = nodeDir.stateCom();

		this.isEditorAdminOwner = SecMan.isAllowedEditPlane();

		Supplier<Component> getParentSupplier = () -> ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow();

		NVT nvt = _nvt != null ? _nvt : nodeDir.nvt(NVT.TEXT);

		this.comForm = NFCreate.createForm(nodeDir, nvt);

		if (comForm instanceof IHeadCom) {
			ZKPage.renderHeadRsrc((IHeadCom) comForm);
		}

		Component addToParent = getParentSupplier.get();
		if (!nvt.isWindowMode()) {
			addToParent.appendChild((Component) comForm);
			return Pare.of((HtmlBasedComponent) comForm, null);
		}
		if (isEditorAdminOwner) {
			comForm._sizable()._closable();
		}
		if (_mode != null) {
			ObjState stateForm = nodeDir.state();
			ObjState.Position pos = stateForm.fields().get_POSITION(ObjState.Position.ABS);
			switch (pos) {
				case ABS:
//				case REL:
					comForm._modal(_mode);

			}
		}

		IZWin izWin = comForm._title(getOrCreateTitle());

		this.comWin = izWin._showInWindow(addToParent);

		if (AppZosProps.APD_UI_EFFECTS_ENABLE.getValueOrDefault()) {
			IZComFadeIO.addEffectInImpl(comWin);
		}

		ZKS.WC_PADDING(comWin, 10, 0);

		ANMCap.applyWinTbxCap(nodeDir, comWin);

		if (stateCom.isToggleBodyBehaviour()) {
			Cap0 cap0 = (Cap0) comWin.getCaption();
			if (cap0 != null) {
				ZKCB_OpenHide.addBeahaviourOn_byMaxHeight(cap0, (HtmlBasedComponent) comForm, true);
			}
		}

		if (_opts != null && _opts.isOnQView) {
			ZKS.ZINDEX(comWin, AxnTheme.ZI_QVIEW_WIN);
		}

		applyPersistCloseBehaviour();

		applyPropsAndBehaviours();

		Boolean isVisibleBody = stateCom.isVisibleBody();
		if (!isVisibleBody) {
			if (stateCom.isToggleBodyBehaviour()) {
				ZKS.addSTYLE((HtmlBasedComponent) comDataText(), "max-height:" + 0);
			} else {
				ZKS.WC_DISPLAY(comWin, "none");
			}
		}


		return Pare.of(null, comWin);
	}

	private void applyPropsAndBehaviours() {

		IZWin comText = comDataText();

		ObjState state = nodeDir.state();

		HtmlBasedComponent comTextNoteTbxm_OrComTextWin = comText instanceof NoteTbxm ? (HtmlBasedComponent) comText : comWin;

		NFStyle.apply_TopLeft_WidthHeigth_Bgc_Titles(comWin, comTextNoteTbxm_OrComTextWin, state, !absMode);

		if (absMode && isEditorAdminOwner) {
			NFBe.addEventListenerPersistMoveAndResize(comWin, comTextNoteTbxm_OrComTextWin, state);
		}


	}

	private void applyPersistCloseBehaviour() {
		if (_opts == null || !_opts.persistState) {
			return;
		}
		DefAction closeAction = NFOpen.getDefCloseHideAction(nodeDir);
//		Ln close = (Ln) Ln.of("X");
//		comTextWin.getCaption().appendChild(close);
//		close.addEventListener(Events.ON_CLICK, (SerializableEventListener<Event>) event -> {
//			comTextWin.onClose();
//			closeAction.onDefAction(event);
//		});

		if (true) {
			comWin.addEventListener(Events.ON_CLOSE, (SerializableEventListener<Event>) event -> {
				closeAction.onDefAction(event);
				if (false) {
					List<Component> allNodeCom = ZKNFinder.findAllNodeCom(false, false);
					allNodeCom.forEach(c -> {
						if (c instanceof NodeLn) {
							NodeLn nodeLn = (NodeLn) c;
							nodeLn.opened = false;
						}
					});
				}
			});
		}

//		comTextWin.addEventListener(Events.ON_CANCEL, (SerializableEventListener<Event>) event -> closeAction.onDefAction(event));
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
			title += SYMJ.ARROW_RIGHT_SPEC + nodeDir.getPath_FormFc_Data();
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
