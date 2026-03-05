package mpe.cmsg.std;

import mpc.rfl.RFL;
import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;

import java.nio.file.Path;

public class QzCallMsg extends CallMsg {

	public static final String KEY = "qzeval";
	public static final String LINE0 = "qzeval:";

	public static final String NODEID_SRC = "node_src";
	//	public static final String NODEID_DST = "node_dst";
	public final Class<?> jobClassName;
	public final String nodeId;

	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

	public QzCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			jobClassName = null;
			nodeId = null;
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, LINE0)) {
			addError("Except first line with starts %s", LINE0);
		}

		Class jobClass = null;
		String nodeId = null;

		String[] three = SPLIT.argsBy(line0, ":");
		switch (three.length) {
			case 1:
			case 2:
				addError("Except pattern [%s:class:node]", LINE0);
				break;
			case 3:
				String key = three[0].trim();
				if (!LINE0.equals(key + ":")) {
					addError("Except pattern [%s:class:node]", LINE0);
					break;
				}
				jobClass = RFL.clazz(three[1].trim(), null);
				if (jobClass == null) {
					addError("Except pattern [%s:class:node]. Set jobClassName", LINE0);
					break;
				}
				//if (!Job.class.isAssignableFrom(jobClassName)) {
				//addError("JobClassName '%s' not assignable from Job.class", jobClassNameStr);
				//}

				nodeId = three[2].trim();
				if (X.empty(nodeId)) {
					addError("Except pattern [%s:class:nodeId]. Set nodeId", LINE0);
					break;
				}
		}

		this.jobClassName = jobClass;
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return "QzEvalMsg{" +
				"msg='" + msg + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", class=" + jobClassName +
				", nodeId=" + nodeId +
				'}';
	}

	public static QzCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static QzCallMsg of(String msg) {
		return (QzCallMsg) ofQk(msg).throwIsErr();
	}

	public static QzCallMsg ofQk(String msg) {
		return new QzCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return QzCallMsg.of(data).isValid();
	}

}
