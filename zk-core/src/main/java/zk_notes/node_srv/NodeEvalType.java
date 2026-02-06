package zk_notes.node_srv;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.RES;
import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import mpe.call_msg.*;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionT;
import zk_notes.node.NodeDir;
import mpe.call_msg.injector.TrackMap;
import zk_notes.node_state.ObjState;

import java.util.Map;

public enum NodeEvalType {
	NODE, HTTP, KAFKA, SQL, QZEVAL, JARTASK, SENDMSG, MVEL, GROOVY, PYTHON, SHTASK, IIPROMPT;

	public static NodeEvalType valueOf(NodeDir nodeDir, boolean strictValid, NodeEvalType... defRq) {
		return valueOf(nodeDir.state(), strictValid, defRq);
	}

	public static NodeEvalType valueOf(ObjState state, boolean strictValid, NodeEvalType... defRq) {

		String line0 = state.nodeLine(0, null);

		if (X.empty(line0)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("CallEntity not found from empty first line of node '%s'", state.objName()), defRq);
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

		// IS II Promt?
		if (strictValid ? IICallMsg.isValid(data) : IICallMsg.isValidQk(data)) {
			return NodeEvalType.IIPROMPT;
		}

		// IS QzEval?
		if (strictValid ? QzEvalMsg.isValid(data) : QzEvalMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.QZEVAL;
		}

		// IS SendMsg?
		if (strictValid ? SendCallMsg.isValid(data) : SendCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.SENDMSG;
		}

		// IS JarTask?
		if (strictValid ? JarCallMsg.isValid(data) : JarCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.JARTASK;
		}
		// IS GROOVY ?
		if (strictValid ? GroovyCallMsg.isValid(data) : GroovyCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.GROOVY;
		}
		// IS GROOVY ?
		if (strictValid ? MvelCallMsg.isValid(data) : MvelCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.MVEL;
		}
		// IS PYTHON ?
		if (strictValid ? PyCallMsg.isValid(data) : PyCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.PYTHON;
		}
		// IS SH ?
		if (strictValid ? BashCallMsg.isValid(data) : BashCallMsg.isValidKeyFirstLine(data)) {
			return NodeEvalType.SHTASK;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("CallEntity not found from node '%s'", state.objName()), defRq);
	}

	public String title() {
		switch (this) {
			case HTTP:
				return "Send HTTP call";
			case SQL:
				return "Send SQL call";
			case JARTASK:
				return "Execute JAR Task";
			case GROOVY:
				return "Execute GROOVY script";
			case MVEL:
				return "Execute Java script";
			case PYTHON:
				return "Execute PYTHON script";
			case SHTASK:
				return "Execute Shell script";
			case KAFKA:
				return "Send KAFKA call";
			case SENDMSG:
				return "Send message";
			case QZEVAL:
				return "Run QUARTZ all task's";
			case IIPROMPT:
				return "II Promt";
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public String titleWithIcon() {
		return icon() + " " + title();
	}

	public String iconLight() {
		switch (this) {
			case NODE:
				return SYMJ.FILE3_WL;
			case JARTASK:
				return SYMJ.JAVA_JAR_LIGHT;
			case HTTP:
//				return SYMJ.THINK2;
//				return SYMJ.STAR_SIMPLE2;
				return SYMJ.STAR_COM;
			case SENDMSG:
				return SYMJ.EMAIL2;
			case QZEVAL:

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
			case PYTHON:
			case MVEL:
			case GROOVY:
			case SHTASK:
				return SYMJ.ONOFF_ON_PLAY;
			case KAFKA:
				return SYMJ.ROCKET;
			case QZEVAL:
				return SYMJ.TIME_R_CLOCK;
			case IIPROMPT:
				return SYMJ.EYE;
			case NODE:
				return SYMJ.FILE2;
//				return SYMJ.FILE_HTML;
//				return SYMJ.THINK_BLACK;
//				return SYMJ.MONEY_STATS;
			case SENDMSG:
				return SYMJ.EMAIL;
			default:
				return null;
		}

	}


	@SneakyThrows
	public String loadDefResData(boolean new_about, String... defRq) {
		String resName = (new_about ? "/_com/_demo_note_new/" : "/_com/_demo_note_about/") + name().toLowerCase() + ".props";
		return RES.of(NodeEvalType.class, resName).cat_(defRq);
	}

	public String[] toColor() {
		switch (this) {
			case IIPROMPT:
				return ColorTheme.LBLUE;
			case SENDMSG:
			case KAFKA:
			case HTTP:
				return ColorTheme.GREEN;

			case QZEVAL:
				return ColorTheme.BLUE;
			case PYTHON:
			case MVEL:
			case GROOVY:
				return ColorTheme.ORANGE;
			case SHTASK:
				return ColorTheme.BLUE;
			case JARTASK:
				return ColorTheme.YELLOW;
			case NODE:
				return ColorTheme.GRAY;
			case SQL:
				return ColorTheme.BLACK;

			default:
				return ColorTheme.WHITE;


		}
	}

	public String shortName() {
		switch (this) {
			case SENDMSG:
				return "smsg";
			case QZEVAL:
				return "qz";
			case JARTASK:
				return "jar";
			case NODE:
				return "note";
			default:
				return name();

		}
	}

	public String shortNameRu() {

		switch (this) {
			case NODE:
				return "ТЕКСТОВЫЕ ДАННЫЕ";
			case SENDMSG:
				return "ОТПРАВКА СООБЩЕНИЯ";
			case HTTP:
				return "HTTP ЗАПРОС";
			case SHTASK:
				return "BASH СКРИПТ";
			case JARTASK:
				return "JAR EXECUTE";
			case GROOVY:
				return "GROOVY СКРИПТ";
			case MVEL:
				return "MVEL JAVA СКРИПТ";
			case PYTHON:
				return "PYTHON СКРИПТ";
			case SQL:
				return "SQL ЗАПРОС";
			case KAFKA:
				return "KAFKA PRODUCER/CONSUMER";
			case QZEVAL:
				return "ТАЙМЕР/CRON";
			case IIPROMPT:
				return "ИИ ПРОМТ";
			default:
				return name();

		}
	}

	//
	//
	//
	public FunctionT<NodeDir, Object> evalIn() {
		return NodeActionIO.in(this, null);
	}

	public FunctionT<NodeDir, Object> evalIn(TrackMap.TrackId context) {
		return NodeActionIO.in(this, context);
	}


	public FunctionT evalOut(NodeDir nodeDir, Map... webContext) {
		return NodeActionIO.out(this, nodeDir, webContext);
	}

}
