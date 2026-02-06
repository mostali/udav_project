package zk_pages.zznsi_pages.jira_tasks;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UF;
import mpc.fs.UUFS;
import mpc.fs.ext.EXT;
import mpc.html.EHtml5;
import mpc.map.BootContext;
import mpc.map.MAP;
import mpc.str.condition.StringConditionType;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.core.QDate;
import mpu.func.Function2;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.STR;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import udav_net.bincall.JiraBin;
import zk_com.base.Cb;
import zk_com.base.Lb;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.Tbxm_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.NotesSpace;
import zk_notes.factory.NFOpen;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_page.*;
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

@PageRoute(pagename = JiraTasksPSP.KEY, role = ROLE.ANONIM, eqt = StringConditionType.REGEX)
public class JiraTasksPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {

	public static final String KEY = "tasks_[a-zA-Z@\\d.]+";

	public static void main(String[] args) {
		X.exit("tasks_daaa.aaa2@asd.ru".matches(KEY));
	}

	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";

	public static final String CK_HLP = "jira_tasks_lp";

	public static final String CK_PROJECT = "jira_tasks_project";
	public static final String CK_STATUS = "jira_tasks_status";
	public static final String CK_ISSUETYPE = "jira_tasks_issuetype";


	public JiraTasksPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	public static class JiraCmdFilter {
		List<String> project, status, issuetype;
	}

	static Function2<String[], JiraCmdFilter, List<JiraBin.IssueContract>> issuesLoader = (hlp, jiraCmdFilter) -> {

		String msg = X.fl("Call jira from [{}->{}] with pojects [{}]", hlp[0], "ПАРОЛЬ", jiraCmdFilter.project);
		L.info(msg);
		ZKI.log(msg);

		List<String> projects = IT.notEmpty(jiraCmdFilter.project, "set projects");
		String projectsArg = JOIN.allByComma(projects);
		List<JiraBin.IssueContract> issueContracts = JiraBin.loadAllTasksByAuth(hlp, projectsArg);

		msg = "Jira call successfully:" + X.sizeOf(issueContracts);
		L.info(msg);
		ZKI.log(msg);

		return issueContracts;
	};


	@SneakyThrows
	public void buildPageImpl() {

		SeNoteTbxm.registerHeadCom();

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");
		ZKS.HEIGHT_MIN(window, "1200px");

//		ZKS.BGIMAGE(window, "url(_bg_img/bg_i_sec.png)", "contain", "top", "repeat");

		Menupopup0 logoMenu = LogoCom.getMainMenu();

//		String pageDir = AFC.PAGES.getDir(sdnAny()).toString();
		PageState pageState = getPageState();
		Path pathDir = pageState.toPathDir();

		logoMenu.addMI_DeleteFile(pathDir.toString(), null);

		logoMenu.addMI_PAGESTATE_BOOLATTR(SK__SHOW_ALL_TASK, false, true);

		logoMenu.addMI_EDITOR("Edit Page Props", pageState.pathFc(), true, EXT.JSON);

		if (false) {

			window.appendChild(new JtLpForm() {

				@Override
				protected void doConnect() {
					super.doConnect();


				}
			});
			window.appendChild(new JtForm() {

				@Override
				protected void doSearch() {
					super.doSearch();

				}
			});
			return;
		}
		//
		//
		//

		if (!checkAndShowPage()) {
			checkAuthAndCallContext(ppi().sdnAny());
		}

	}

	private static void checkAuthAndCallContext(Sdn sdn) throws Exception {

		AtomicReference<String[]> pare3Ref = new AtomicReference<String[]>();


		String projectValue = BootContext.getKey("jira.tasks.project", "");
		Tbx tbxProjects = Tbx.of(projectValue, "PROJECT1,PROJECT2");


		String[] hlpFromSes = ZKSession.getSessionAttrs().getAs(CK_HLP, String[].class, null);
		if (hlpFromSes != null) {
			L.info("Run HLP from session:" + ARRi.first(hlpFromSes));
			pare3Ref.set(hlpFromSes);
		}

		if (pare3Ref.get() == null) {

			String nlHtml2sysNl = EHtml5.NLH2NL(ZkCookie.getCookieValue(CK_HLP, ""));
			String[] hlpFromCoockie = SPLIT.argsByNL(nlHtml2sysNl);
			if (X.notEmpty(hlpFromCoockie)) {
				L.info("Run HLP from cookie:" + ARRi.first(hlpFromCoockie));
				pare3Ref.set(hlpFromCoockie);
			}
		}

		final AtomicBoolean useCookie = new AtomicBoolean(false);

		XulElement cbCookie = new Cb().onCLICK(e -> {
			useCookie.set(((Cb) e.getTarget()).isChecked());
			L.info("UseCookie:" + useCookie.get());
		});

		Span0 titleCapCom = Span0.of(Lb.of("input login:pass + jiraUrl"), cbCookie, tbxProjects);

		Function<String, Object> handlerAuthForm = (v1) -> {

			String[] hlp = SPLIT.argsByNL(v1);

			ZKSession.SessionAttrs sessionAttrs = ZKSession.getSessionAttrs();

			{//HLP
				if (useCookie.get()) {
					ZkCookie.setCookie(CK_HLP, EHtml5.NL2NLH(v1), false);
				}
				sessionAttrs.putAs(CK_HLP, hlp);
			}

			//PROJECTS
			String userProjectValues = STR.noSpace(tbxProjects.getValue());
			if (X.empty(userProjectValues)) {
				userProjectValues = tbxProjects.getValue();
			}
			if (X.notEmpty(userProjectValues)) {
				if (useCookie.get()) {
					ZkCookie.setCookie(CK_PROJECT, userProjectValues, false);
				}
				sessionAttrs.putAs(CK_PROJECT, userProjectValues);
			}

//			showPage(sdn, hlp, userProjectValues);
			checkAndShowPage();

//			ZKR.restartPage();
			titleCapCom.getParent().getParent().detach();

			return false;
		};


		new Tbxm_CfrmSerializableEventListener(titleCapCom, "login\npass\nurl-to-jira", "login\npass\nhttps://url-to-jira.ru", handlerAuthForm).onEvent(null);

//		return false;
	}

	@RequiredArgsConstructor
	static class PageTasksContext {
		final Sdn sdn;
		final String[] hlpArgs;
		final Boolean showAll;
		final JiraCmdFilter jiraCmdFilter;

		public static PageTasksContext get() {
			PageTasksContext fromCookie = getFromCookie();
			if (fromCookie != null) {
				return fromCookie;
			}
			PageTasksContext fromSesseion = getFromSesseion();
			if (fromSesseion != null) {
				return fromSesseion;
			}
			return null;
		}

		public static PageTasksContext getFromSesseion() {

			PageTasksContext fromCookie = getFromCookie();
			if (fromCookie != null) {
				return fromCookie;
			}

			Map<String, Object> sessionAttrsMap = ZKSession.getSessionAttrsMap();

			String[] hlpArgs = (String[]) sessionAttrsMap.get(CK_HLP);
			if (hlpArgs == null) {
				return null;
			}

			String projects = (String) sessionAttrsMap.get(CK_PROJECT);
			String status = (String) sessionAttrsMap.get(CK_STATUS);
			String issuetype = (String) sessionAttrsMap.get(CK_ISSUETYPE);

			Boolean showAll = MAP.getAsBool(sessionAttrsMap, SK__SHOW_ALL_TASK, false);

			Sdn sdn = Sdn.get();

			JiraCmdFilter jiraCmdFilter = new JiraCmdFilter();
			jiraCmdFilter.project = SPLIT.allByComma(projects);
			jiraCmdFilter.status = SPLIT.allByComma(status);
			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);

			return new PageTasksContext(sdn, hlpArgs, showAll, jiraCmdFilter);
		}

		public static PageTasksContext getFromCookie() {

			String nlHtml2sysNl = EHtml5.NLH2NL(ZkCookie.getCookieValue(CK_HLP, ""));
			if (X.empty(nlHtml2sysNl)) {
				return null;
			}
			String[] hlp = SPLIT.argsByNL(nlHtml2sysNl);

			String projects = ZkCookie.getCookieValue(CK_PROJECT, "");
			String status = ZkCookie.getCookieValue(CK_STATUS, "");
			String issuetype = ZkCookie.getCookieValue(CK_ISSUETYPE, "");

			Boolean showAll = ZkCookie.getCookieValueAs(SK__SHOW_ALL_TASK, Boolean.class, false);

			Sdn sdn = Sdn.get();
			JiraCmdFilter jiraCmdFilter = new JiraCmdFilter();
			jiraCmdFilter.project = SPLIT.allByComma(projects);
			jiraCmdFilter.status = SPLIT.allByComma(status);
			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);
			return new PageTasksContext(sdn, hlp, showAll, jiraCmdFilter);
		}
	}

	private static boolean checkAndShowPage() {

		PageTasksContext pageContext = PageTasksContext.get();
		if (pageContext == null) {
			return false;
		}

		Sdn sdn = pageContext.sdn;
		String[] hlp = pageContext.hlpArgs;

		Boolean showAll = pageContext.showAll;

		List<JiraBin.IssueContract> issues;
		try {
			issues = issuesLoader.apply(hlp, pageContext.jiraCmdFilter);
			Div0 sss = Div0.of(Lb.of("sss"));
//			sss._showInWindowEmbed();
			ZKC.getFirstWindow().insertBefore(sss, NotesSpace.findFirst());
		} catch (Exception ex) {
			ZKI.alert(ERR.getRootCause(ex));
			return false;
		}

		//
		//

		Map<String, Path> mapNotes = UUFS.toMapByFn(AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn));

		for (JiraBin.IssueContract obj : issues) {
			String keyAndNN = obj.getKey();
			ObjState formState = AppStateFactory.forForm(sdn, keyAndNN);
			List<String> labels = SPLIT.allByComma(STR.unwrapBody(obj.getLabels(), "[", "]"));
			if (labels.contains("rmm") || (labels.contains("old"))) {
				continue;
			}
			if (!formState.existPropsFile()) {
				String prio = "\n" + "<<<" + obj.getPriorityType() + ">>>";
//				String url = "\n" + "https://job-jira.otr.ru/browse/" + keyAndNN;
//				String jiraHost = AP.get(APK_JIRA_URL, null);
				String jiraHost = hlp[2];
				String url = "\n" + UF.normFileEnd(jiraHost) + "/browse/" + keyAndNN;
				String dataNote = obj.getSummary() + prio + url;
				Window win = NodeFileTransferMan.AddNewForm.addNewFormAndOpen(keyAndNN, dataNote).val();
				String color = ARRi.rand(obj.getPriorityType().colorTheme);
				formState.set(ObjState.BG_COLOR, color);
				formState.set("user", hlp[0]);
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

		return true;
	}


}
