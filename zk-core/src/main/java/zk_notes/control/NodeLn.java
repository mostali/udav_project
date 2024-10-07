package zk_notes.control;

import mpc.html.EHtml5;
import mpu.core.ARG;
import zk_notes.ANM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.AppNotesTheme;
import zk_os.sec.Sec;
import zk_page.node.NodeDir;
import zk_page.ZKS;

public class NodeLn extends Ln {

	public final HtmlBasedComponent parent;
	public final NodeDir nodeDir;

	@Override
	public String getComStateName() {
		return nodeDir.nodeName();
	}

	private boolean checkAndOpen_IfOpened = true;

	public NodeLn checkAndOpenIfStateOpened(boolean... checkAndOpenIfOpened) {
		this.checkAndOpen_IfOpened = ARG.isDefNotEqFalse(checkAndOpenIfOpened);
		return this;
	}

	private boolean persistState = true;

	public NodeLn persistState(boolean... persistState) {
		this.persistState = ARG.isDefNotEqFalse(persistState);
		return this;
	}

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
//		EHtml5.s.wrap(nodeDir.nodeName()), true
//		appendChild(Xml.ofXml(nodeDir.nodeName(), EHtml5.s));
		this.parent = parent;
		this.nodeDir = nodeDir;
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir, boolean markDeprecated) {
		super(EHtml5.s.wrap(nodeDir.nodeName()), true);
		this.parent = parent;
		this.nodeDir = nodeDir;
	}

	@Override
	protected void init() {
		super.init();

		initStyle();

		initBeaviours();

		initContextMenu();

	}

	protected void initContextMenu() {
		Menupopup0 nodeLnMenu = getOrCreateMenupopup(parent);
		ANM.applyNotesPageLink(nodeLnMenu, nodeDir);

	}

	protected void initBeaviours() {

		if (checkBusinessLogic_HPE) {
			//
			//host profile contract
			NodeFactory.on_checkBusinessLogic_HPE(nodeDir.getTargetPathDataFc());
		}

		if (checkAndOpen_IfOpened) {
			NodeFactory.openFormIdentitySinglyAsWin0_ifNotClosed(nodeDir, persistState);
		}

		if (Sec.isEditorAdminOwner()) {
			onCLICK(e -> NodeFactory.openFormIdentitySinglyAsWin0(nodeDir, persistState));
		}

		if (dnd && Sec.isEditorAdminOwner()) {
			ZKS.DRAG_DROP(this);
		}
	}

	protected void initStyle() {
		decoration_none().padding("0 5px").absolute();

		font_size(AppNotesTheme.APP_LINK_FONT_SIZE);

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
		applyState_RandomOrTopLeft(nodeDir.newStateCom());

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
