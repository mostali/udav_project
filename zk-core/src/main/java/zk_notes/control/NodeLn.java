package zk_notes.control;

import mpc.html.EHtml5;
import mpu.X;
import mpu.core.ARG;
import mpu.str.STR;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_form.notify.ZKI;
import zk_notes.coms.NoteTbxm;
import zk_notes.events.ANM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.AxnTheme;
import zk_notes.factory.NFOpen;
import zk_notes.factory.NFView;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_srv.core.NodeActionIO;
import zk_os.sec.Sec;
import zk_notes.node.NodeDir;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKS;
import zk_page.events.ZKE;

public class NodeLn extends Ln {

	public final HtmlBasedComponent parent;
	public final NodeDir nodeDir;

	@Override
	public String getComName() {
		return nodeDir.nodeName();
	}

	public String getFormName() {
		return getComName();
	}

//	private boolean checkAndOpen_IfOpened = true;
//
//	public NodeLn checkAndOpenIfStateOpened(boolean... defRq) {
//		this.checkAndOpen_IfOpened = ARG.isDefNotEqFalse(defRq);
//		return this;
//	}

	private HtmlBasedComponent parentForNew = null;

	public NodeLn parentForDependForm(HtmlBasedComponent parentForNew) {
		this.parentForNew = parentForNew;
		return this;
	}

	private boolean onQView = false;

	public NodeLn onQView(boolean... onQView) {
		this.onQView = ARG.isDefNotEqFalse(onQView);
		return this;
	}

	public Boolean opened = null;

	private boolean checkBusinessLogic_HPE = true;

	public NodeLn checkBusinessLogic_HPE(boolean... checkBusinessLogic_HPE) {
		this.checkBusinessLogic_HPE = ARG.isDefNotEqFalse(checkBusinessLogic_HPE);
		return this;
	}

	private boolean dnd = true;

	public NodeLn dnd(boolean... dnd) {
		this.dnd = ARG.isDefNotEqFalse(dnd);
		return this;
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir) {
		super(nodeDir.nodeName());
		this.parent = parent;
		this.nodeDir = nodeDir;
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir, boolean markDeprecated) {
		super(EHtml5.s.with(nodeDir.nodeName()), true);
		this.parent = parent;
		this.nodeDir = nodeDir;
	}

	@Override
	protected void init() {
		super.init();

		initStyle();

		initBeaviours();

		initContextMenu();

//		borderRed();
	}

	protected void initContextMenu() {
		Menupopup0 nodeLnMenu = getOrCreateMenupopup(parent);
		ANM.applyFormLink(nodeLnMenu, nodeDir);

	}

	protected void initBeaviours() {

		if (checkBusinessLogic_HPE) {
			//
			//host profile contract
			NFOpen.on_checkBusinessLogic_HPE(nodeDir.getPathFc());
		}

//		if (nodeDir.nvt() != NVT.DIR) {
			NFOpen.openFormIdentitySinglyAsWin0_ifNotClosed(nodeDir, parentForNew);
//		}

		NodeEvalType nodeEvalType = nodeDir.evalType(false, NodeEvalType.NODE);

		String label = nodeEvalType.iconLight() + " " + nodeDir.nodeName();
		setLabel(label);

		if (Sec.isEditorAdminOwner()) {

//			addEventListener(Events.ON_MOUSE_OVER, e -> setLabel(wrappIcon(getLabel(), nodeEvalType)));
//			addEventListener(Events.ON_MOUSE_OUT, e -> setLabel(unwrapIcon(getLabel(), nodeEvalType)));

			onCLICK(e -> {

				ZKJS.setAction_ShakeEffect(this);

				MouseEvent mouseEvent = (MouseEvent) e;

				int keys = mouseEvent.getKeys();
				switch (keys) {
					case ZKE.ZKE_2_CTRL_SHIFT_CODE:
					case ZKE.ZKE_2_CTRL_ALT_CODE:
					case ZKE.ZKE_CTRL_CODE:
						NodeActionIO.doEventAction_ActiveWeb(nodeDir, keys);
						break;
					case ZKE.ZKE_ALT_CODE:
						String rslt = nodeDir.state().readFcDataOk(null);
						if (X.empty(rslt)) {
							ZKI.infoAfterPointer("Empty OK data", ZKI.Level.WARN);
						} else {
							ZKME.openEditorTextReadonly("OK data", rslt, true);
						}
						break;
					case ZKE.ZKE_SHIFT_CODE:
						String rsltErr = nodeDir.state().readFcDataErr(null);
						if (X.empty(rsltErr)) {
							ZKI.infoAfterPointer("Empty ERR data", ZKI.Level.WARN);
						} else {
							ZKME.openEditorTextReadonly("ERR data", rsltErr, true);
						}
						break;

					default:

						NoteTbxm existed = NFView.openIfNotFoundNodeForm(nodeDir, ZKC.getFirstWindow());
						if (existed != null) {
							existed.detachNodeCom();
						}

				}

			});
		}

		if (dnd && Sec.isEditorAdminOwner()) {
			ZKS.DRAG_DROP(this);
		}
	}

	private String unwrapIcon(String label, NodeEvalType nodeEvalType) {
		return !label.endsWith(nodeEvalType.icon()) ? label : STR.removeEndString(label, nodeEvalType.icon());
	}

	private String wrappIcon(String label, NodeEvalType nodeEvalType) {
		return label.endsWith(nodeEvalType.icon()) ? label : label + " " + STR.removeEndString(getLabel(), nodeEvalType.icon());
	}

	protected void initStyle() {

		decoration_none().padding("0 5px");

		font_size(AxnTheme.FONT_SIZE_APP_LINK);

		ZKS.PADDING(this, "9px 6px");
//		ZKS.OPACITY(this, 0.96);
		ZKS.BORDER_RADIUS(this, "9px 6px 6px 6px");

		double weigthZoom = getWeigthOpacity(nodeDir);
		if (weigthZoom != 1.0) {
//			ZKS.ZOOM(this, weigthZoom);
			ZKS.OPACITY(this, weigthZoom);
		}


		applyState_BgColor(nodeDir.state());
//		applyState_RandomOrTopLeft(nodeDir.nodeName());

//		boolean fixedOrTrue = nodeDir.state().fields().get_FIXED_orTrue();
//		if (fixedOrTrue) {
		absolute();
		applyState_RandomOrTopLeft(nodeDir.newStateCom());
//		}


	}

	private double getWeigthZoom() {
		return getWeigthZoom(nodeDir);
	}

	public static double getWeigthZoom(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.6;
		} else if (weightKb < 100) {
			return 0.9;
		}
//		else if (weightKb < 500) {
//			return 0.8;
//		}
		return 1.0;
	}

	public static double getWeigthOpacity(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.44;
		} else if (weightKb < 100) {
			return 0.69;
		}
//		else if (weightKb < 500) {
//			return 0.8;
//		}
		return 1.0;
	}
}
