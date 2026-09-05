package zk_pages.zznsi_pages.jira_tasks.core;

import mpc.arr.STREAM;
import mpc.fs.ext.EXT;
import mpc.html.EHtml5;
import mpc.types.tks.FIDT;
import mpe.cmsg.std.JqlCallMsg;
import mpe.sql.SeqBuilder;
import mpu.X;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import udav_net.bincall.JiraBin;
import udav_net.bincall.jira.IssueContract;
import zk_com.base.Cb;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_form.WithLogo;
import zk_form.events.cfrm.CfrmTbxm_SEL;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.factory.NFTrans;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.jqlMsg.JqlZSrv;
import zk_notes.node_srv.types.jqlMsg.JtUserContext;
import zk_notes.node_state.impl.PageState;
import zk_page.ZKS;
import zk_page.ZKSession;
import zk_page.ZkCookie;
import zk_page.core.PageSP;
import zk_page.zpage.PageZView;
import zk_page.zpage.ZPage;
import zk_pages.zznsi_pages.jira_tasks.JtApp;
import zk_pages.zznsi_pages.jira_tasks.form.SingleJqlForm;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class JqlPageZView extends PageZView {
	public static final String PATTERN_QUERY = "project in (%s) AND key in (%s)";
	public static final String QUERY_ARG_TASKS = "t";

	public JqlPageZView(ZPage zPage) {
		super(zPage);
	}

	public static void showTbxConf_fillFromUser(FunctionV successCallback) {
		AtomicReference<CfrmTbxm_SEL> fHlp = new AtomicReference<>();

		final AtomicBoolean useCookie = new AtomicBoolean(false);
		Cb cbCookie = (Cb) new Cb().onCLICK(e -> {
			useCookie.set(((Cb) e.getTarget()).isChecked());
			JqlZSrv.L.info("UseCookie:" + useCookie.get());
		});
		cbCookie.setLabel("Store in cookie");
		ZKS.MARGIN_LEFT(cbCookie, "30");

		Span0 titleCapCom = Span0.of(Lb.of("Input credentials for Jira system"), cbCookie);

		Function<String, Object> handlerInput = (hlpStr) -> {

			String[] hlp = SPLIT.argsByNL(hlpStr);

			String jqlMsg = JqlCallMsg.buildMsgByHlp(hlp, JqlCallMsg.JQL_PING);

			List<IssueContract> testCallFindTask = JiraBin.apiV3().getIssuesContract(jqlMsg);
//				List<JiraBinExt.IssueContract> issueContracts = JiraBinExt.loadAllTasks_Models(hlp, "SUP");
			PageSP.L.info("Validate call succesfully:" + X.sizeOf(testCallFindTask));

//				ZKR.setCookie(cookieNameLP, v1, false);

			ZKSession.getSessionAttrs().putAs(JtUserContext.CK_HLP, hlp);

			if (cbCookie.isChecked()) {
				ZkCookie.setCookie(JtUserContext.CK_HLP, EHtml5.NL2NLH(hlpStr), false);
			}

			if (successCallback != null) {
				successCallback.apply();
			}

			fHlp.get().window.onClose();

			String sfx = X.empty(testCallFindTask) ? "" : "\nYour work task: " + JtApp.toLinkIssue(hlp[2], testCallFindTask.get(0).getKey(null));

			JtApp.showWarn("Store creds in session" + sfx);

			return null;
		};

//			Ln auth = Tbxm2_CfrmSEL.toLn("input login:pass + jiraUrl", "auth", ARR.EMPTY_STR, ARR.of("set pattern login:pass", "set jira.url"), h);
//			window.appendChild(auth);
		CfrmTbxm_SEL ev = new CfrmTbxm_SEL(titleCapCom, "", "LOGIN\nPASSWORD\nhttp://jira.site.com/", handlerInput);
		fHlp.set(ev);
		ev.onEvent();

	}


	private void applyHeadComponents() {
		SeNoteTbxm.registerHeadCom();

		if (zPage.window0 instanceof Window) {
			ZKS.PADDING0((Window) zPage.window0);
		}

		ZKS.MARGIN(zPage.window0, "30px 0 0 0");
		ZKS.HEIGHT_MIN(zPage.window0, "1200px");

		zPage.addBreadDiv();
	}

	@Override
	protected void init() {
		super.init();

		applyHeadComponents();

		String firstAsStr = ppiq().queryUrl().getFirstAsStr(QUERY_ARG_TASKS, null);
		if (X.notEmpty(firstAsStr)) {
			List<String> tasks = SPLIT.allByComma(firstAsStr);
			if (X.notEmpty(tasks)) {
				Set<FIDT> fits = STREAM.mapToSet(tasks, FIDT::of);
				Set<String> projects = STREAM.mapToSet(fits, FIDT::first);
				StringBuilder projSeq = SeqBuilder.generateSequence(projects, true, false);
				StringBuilder fitsSeq = SeqBuilder.generateSequence(fits, true, false);
				String jqlQuery = X.f(PATTERN_QUERY, projSeq, fitsSeq);

				NodeDir nodeDir = NodeDir.ofNodeName(sdn(), JqlCallMsg.TYPE, true);

				nodeDir.getProxyRW(false).writeContent(JqlCallMsg.LINE0 + jqlQuery);
//				ZKR.restartPage();

			}
		}

		zPage.addNotesSpace();


		//
		//

		Menupopup0 logoMenu = WithLogo.LogoCom.getMainMenu();

		PageState pageState = getPageState();

		Path pathDir = pageState.toPathDir();

		logoMenu.addMI_DeleteFile(pathDir.toString(), null);

//		logoMenu.addMI_PAGESTATE_BOOLATTR(SK__SHOW_ALL_TASK, false, true);

		logoMenu.addMI_EDITOR("Edit Page Props", pageState.pathFc(), true, EXT.JSON);


	}

	public void checkAndOpenUpdateHLP() {

		FunctionV appJqlView = () -> {
			zPage.window0.appendChild(new SingleJqlForm() {
				@Override
				protected void doAdd(Event e, String jqlVal) {
					IssuesViewBuilder.newBuilderAndAdd(jqlVal);
				}

				@Override
				protected void doUp(Event e, String jqlVal) {
					String jqlFilter = STR.removeStart(jqlVal, JqlCallMsg.LINE0, true);
					IssuesViewBuilder.doUp(sdn(), JtUserContext.get().hlpArgs, jqlFilter);
				}

				@Override
				protected void doReset(Event e, String jqlVal) {
					RecoveryState.TREE().removeDb();
					NFTrans.deletePage(sdn());
					doUp(e, jqlVal);
				}

			});

		};
		Pare<Boolean, JtUserContext> userContext = JtUserContext.get0();
		if (userContext == null) {
			showTbxConf_fillFromUser(appJqlView);
		} else {
			appJqlView.apply();
			JtApp.showInfo("Apply creds from " + (userContext.key() ? " Cookie" : " Session"));
		}

	}
}
