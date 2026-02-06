package mpe.call_msg;

import mpc.rfl.RFL;
import mpu.X;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.nio.file.Path;

public class QzEvalMsg extends CallMsg {

	public static final String KEY = "qzeval";
	public static final String NODEID_SRC = "node_src";
//	public static final String NODEID_DST = "node_dst";
	public final Class<?> jobClassName;
	public final String nodeId;

	public static boolean isValidKeyFirstLine(String msg) {
		return STR.startsWith(msg, KEY + ":", true);
	}

	public QzEvalMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsgHeadersAndBody())) {
			jobClassName = null;
			nodeId = null;
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, KEY + ":")) {
			addError("Except first line with starts %s", KEY + ":");
		}

		Class jobClass = null;
		String nodeId = null;

		String[] three = SPLIT.argsBy(line0, ":");
		switch (three.length) {
			case 1:
			case 2:
				addError("Except pattern [%s:class:node]", KEY);
				break;
			case 3:
				if (!KEY.equals(three[0].trim())) {
					addError("Except pattern [%s:class:node]", KEY);
					break;
				}
				jobClass = RFL.clazz(three[1].trim(), null);
				if (jobClass == null) {
					addError("Except pattern [%s:class:node]. Set jobClassName", KEY);
					break;
				}
				//if (!Job.class.isAssignableFrom(jobClassName)) {
				//addError("JobClassName '%s' not assignable from Job.class", jobClassNameStr);
				//}

				nodeId = three[2].trim();
				if (X.empty(nodeId)) {
					addError("Except pattern [%s:class:nodeId]. Set nodeId", KEY);
					break;
				}
		}

		this.jobClassName = jobClass;
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return "QzEvalMsg{" +
				"msg='" + fileData + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", class=" + jobClassName +
				", nodeId=" + nodeId +
				'}';
	}

	public static QzEvalMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static QzEvalMsg of(String msg) {
		return (QzEvalMsg) ofQk(msg).throwIsErr();
	}

	public static QzEvalMsg ofQk(String msg) {
		return new QzEvalMsg(msg);
	}

	public static boolean isValid(String data) {
		return QzEvalMsg.of(data).isValid();
	}

}
