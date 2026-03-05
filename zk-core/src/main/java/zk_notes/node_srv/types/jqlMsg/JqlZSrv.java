package zk_notes.node_srv.types.jqlMsg;

import lombok.RequiredArgsConstructor;
import mpc.exception.StackTraceRuntimeException;
import mpc.html.EHtml5;
import mpc.types.tks.FID;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.INode;
import mpe.cmsg.std.JqlCallMsg;
import mpe.cmsg.std.PublCallMsg;
import mpu.X;
import mpu.pare.Pare;
import mpu.pare.Paret;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import udav_net.bincall.JiraBin;
import udav_net.bincall.jira.IssueContract;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_notes.coms.NoteTbxm;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.PlayContainer;
import zk_notes.node_srv.core.ZService;
import zk_os.core.Sdn;
import zk_page.ZKR;
import zk_page.ZKSession;
import zk_page.ZkCookie;
import zk_pages.zznsi_pages.jira_tasks.JtApp;
import zk_pages.zznsi_pages.jira_tasks.core.IssuesViewBuilder;

import java.util.Map;

public class JqlZSrv implements ZService {

	public static final Logger L = LoggerFactory.getLogger(JqlZSrv.class);

	@Override
	public Paret doSendMsg_AsyncLog(INode inject, TrackMap.TrackId track) {
		return doSendMsg_AsyncLog0(inject, track);
	}

	public static Paret<FID> doSendMsg_AsyncLog0(INode<NodeDir> inject, TrackMap.TrackId track) {

//		NI.stop();
//		PublCallMsg callMsg = inject.newCalclMsg(StdType.PUBL, false);
		PublCallMsg publCallMsg = new PublCallMsg(inject.readNodeDataStr());

//		return new PublTask(callMsg).doTask().getFidParet_();
//		JiraBinExt.z
//		ZJar.of("")
		return null;
	}

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
				if (msgLine.startsWith(JtApp.TASK_ICON)) {
					String pattern = msgLine.substring(JtApp.TASK_ICON.length()).trim();
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

	@Override
	public PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
		PlayContainer playContainer = new PlayContainer(playLn);
		Menupopup0 playMenu = playLn.getOrCreateMenupopup(playContainer);

		JqlCallMsg jqlMsg = playLn.node.newInstanceCallMsgValid(null);


		if (jqlMsg.getKeyAsTaskUrl(null) != null) {
			playMenu.addMI("Open task page", e -> {
				String keyAsTaskUrl = jqlMsg.getKeyAsTaskUrl(null);
				if (keyAsTaskUrl != null) {
					ZKR.openWindow800_1200(keyAsTaskUrl);
				}
			});
		}

		if (jqlMsg.getKeyAsJql(null) != null) {

			playMenu.addMI("Apply this filter", e -> {
				IssuesViewBuilder.newBuilderAndAdd(Sdn.get(), jqlMsg);
			});
		}

		return playContainer;
	}


	@RequiredArgsConstructor
	public static class UserContext {
		public static final String SK__SHOW_ALL_TASK = "ShowAllTask";
		public static final String CK_HLP = "jira_tasks_hlp";
		public static final String CK_PROJECT = "jira_tasks_project";
		public static final String CK_STATUS = "jira_tasks_status";
		public static final String CK_ISSUETYPE = "jira_tasks_issuetype";
		public final Sdn sdn;
		public final String[] hlpArgs;
		public final JiraBin.JqlLoader.JqlFilterCustom jiraCustomFilter;

		public static UserContext get() {
			return get0().val();
		}

		public static Pare<Boolean, UserContext> get0() {
			UserContext fromCookie = getFromCookie();
			if (fromCookie != null) {
				return Pare.of(true, fromCookie);
			}
			UserContext fromSesseion = getFromSesseion();
			if (fromSesseion != null) {
				return Pare.of(false, fromCookie);
			}
			return null;
		}

		public static UserContext getFromSesseion() {

//			UserContext fromCookie = getFromCookie();
//			if (fromCookie != null) {
//				return fromCookie;
//			}

			Map<String, Object> sessionAttrsMap = ZKSession.getSessionAttrsMap();

			String[] hlpArgs = (String[]) sessionAttrsMap.get(CK_HLP);
			if (hlpArgs == null) {
				return null;
			}

			String projects = (String) sessionAttrsMap.get(CK_PROJECT);
			String status = (String) sessionAttrsMap.get(CK_STATUS);
			String issuetype = (String) sessionAttrsMap.get(CK_ISSUETYPE);

//			Boolean showAll = MAP.getAsBool(sessionAttrsMap, SK__SHOW_ALL_TASK, false);

			Sdn sdn = Sdn.get();

			JiraBin.JqlLoader.JqlFilterCustom jiraCmdFilter = new JiraBin.JqlLoader.JqlFilterCustom();

			jiraCmdFilter.project = SPLIT.allByComma(projects);
			jiraCmdFilter.status = SPLIT.allByComma(status);
			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);

			return new UserContext(sdn, hlpArgs, jiraCmdFilter);
		}

		public static UserContext getFromCookie() {

			String nlHtml2sysNl = EHtml5.NLH2NL(ZkCookie.getCookieValue(CK_HLP, ""));
			if (X.empty(nlHtml2sysNl)) {
				return null;
			}
			String[] hlp = SPLIT.argsByNL(nlHtml2sysNl);

			String projects = ZkCookie.getCookieValue(CK_PROJECT, "");
			String status = ZkCookie.getCookieValue(CK_STATUS, "");
			String issuetype = ZkCookie.getCookieValue(CK_ISSUETYPE, "");

//			Boolean showAll = ZkCookie.getCookieValueAs(SK__SHOW_ALL_TASK, Boolean.class, false);

			Sdn sdn = Sdn.get();

			JiraBin.JqlLoader.JqlFilterCustom jiraCmdFilter = new JiraBin.JqlLoader.JqlFilterCustom();

			jiraCmdFilter.project = SPLIT.allByComma(projects);
			jiraCmdFilter.status = SPLIT.allByComma(status);
			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);

			return new UserContext(sdn, hlp, jiraCmdFilter);
		}

	}
}
