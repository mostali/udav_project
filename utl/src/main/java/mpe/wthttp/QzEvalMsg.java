package mpe.wthttp;

import mpc.rfl.RFL;
import mpu.X;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.nio.file.Path;

public class QzEvalMsg extends CallMsg {

	public static final String QZEVAL = "qzeval";
	public static final String NODEID_SRC = "node_src";
//	public static final String NODEID_DST = "node_dst";
	public final Class<?> jobClassName;
	public final String nodeId;

	public static boolean isValidKeyFirstLine(String msg) {
		return STR.startsWith(msg, QZEVAL + ":", true);
	}

	public QzEvalMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsg)) {
			jobClassName = null;
			nodeId = null;
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, QZEVAL + ":")) {
			addError("Except first line with starts %s", QZEVAL + ":");
		}

		Class jobClass = null;
		String nodeId = null;

		String[] three = SPLIT.argsBy(line0, ":");
		switch (three.length) {
			case 1:
			case 2:
				addError("Except pattern [%s:class:node]", QZEVAL);
				break;
			case 3:
				if (!QZEVAL.equals(three[0].trim())) {
					addError("Except pattern [%s:class:node]", QZEVAL);
					break;
				}
				jobClass = RFL.clazz(three[1].trim(), null);
				if (jobClass == null) {
					addError("Except pattern [%s:class:node]. Set jobClassName", QZEVAL);
					break;
				}
				//if (!Job.class.isAssignableFrom(jobClassName)) {
				//addError("JobClassName '%s' not assignable from Job.class", jobClassNameStr);
				//}

				nodeId = three[2].trim();
				if (X.empty(nodeId)) {
					addError("Except pattern [%s:class:nodeId]. Set nodeId", QZEVAL);
					break;
				}
		}

		this.jobClassName = jobClass;
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return "QzEvalMsg{" +
				"msg='" + fullMsg + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", class=" + jobClassName +
				", nodeId=" + nodeId +
				'}';
	}

	public static QzEvalMsg of(Path file) {
		return of(RW.readContent(file));
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
