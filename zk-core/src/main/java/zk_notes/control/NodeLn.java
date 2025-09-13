package zk_notes.control;

import mpc.html.EHtml5;
import mpu.X;
import mpu.core.ARG;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_form.notify.ZKI;
import zk_notes.AxnTheme;
import zk_notes.events.ANMF;
import zk_notes.factory.NFBe;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.NodeActionIO;
import zk_notes.node_srv.core.NodeEvalType;
import zk_os.sec.Sec;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKS;
import zk_page.events.ECtrl;

public class NodeLn extends Ln {

	public final HtmlBasedComponent parent;
	public final NodeDir nodeDir;
	public final Boolean isDeprecated;

	@Override
	public String getComName() {
		return nodeDir.nodeName();
	}

	public String getFormName() {
		return getComName();
	}

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

//	public NodeLn checkBusinessLogic_HPE(boolean... checkBusinessLogic_HPE) {
//		this.checkBusinessLogic_HPE = ARG.isDefNotEqFalse(checkBusinessLogic_HPE);
//		return this;
//	}

	private boolean dnd = true;

	public NodeLn dnd(boolean... dnd) {
		this.dnd = ARG.isDefNotEqFalse(dnd);
		return this;
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir) {
		super(nodeDir.nodeName());
		this.parent = parent;
		this.nodeDir = nodeDir;
		this.isDeprecated = false;
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir, Boolean markDeprecated) {
		super(getLinkAsHtml(nodeDir), true);
		this.parent = parent;
		this.nodeDir = nodeDir;
		this.isDeprecated = true;
	}

	private static @NotNull String getLinkAsHtml(NodeDir nodeDir) {
		return EHtml5.s.with(nodeDir.evalType(false, NodeEvalType.NODE).iconLight() + " " + nodeDir.nodeName());
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
		ANMF.applyForm(nodeLnMenu, nodeDir);

	}

	protected void initBeaviours() {

		if (checkBusinessLogic_HPE) {
			NFOpen.on_checkBusinessLogic_HPE(nodeDir.getPathFc());
		}

		NFOpen.openFormIdentitySinglyAsWin0_ifNotClosed(nodeDir, parentForNew);

		NodeEvalType nodeEvalType = nodeDir.evalType(false, NodeEvalType.NODE);

		if (!isDeprecated) {
			String label =
//			isDeprecated ?
//					nodeEvalType.iconLight() + " " + EHtml5.s.with(nodeDir.nodeName()) :
					nodeEvalType.iconLight() + " " + nodeDir.nodeName();
			setLabel(label);
		}

		if (Sec.isEditorAdminOwner()) {

//			addEventListener(Events.ON_MOUSE_OVER, e -> setLabel(wrappIcon(getLabel(), nodeEvalType)));
//			addEventListener(Events.ON_MOUSE_OUT, e -> setLabel(unwrapIcon(getLabel(), nodeEvalType)));

			onCLICK(e -> {

				ZKJS.setAction_ShakeEffect(this);

				MouseEvent mouseEvent = (MouseEvent) e;

				int keys = mouseEvent.getKeys();
				switch (keys) {
					case ECtrl.ZKE_2_CTRL_SHIFT_CODE:
					case ECtrl.ZKE_2_CTRL_ALT_CODE:
					case ECtrl.ZKE_CTRL_CODE:
						NodeActionIO.doEventAction_ActiveWeb(nodeDir, keys);
						break;
					case ECtrl.ZKE_ALT_CODE:
						String rslt = nodeDir.state().readFcDataOk(null);
						if (X.empty(rslt)) {
							ZKI.infoAfterPointer("Empty OK data", ZKI.Level.WARN);
						} else {
							ZKME.openEditorTextReadonly("OK data", rslt, true);
						}
						break;
					case ECtrl.ZKE_SHIFT_CODE:
						String rsltErr = nodeDir.state().readFcDataErr(null);
						if (X.empty(rsltErr)) {
							ZKI.infoAfterPointer("Empty ERR data", ZKI.Level.WARN);
						} else {
							ZKME.openEditorTextReadonly("ERR data", rsltErr, true);
						}
						break;

					default:

						NFOpen.openOrCloseToggle(nodeDir, ZKC.getFirstWindow());

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

		ZKS.BORDER_RADIUS(this, "9px 6px 6px 6px");

		double weigthZoom = getWeigthOpacity(nodeDir);
		if (weigthZoom != 1.0) {
			ZKS.OPACITY(this, weigthZoom);
		}

		applyState_BgColor(nodeDir.state());

		absolute();

//		applyState_RandomOrTopLeft(nodeDir.newStateCom());

		NFBe.applyState_RandomOrTopLeft_ForNode(this, nodeDir.newStateCom(false));

	}

//	private double getWeigthZoom() {
//		return getWeigthZoom(nodeDir);
//	}

	public static double getWeigthZoom(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.6;
		} else if (weightKb < 100) {
			return 0.9;
		}
		return 1.0;
	}

	public static double getWeigthOpacity(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.44;
		} else if (weightKb < 100) {
			return 0.69;
		}
		return 1.0;
	}
}
