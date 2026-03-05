package mpe.cmsg.std;

import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import mpe.cmsg.core.CallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

//@CallMsg.CallMsgAno(st = "")
public class JqlCallMsg extends CallMsg {

	//LINE0
	//TYPE
	//NAME
	//NAME_EN--
	//NAME_RU
	//TITLE

	//	public static final String NAME = "PUBL";
//	public static final String TITLE = "Send PUBL";
	public static final String ICON = SYMJ.PUZZLE;
	//	public static final String ICON_LIGHT = "";
	public static final String[] COLOR = ColorTheme.ORANGE;
	//	public static final String SHORT_NAME = "publ";
//	public static final String SHORT_NAME_RU = "ПУБЛИКАЦИЯ";

	public static final String KEY = "jql";
	public static final String TYPE = KEY.toUpperCase();
	public static final String LINE0 = "jql:";

	public static final String JQL_PING = "issuetype = Sub-task AND status in (\"In Progress\") AND assignee in (currentUser())";


	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

	public static @NotNull String buildMsgByHlp(String[] hlp, String jqlVal) {
		String jqlMsg = "jql:" + jqlVal + STR.NL +
				"--jira.login:" + hlp[0] + STR.NL +
				"--jira.pass:" + hlp[1] + STR.NL +
				"--jira.host:" + hlp[2] + STR.NL;
		return jqlMsg;
	}


//	@Override
//	public TYPE subtype(Object... defRq) {
//		Map<String, Object> head = getHeaders_MAP();
//		if (head.keySet().stream().anyMatch(k -> k.startsWith("dst:g"))) {
//			return TYPE.VK;
//		}
//		return (TYPE) ARG.throwMsg(() -> X.f("Not found PublCallMsg"), defRq);
//	}


	public JqlCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			addError("Empty msg");
			return;
		}

		if (!STR.startsWith(line0, true, LINE0)) {
			addError("Except first line with starts %s", LINE0);
		}


	}

	@Override
	public String toString() {
		return "JqlCallMsg{" + "msg='" + msg + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", errs=" + X.sizeOf0(getErrors()) + '}';
	}

	public static JqlCallMsg of(Path file) {
		String msg = RW.readString(file);
		JqlCallMsg publCallMsg = of(msg);
		publCallMsg.setFromSrc(file);
		return publCallMsg;
	}

	public static JqlCallMsg of(String msg) {
		return (JqlCallMsg) ofQk(msg).throwIsErr();
	}

	public static JqlCallMsg ofQk(String msg) {
		return new JqlCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return JqlCallMsg.of(data).isValid();
	}

	public String getKeyAsTaskUrl(String... defRq) {
		String keyLastFromLine0 = super.getKeyLastFromLine0(null);
		if (keyLastFromLine0 != null) {
			if (UST.URL(keyLastFromLine0, null) != null) {
				return keyLastFromLine0;
			}
		}
		return ARG.throwMsg(() -> X.f("JqlCallMsg '%s' except key asTaskUrl from line0 : %s", toObjMsgId(), line0), defRq);
	}

	public String getKeyAsJql(String... defRq) {
		String keyAsTaskUrl = getKeyAsTaskUrl(null);
		if (keyAsTaskUrl == null) {
			String keyLastFromLine0 = super.getKeyLastFromLine0(null);
			if (keyLastFromLine0 != null) {
				return keyLastFromLine0;
			}
		}
		return ARG.throwMsg(() -> X.f("JqlCallMsg '%s' except key asTaskJql from line0 : %s", toObjMsgId(), line0), defRq);
	}

	public String getAppOrg(String... defRq) {
		String headerValueByKey = getHeaderValueByKey("app.org", defRq);
		String ne = IT.NE(headerValueByKey, "set app.org");
		IT.NNh(headerValueByKey, "set app.org");
		return ne;
	}

//	public String getExtAsPST(Object o) {
//		getHeadersAndBodyLines_SkipCommonComment()
//	}

}
