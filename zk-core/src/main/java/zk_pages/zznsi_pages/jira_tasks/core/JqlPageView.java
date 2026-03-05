package zk_pages.zznsi_pages.jira_tasks.core;

import mpc.fs.ext.EXT;
import mpc.html.EHtml5;
import mpe.cmsg.std.JqlCallMsg;
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
import zk_form.events.Tbxm_CfrmSEL;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.factory.NFBe;
import zk_notes.factory.NFTrans;
import zk_notes.node_srv.types.jqlMsg.JqlZSrv;
import zk_notes.node_state.impl.PageState;
import zk_page.ZKS;
import zk_page.ZKSession;
import zk_page.ZkCookie;
import zk_page.core.PageSP;
import zk_page.zpage.PageView;
import zk_page.zpage.ZPage;
import zk_pages.zznsi_pages.jira_tasks.JtApp;
import zk_pages.zznsi_pages.jira_tasks.form.SingleJqlForm;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class JqlPageView extends PageView {

	public JqlPageView(ZPage zPage) {
		super(zPage);
	}

	public static void showTbxConf_fillFromUser(FunctionV successCallback) {
		AtomicReference<Tbxm_CfrmSEL> fHlp = new AtomicReference<>();

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

			List<IssueContract> testCallFindTask = JiraBin.apiV3().getIssues(jqlMsg);
//				List<JiraBinExt.IssueContract> issueContracts = JiraBinExt.loadAllTasks_Models(hlp, "SUP");
			PageSP.L.info("Validate call succesfully:" + X.sizeOf(testCallFindTask));

//				ZKR.setCookie(cookieNameLP, v1, false);

			ZKSession.getSessionAttrs().putAs(JqlZSrv.UserContext.CK_HLP, hlp);

			if (cbCookie.isChecked()) {
				ZkCookie.setCookie(JqlZSrv.UserContext.CK_HLP, EHtml5.NL2NLH(hlpStr), false);
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
		Tbxm_CfrmSEL ev = new Tbxm_CfrmSEL(titleCapCom, "", "LOGIN\nPASSWORD\nhttp://jira.site.com/", handlerInput);
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
		zPage.addNotesSpace();
	}

	@Override
	protected void init() {
		super.init();

		applyHeadComponents();

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
					String jqlFilter = STR.removeStartsWith(jqlVal, JqlCallMsg.LINE0, true);
					IssuesViewBuilder.doUp(sdn(), JqlZSrv.UserContext.get().hlpArgs, jqlFilter);
				}

				@Override
				protected void doReset(Event e, String jqlVal) {
					RecoveryState.TREE().removeDb();
					NFTrans.deletePage(sdn());
					doUp(e, jqlVal);
				}

			});

		};
		Pare<Boolean, JqlZSrv.UserContext> userContext = JqlZSrv.UserContext.get0();
		if (userContext == null) {
			showTbxConf_fillFromUser(appJqlView);
		} else {
			appJqlView.apply();
			JtApp.showInfo("Apply creds from " + (userContext.key() ? " Cookie" : " Session"));
		}

//				window.appendChild(new HlpForm() {
//					@Override
//					protected void doConnect() {
//						super.doConnect();
//					}
//				});
	}
}
