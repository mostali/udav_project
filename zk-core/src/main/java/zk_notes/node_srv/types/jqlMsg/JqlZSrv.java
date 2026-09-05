package zk_notes.node_srv.types.jqlMsg;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.exception.StackTraceRuntimeException;
import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import mpe.cmsg.core.INode;
import mpe.cmsg.std.JqlCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import udav_net.bincall.jira.IssueContract;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZWin;
import zk_form.notify.ZKI;
import zk_notes.coms.NoteTbxm;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.PlayContainer;
import zk_notes.node_srv.core.ZService;
import zk_os.core.Sdn;
import zk_page.ZKR;
import zk_page.ZkCookie;
import zk_pages.zznsi_pages.jira_tasks.JtApp;
import zk_pages.zznsi_pages.jira_tasks.core.IssuesViewBuilder;

import javax.servlet.http.Cookie;
import java.util.List;

public class JqlZSrv implements ZService {

	public static final Logger L = LoggerFactory.getLogger(JqlZSrv.class);
	public static final String[] JIRA_HLP = {"jira.login", "jira.pass", "jira.host"};

	@Override
	public boolean applyBeStyle(Pare<Window, IZWin> pare) {

		IZWin com = pare.val();

		if (com instanceof NoteTbxm) {
			NoteTbxm n = (NoteTbxm) com;
			NodeDir nodeDir = n.getNodeDir();
			JqlCallMsg iCallMsg = nodeDir.newInstanceCallMsgValid(null);
			if (iCallMsg == null) {
				L.warn(X.f("Except JqlCallMsg type from note '%s'", nodeDir), new StackTraceRuntimeException());
			}

			String[] tspType = null;
			for (int i = 2; i < iCallMsg.getLinesMsg().size(); i++) {
				if (i > 3) {
					break;
				}
				String msgLine = iCallMsg.getLinesMsg().get(i);
				if (msgLine.startsWith(JtApp.ICO_ISSUE)) {
					String pattern = msgLine.substring(JtApp.ICO_ISSUE.length()).trim();
					tspType = SPLIT.allBy("-", pattern).stream().map(String::trim).toArray(String[]::new);
					if (tspType.length != 3) {
						continue;
					}
				}
			}
//			IssueContract.IssueType issueType = IssueContract.IssueType.valueOf(tspType[0], IssueContract.IssueType.UNDEFINED);
//			IssueContract.StatusType statusType = IssueContract.StatusType.valueOf(tspType[1], IssueContract.StatusType.UNDEFINED);

			IssueContract.PrioType prioType = null;
			if (tspType != null && X.notEmpty(tspType[2])) {
				prioType = IssueContract.PrioType.valueOf(tspType[2], IssueContract.PrioType.UNDEFINED);
			}


			if (prioType != null) {
				com.bgcolor(prioType.zkColor.nextColor());
			}

			return true;
		}

		return false;
	}

	public static PlayContainer newPlayContainer(PlayContainer.PlayLn playLn, JqlCallMsg jqlMsg) {
		boolean hasLink = jqlMsg.getKeyAsTaskUrl(null) != null;
		if (hasLink) {
			Ln ln = Ln.of(SYMJ.LINK);
			JqlAppEvents.applyEvent_openTaskPage(ln, jqlMsg);
			return new PlayContainer(playLn, Xml.NBSP(), ln);
		} else {
			return new PlayContainer(playLn);
		}
	}

	@Override
	public PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {

		JqlCallMsg jqlMsg = playLn.node.newInstanceCallMsgValid(null);

		PlayContainer playContainer = newPlayContainer(playLn, jqlMsg);

		Menupopup0 playMenu = playLn.getOrCreateMenupopup(playContainer);

		if (jqlMsg.getKeyAsTaskUrl(null) != null) {

			JqlAppEvents.applyEvent_openTaskPage(playMenu, jqlMsg);
//            OpenTaskPageSEL.addToMenu(playMenu, jqlMsg);

			playMenu.addMI("Show comments", e -> {
				String[] hlp = getHlpFromCookie();
				String issueKey = jqlMsg.getIssueKey(null);
				IT.state(issueKey != null);
				if (false) {
					//NW
					//String call = (String) JqlCallMsg.of(jqlMsg.getKeyAsTaskUrl()).call(true);
				}
				String s = JqlCallMsg.buildMsgByHlp(hlp, hlp[2] + "/" + issueKey);
				String call;
//				if (true) {
				call = (String) JqlCallMsg.of(s).call(true);
//				String call = (String) jqlMsg.call(true);
				GsonMap gm = GsonMap.of(call);
				List<GsonMap> comments = gm.getAsArrayGsonMap("comments", ARR.EMPTY_LIST);
				List<JtComment> commentLines = STREAM.mapToList(comments, JtComment::of);

				String msg = JOIN.allByNL(commentLines);

				ZKI.infoEditorDark(msg);
			});
			playMenu.addMI("Show issue json", e -> {
				String[] hlp = getHlpFromCookie();
				String issueKey = jqlMsg.getIssueKey(null);
				IT.state(issueKey != null);
				String s = JqlCallMsg.buildMsgByHlp(hlp, hlp[2] + "/" + issueKey);
				String call;
//				if (false) {
//					call = (String) jqlMsg.call(true);
//				}
				call = (String) JqlCallMsg.of(s).call(true);
				ZKI.infoEditorJson(call, false);
			});
		}

		if (jqlMsg.getKeyAsJql(null) != null) {
			playMenu.addMI("Apply this filter", e -> {
				IssuesViewBuilder.newBuilderAndAdd(Sdn.get(), jqlMsg);
				//trash on window - vertical column - with node-display:block
				ZKR.restartPage();
			});
		}

		return playContainer;
	}

	private static String[] getHlpFromCookie(String[]... defRq) {
		String cookieValue = ZkCookie.getCookieValue(JtUserContext.CK_HLP, null);
		if (cookieValue != null) {
			String[] strings = SPLIT.argsByNLh(cookieValue);
			IT.hasLength(strings, 3);
			IT.notEmptyAll(strings);
			return strings;
		}
		return ARG.throwMsg(() -> X.f("Except HLP from cookie"), defRq);
	}

	@SneakyThrows
	@Override
	public String evalAsString(INode node, EvalOpts opts) {
		JqlCallMsg iCallMsg = (JqlCallMsg) node.newInstanceCallMsgValid();
		String call = (String) iCallMsg.call(true);
		return call;
	}


}
