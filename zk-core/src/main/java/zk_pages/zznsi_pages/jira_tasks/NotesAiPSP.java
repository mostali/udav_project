package zk_pages.zznsi_pages.jira_tasks;

import lombok.SneakyThrows;
import mpc.fs.UF;
import mpc.fs.UUFS;
import mpc.map.MAP;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.QDate;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.STR;
import org.zkoss.zul.Window;
import udav_net.bincall.JiraBin;
import zk_com.base.Cb;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.Tbxm_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.factory.NFOpen;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.FormState;
import zk_os.coms.AFC;
import zk_os.sec.ROLE;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.ZKSession;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithSearch;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@PageRoute(pagename = "tasks", role = ROLE.ANONIM)
public class NotesAiPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {

//	private static final Logger L = LoggerFactory.getLogger(NotesAiPSP.class);

	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";

	public static final String CK_HLP = "jira_lp";
	public static final String SK_HLP = CK_HLP;

	public NotesAiPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

//	static Function2<String, Boolean, String[]> lpGetterBySemicolon = (inputValue, sneakyErrWithView_elseThrow) -> {
//		try {
//			IT.state(!inputValue.contains(";"), "illegal char ';' for store value in cookie");
//			String[] lp = TKN.two(inputValue, ":", null);
//			IT.state(lp != null, "Use pattern login:password");
//			IT.notBlank(lp[0], "set login");
//			IT.notBlank(lp[1], "set password");
//			return lp;
//		} catch (Exception ex) {
//			if (sneakyErrWithView_elseThrow) {
//				ZKI.alert(ex);
//				return null;
//			}
//			throw ex;
//		}
//	};

	@SneakyThrows
	public void buildPageImpl() {

		SeNoteTbxm.registerHeadCom();

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");
		ZKS.HEIGHT_MIN(window, "1200px");

//		ZKS.BGIMAGE(window, "url(_bg_img/bg_i_sec.png)", "contain", "top", "repeat");


		Menupopup0 menu = LogoCom.getMainMenu();

		menu.addMI_DeleteFile(AFC.PAGES.getDir(sdnAny()).toString(), null);

		//
		//
		//

		menu.addMI_SESSSION_BOOLATTR(SK__SHOW_ALL_TASK, false, true);

		//
		//
		//

		AtomicReference<String[]> pare3Ref = new AtomicReference<String[]>();
		final AtomicBoolean useCockie = new AtomicBoolean(false);

		//
		//
		//

		FunctionV showPageFunc = () -> showPage(pare3Ref.get());

		authOk:
		if (pare3Ref.get() == null) {
			String[] hlpFromSes = ZKSession.getSessionAttrs().getAs(SK_HLP, String[].class, null);
			if (hlpFromSes != null) {
				L.info("Run HLP from session:" + ARRi.first(hlpFromSes));
				pare3Ref.set(hlpFromSes);
				break authOk;
			}
			String nlHtml2sysNl = ZKR.getCookieValue(CK_HLP, "").replace(STR.NL_HTML, STR.NL);
			String[] hlpFromCoockie = SPLIT.argsByNL(nlHtml2sysNl);
			if (X.notEmpty(hlpFromCoockie)) {
				L.info("Run HLP from cookie:" + ARRi.first(hlpFromCoockie));
				pare3Ref.set(hlpFromCoockie);
				break authOk;
			}


			Function<String, Object> handlerConf1 = (v1) -> {

				String[] hlp = SPLIT.argsByNL(v1);
				List<JiraBin.IssueContract> issueContracts = JiraBin.loadAllTasksByAuth(hlp);
				L.info("Validate call successfully:" + X.sizeOf(issueContracts));

				if (useCockie.get()) {
					ZKR.setCookie(CK_HLP, v1.replace(STR.NL, STR.NL_HTML), false);
				}

				ZKSession.getSessionAttrs().putAs(SK_HLP, hlp);

				ZKR.restartPage();

				return null;
			};

			Object titleCapCom = Span0.of(Lb.of("input login:pass + jiraUrl"), new Cb().onCLICK(e -> {
				useCockie.set(((Cb) e.getTarget()).isChecked());
				L.info("UseCookie:" + useCockie.get());
			}));
			new Tbxm_CfrmSerializableEventListener(titleCapCom, "login\npass\nurl-to-jira", "login\npass\nhttps://url-to-jira.ru", handlerConf1).onEvent(null);

			return;
		}

		showPageFunc.apply();
	}

	private void showPage(String[] hlpArgs) {

		Boolean showAll = MAP.getAsBool(ZKSession.getSessionAttrsMap(), SK__SHOW_ALL_TASK, false);

		List<JiraBin.IssueContract> issues;
		try {
			issues = JiraBin.loadAllTasksByAuth(hlpArgs);//by user.name & AP:app.org
		} catch (Exception ex) {
			issues = ARR.asLL();
//				window.appendChild((Component) Lb.ERR("Check jira-url").block());
			ZKI.alert(ERR.getRootCause(ex));
		}

		//
		//

//		List<JiraBin.IssueContract> issues = JiraBin.loadAllTasksAsJOC(1);

		Pare<String, String> sdn = ppi().sdnAny();

		Map<String, Path> mapNotes = UUFS.toMapByFn(AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn));


		for (JiraBin.IssueContract obj : issues) {
			String keyAndNN = obj.getKey();
			FormState formState = AppStateFactory.ofFormName_orCreate(sdn, keyAndNN);
			List<String> labels = SPLIT.allByComma(STR.unwrapBody(obj.getLabels(), "[", "]"));
			if (labels.contains("rmm") || (labels.contains("old"))) {
				continue;
			}
			if (!formState.existPropsFile()) {
				String prio = "\n" + "<<<" + obj.getPriorityType() + ">>>";
//				String url = "\n" + "https://job-jira.otr.ru/browse/" + keyAndNN;
//				String jiraHost = AP.get(APK_JIRA_URL, null);
				String jiraHost = hlpArgs[2];
				String url = "\n" + UF.normFileEnd(jiraHost) + "/browse/" + keyAndNN;
				String dataNote = obj.getSummary() + prio + url;
				Window win = NodeFileTransferMan.AddNewForm.addNewFormAndOpen(keyAndNN, dataNote).val();
				String color = ARRi.rand(obj.getPriorityType().colorTheme);
				formState.set(FormState.BG_COLOR, color);
				formState.set("user", hlpArgs[0]);
				formState.set("created", QDate.now().mono14_y4s2());
				ZKS.BGCOLOR(win, color);
				continue;
			}
			NFOpen.openFormRequired(keyAndNN, sdn);
			if (showAll) {
				mapNotes.remove(keyAndNN);
			}
		}

		if (showAll) {
			mapNotes.entrySet().stream().forEach(e -> NFOpen.openFormRequired(e.getKey(), sdn));
		}
	}


}
