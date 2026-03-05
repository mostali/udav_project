package zk_notes.control;

import mpc.html.EHtml5;
import mpc.str.sym.SYMJ;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.StdType;
import mpe.str.CN;
import mpu.core.ARG;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.events.ANMF;
import zk_notes.factory.NFForm;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVM;
import zk_os.sec.SecMan;
import zk_os.sec.UO;
import zk_page.*;

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
		this(parent, nodeDir, getLinkAsHtml(nodeDir));
	}

	public NodeLn(HtmlBasedComponent parent, NodeDir nodeDir, String html) {
		super(html, true);
		this.parent = parent;
		this.nodeDir = nodeDir;
		this.isDeprecated = true;
	}

	private static @NotNull String getLinkAsHtml(NodeDir nodeDir) {
		return EHtml5.s.with(nodeDir.evalType(StdType.NODE).stdProps().iconLight() + " " + nodeDir.nodeName());
	}

	@Override
	protected void init() {
		super.init();

		initBeaviours();

		initContextMenu();

	}

	protected void initContextMenu() {
		if (SecMan.isAnonimUnsafe()) {
			return;
		}
		Menupopup0 nodeLnMenu = getOrCreateMenupopup(parent);
		ANMF.applyFormSec(nodeLnMenu, nodeDir);

	}

//	private String unwrapIcon(String label, INodeType nodeEvalType) {
//		return !label.endsWith(nodeEvalType.stdProps().icon()) ? label : STR.removeEndStringQk(label, nodeEvalType.stdProps().icon());
//	}
//
//	private String wrappIcon(String label, INodeType nodeEvalType) {
//		return label.endsWith(nodeEvalType.stdProps().icon()) ? label : label + " " + STR.removeEndStringQk(getLabel(), nodeEvalType.stdProps().icon());
//	}


	protected void initBeaviours() {

		if (checkBusinessLogic_HPE) {
			NFForm.on_checkBusinessLogic_HPE(nodeDir.getPathFc());
		}

		NFForm.openFormInit(nodeDir, parentForNew);

		INodeType nodeEvalType = nodeDir.evalType(StdType.NODE);

		String nodeName = nodeDir.nodeName();

		if (!isDeprecated) {
			boolean hasLink = nodeDir.stateCom().get(CN.HREF, null) != null;
			String label = nodeName;
			if (!hasLink) {
				label = nodeEvalType.stdProps().iconLight() + " " + label;
			}
			setLabel(label);
		}

		NVM nvm = nodeDir.nvm_first_auto_cached();
		if (nvm != null) {
			setLabel(SYMJ.FILE_VIDEO + " " + nodeName);
		}

//			addEventListener(Events.ON_MOUSE_OVER, e -> setLabel(wrappIcon(getLabel(), nodeEvalType)));
//			addEventListener(Events.ON_MOUSE_OUT, e -> setLabel(unwrapIcon(getLabel(), nodeEvalType)));

		onCLICK(e -> NodeLnAction.doNodeLnAction(e, nodeDir));

		if (dnd) {
			boolean allowedEdit = UO.isAllowed_EDIT(nodeDir);
			if (allowedEdit) {
				ZKS.DRAG_DROP(this);
			}
		}
	}

}
