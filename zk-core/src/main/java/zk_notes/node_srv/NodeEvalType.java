package zk_notes.node_srv;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.RES;
import mpc.rfl.RFL;
import mpc.str.sym.SYMJ;
import mpe.wthttp.*;
import mpu.X;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_page.ZKM_Editor;
import zk_page.events.ZKE;

public enum NodeEvalType {
	NODE, HTTP, KAFKA, SQL, QZTASK, QZEVAL, JARTASK, GROOVY;

	public static NodeEvalType valueOf(NodeDir nodeDir, boolean strictValid, NodeEvalType... defRq) {
		return valueOf(nodeDir.state(), strictValid, defRq);
	}

	public static NodeEvalType valueOf(FormState state, boolean strictValid, NodeEvalType... defRq) {

		String line0 = state.nodeLine(0, null);

		if (X.empty(line0)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("CallEntity not found from empty first line of node '%s'", state.formName()), defRq);
		}

		String data = state.nodeDataCached();

		if (SqlCallMsg.isValid(data)) {
			return NodeEvalType.SQL;
		}

		// IS KAFKA?
		if (KafkaCallMsg.isValid(data)) {
			return NodeEvalType.KAFKA;
		}

		// IS HTTP?
		if (strictValid ? HttpCallMsg.isValid(data) : HttpCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.HTTP;
		}

		// IS QzTask?
		if (strictValid ? QzTaskMsg.isValid(data) : QzTaskMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.QZTASK;
		}

		// IS QzEval?
		if (strictValid ? QzEvalMsg.isValid(data) : QzEvalMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.QZEVAL;
		}

		// IS JarTask?
		if (strictValid ? JarCallMsg.isValid(data) : JarCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.JARTASK;
		}

		// IS GROOVY ?
		if (strictValid ? GroovyCallMsg.isValid(data) : GroovyCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.GROOVY;
		}
//		if (ACN.GROOVY.equals(state.fields().get_EXE(null))) {
//			return NodeCallType.GROOVY;
//		}

		return ARG.toDefThrow(() -> new RequiredRuntimeException("CallEntity not found from node '%s'", state.formName()), defRq);
	}


	public Object doEventAction(NodeDir nodeDir, Component pushHolderCom, Object... defRq) {
		return doEventAction(nodeDir, pushHolderCom, 0, defRq);
	}

	public Object doEventAction(NodeDir nodeDir, Component pushHolderCom, Integer keys, Object... defRq) {
		switch (this) {

			case NODE:
				String title = icon() + " " + nodeDir.nodeName();
				//if (nodeDir.nvt(null) == NodeDir.NVT.WYSIWYG) {
				switch (keys) {
					case ZKE.ZKE_2_CTRL_ALT_CODE:
						return ZKM_Editor.openEditorHTML(title, nodeDir.getPathFormFc());
					default:
						return ZKM_Editor.openEditorTEXT(title, nodeDir.getPathFormFc());
				}

			case HTTP:
				return NodeCapsCom.HttpPlayLn.doEventAction(nodeDir);

			case JARTASK:
				return NodeCapsCom.JartaskCtrlLn.doEventAction(nodeDir, pushHolderCom);

			case SQL:
				return NodeCapsCom.SqlPlayLn.doEventAction(nodeDir);

			case GROOVY:
				return NodeCapsCom.GroovyPlayLn.doEventAction(nodeDir);

			case KAFKA:
				return NodeCapsCom.KafkaCtrlLn.doEventAction(nodeDir, pushHolderCom);

			case QZTASK:
				return NodeCapsCom.QzCtrlTaskLn.doEventAction(nodeDir);

			case QZEVAL:
				return NodeCapsCom.QzCtrlEvalLn.doEventAction(nodeDir);

			default:
				return ARG.toDefThrowMsg(() -> X.f("Event Action '%s' not found", this), defRq);
		}
	}

	public void applyLn(Ln ln, NodeDir node, Component pushHolderCom) {
		ln.setLabel(icon());
		ln.title(title());
		ln.onCLICK(e -> {
			doEventAction(node, pushHolderCom);
		});
	}

	private String title() {
		switch (this) {
			case HTTP:
				return "Send HTTP call";
			case SQL:
				return "Send SQL call";
			case JARTASK:
				return "Execute JAR Task";
			case GROOVY:
				return "Execute GROOVY script";
			case KAFKA:
				return "Send KAFKA call";
			case QZTASK:
			case QZEVAL:
				return "Run QUARTZ all task's";
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public String titleWithIcon() {
		return icon() + " " + title();
	}

	public String iconLight() {
		switch (this) {
			case JARTASK:
				return SYMJ.JAVA_JAR_LIGHT;
			case HTTP:
//				return SYMJ.THINK2;
//				return SYMJ.STAR_SIMPLE2;
				return SYMJ.STAR_SIMPLE_ROUND;
			case QZTASK:
			case QZEVAL:
				return SYMJ.TIME_L_ALARM;
			default:
				return icon();
		}
	}

	public String icon() {
		switch (this) {
			case HTTP:
				return SYMJ.JET;
			case SQL:
				return SYMJ.TARGET;
			case JARTASK:
				return SYMJ.JAVA_JAR;
			case GROOVY:
				return SYMJ.ONOFF_ON_PLAY;
			case KAFKA:
				return SYMJ.ROCKET;
			case QZTASK:
			case QZEVAL:
				return SYMJ.TIME_R_CLOCK;
			case NODE:
				return SYMJ.FILE3_WL;
//				return SYMJ.FILE_HTML;
//				return SYMJ.THINK_BLACK;
//				return SYMJ.MONEY_STATS;
			default:
				return null;
		}

	}

	public Component toEvalCom(NodeDir node) {
		Class<? extends Component> nodeCallCapLinkClass = NodeCapsCom.getNodeCallCapLinkClass(this);
		return RFL.inst(nodeCallCapLinkClass, NodeDir.class, node);
	}

	public void applyMenu(Menupopup0 menu, NodeDir node) {
		menu.addMenuItem(titleWithIcon(), e -> doEventAction(node, menu));
	}

	@SneakyThrows
	public String loadDefResData(String... defRq) {
		String resName = "/_com/_demo_eval_coms/" + name().toLowerCase() + ".props";
		return RES.of(NodeEvalType.class, resName).cat_(defRq);
	}
}
