//package zk_pages.zznsi_pages.jira_tasks.rmm;
//
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import mpc.env.EnvTlp;
//import mpc.fs.ext.EXT;
//import mpc.html.EHtml5;
//import mpc.map.BootContext;
//import mpc.map.MAP;
//import mpc.str.condition.StringConditionType;
//import mpe.cmsg.std.JqlCallMsg;
//import mpe.core.ERR;
//import mpu.X;
//import mpu.core.ARRi;
//import mpu.func.FunctionV;
//import mpu.str.SPLIT;
//import mpu.str.STR;
//import org.zkoss.zul.Window;
//import org.zkoss.zul.impl.XulElement;
//import udav_net.bincall.JiraBin;
//import udav_net.bincall.JiraBinExt;
//import zk_com.base.Cb;
//import zk_com.base.Lb;
//import zk_com.base.Tbx;
//import zk_com.base_ctr.Menupopup0;
//import zk_com.base_ctr.Span0;
//import zk_com.core.IZState;
//import zk_com.sun_editor.IPerPage;
//import zk_form.WithLogo;
//import zk_form.events.Tbxm_CfrmSEL;
//import zk_form.notify.ZKI;
//import zk_notes.coms.SeNoteTbxm;
//import zk_notes.factory.NFForm;
//import zk_notes.node_state.impl.PageState;
//import zk_os.core.Sdn;
//import zk_os.sec.ROLE;
//import zk_page.ZKS;
//import zk_page.ZKSession;
//import zk_page.ZkCookie;
//import zk_page.core.PageRoute;
//import zk_page.core.PageSP;
//import zk_page.core.SpVM;
//import zk_page.core.ZPage;
//import zk_page.with_com.WithSearch;
//import zk_pages.zznsi_pages.jira_tasks.JtApp;
//import zk_pages.zznsi_pages.jira_tasks.ViewIssuesBuilder;
//import zk_pages.zznsi_pages.jira_tasks.form.JqlForm;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Function;
//
//@PageRoute(pagename = JiraTasksPSPOLD2.KEY, role = ROLE.ANONIM, eqt = StringConditionType.REGEX)
//public class JiraTasksPSPOLD2 extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {
//
//	public static final String KEY = "@@tasks@[a-zA-Z@\\d.]+";
//
//	public static void main(String[] args) {
//		X.exit("tasks_daaa.aaa2@asd.ru".matches(KEY));
//		X.exit("tasks_daaa.aaa2@asd.ru".matches(KEY));
//	}
//
//	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";
//
//	public static final String CK_HLP = "jira_tasks_hlp";
//
//	public static final String CK_PROJECT = "jira_tasks_project";
//	public static final String CK_STATUS = "jira_tasks_status";
//	public static final String CK_ISSUETYPE = "jira_tasks_issuetype";
//
//
//	public JiraTasksPSPOLD2(Window window, SpVM spVM) {
//		super(window, spVM);
//	}
//
//
//	@SneakyThrows
//	public void buildPageImpl() {
//
//		SeNoteTbxm.registerHeadCom();
//
//		ZKS.PADDING0(window);
//		ZKS.MARGIN(window, "30px 0 0 0");
//		ZKS.HEIGHT_MIN(window, "1200px");
//
//		ZPage zPage = ZPage.of(sdn(), window);
//		zPage.addBreadDiv();
//		ZPage.of(sdn(), window).addNotesSpace();
//
//
//		Menupopup0 logoMenu = LogoCom.getMainMenu();
//
//		PageState pageState = getPageState();
//
//		Path pathDir = pageState.toPathDir();
//
//		logoMenu.addMI_DeleteFile(pathDir.toString(), null);
//
//		logoMenu.addMI_PAGESTATE_BOOLATTR(SK__SHOW_ALL_TASK, false, true);
//
//		logoMenu.addMI_EDITOR("Edit Page Props", pageState.pathFc(), true, EXT.JSON);
//
////		AtomicReference<String[]> hlp = new AtomicReference<>();
//		if (true) {
//
//			if (true) {
//
//				FunctionV appJqlView = () -> {
//					window.appendChild(new JqlForm() {
//						@Override
//						protected void doSearch() {
//							ZKI.infoAfterPointer("" + getJqlExpression());
//						}
//					});
//
//				};
//				UserContext pageTasksContext = UserContext.get();
//				if (pageTasksContext == null) {
//					UserContext.fillFromUser(appJqlView);
//				}else {
//					JtApp.infoCurrent("Apply creds from session", ZKI.Level.INFO);
//				}
////				window.appendChild(new HlpForm() {
////					@Override
////					protected void doConnect() {
////						super.doConnect();
////					}
////				});
//				return;
//			}
//			EnvTlp envTlp = EnvTlp.ofHlpOrg("otr", "dav");
//
//			String jqlExpression = "project in (NSI, BSK) AND issuetype = Sub-task AND status in (\"In Progress\",Paused) AND assignee in (currentUser())";
//			String jqlMsg = "jql:" + jqlExpression + "\n" + //
//					"--auth.usr:dav\n" + //
//					"--app.org:otr\n";
//
//			JiraBin.JqlLoader jqlLoader = new JiraBin.JqlLoader(envTlp.readAsHLP3(), new JiraBin.JqlLoader.JqlFilter(JqlCallMsg.of(jqlMsg)));
//
//			ViewIssuesBuilder walkIssues = new ViewIssuesBuilder(sdn(), jqlLoader, true);
//
//			walkIssues.onBuild();
//
//			return;
//		}
//		if (false) {
//
//
////			window.appendChild(new JqlForm() {
////
////				@Override
////				protected void doSearch() {
////					super.doSearch();
////
////				}
////			});
//			return;
//		}
//		//
//		//
//		//
//
//		if (!checkAndShowPage()) {
//			checkAuthAndCallContext(ppi().sdnAny());
//		}
//
//	}
//
//	private static void checkAuthAndCallContext(Sdn sdn) throws Exception {
//
//		AtomicReference<String[]> pare3Ref = new AtomicReference<String[]>();
//
//
//		String projectValue = BootContext.getKey("jira.tasks.project", "");
//		Tbx tbxProjects = Tbx.of(projectValue, "PROJECT1,PROJECT2");
//
//
//		String[] hlpFromSes = ZKSession.getSessionAttrs().getAs(CK_HLP, String[].class, null);
//		if (hlpFromSes != null) {
//			L.info("Run HLP from session:" + ARRi.first(hlpFromSes));
//			pare3Ref.set(hlpFromSes);
//		}
//
//		if (pare3Ref.get() == null) {
//
//			String nlHtml2sysNl = EHtml5.NLH2NL(ZkCookie.getCookieValue(CK_HLP, ""));
//			String[] hlpFromCoockie = SPLIT.argsByNL(nlHtml2sysNl);
//			if (X.notEmpty(hlpFromCoockie)) {
//				L.info("Run HLP from cookie:" + ARRi.first(hlpFromCoockie));
//				pare3Ref.set(hlpFromCoockie);
//			}
//		}
//
//		final AtomicBoolean useCookie = new AtomicBoolean(false);
//
//		XulElement cbCookie = new Cb().onCLICK(e -> {
//			useCookie.set(((Cb) e.getTarget()).isChecked());
//			L.info("UseCookie:" + useCookie.get());
//		});
//
//		Span0 titleCapCom = Span0.of(Lb.of("input login:pass + jiraUrl"), cbCookie, tbxProjects);
//
//		Function<String, Object> handlerAuthForm = (v1) -> {
//
//			String[] hlp = SPLIT.argsByNL(v1);
//
//			ZKSession.SessionAttrs sessionAttrs = ZKSession.getSessionAttrs();
//
//			{//HLP
//				if (useCookie.get()) {
//					ZkCookie.setCookie(CK_HLP, EHtml5.NL2NLH(v1), false);
//				}
//				sessionAttrs.putAs(CK_HLP, hlp);
//			}
//
//			//PROJECTS
//			String userProjectValues = STR.noSpace(tbxProjects.getValue());
//			if (X.empty(userProjectValues)) {
//				userProjectValues = tbxProjects.getValue();
//			}
//			if (X.notEmpty(userProjectValues)) {
//				if (useCookie.get()) {
//					ZkCookie.setCookie(CK_PROJECT, userProjectValues, false);
//				}
//				sessionAttrs.putAs(CK_PROJECT, userProjectValues);
//			}
//
//			checkAndShowPage();
//
//			titleCapCom.getParent().getParent().detach();
//
//			return false;
//		};
//
//
//		new Tbxm_CfrmSEL(titleCapCom, "login\npass\nurl-to-jira", "login\npass\nhttps://url-to-jira.ru", handlerAuthForm).onEvent(null);
//
//	}
//
//	@RequiredArgsConstructor
//	static class UserContext {
//		final Sdn sdn;
//		final String[] hlpArgs;
//		final Boolean showAll;
//		final JiraBin.JqlLoader.JqlFilterCustom jiraCustomFilter;
//
//		public static UserContext get() {
//			UserContext fromCookie = getFromCookie();
//			if (fromCookie != null) {
//				return fromCookie;
//			}
//			UserContext fromSesseion = getFromSesseion();
//			if (fromSesseion != null) {
//				return fromSesseion;
//			}
//			return null;
//		}
//
//		public static UserContext getFromSesseion() {
//
////			UserContext fromCookie = getFromCookie();
////			if (fromCookie != null) {
////				return fromCookie;
////			}
//
//			Map<String, Object> sessionAttrsMap = ZKSession.getSessionAttrsMap();
//
//			String[] hlpArgs = (String[]) sessionAttrsMap.get(CK_HLP);
//			if (hlpArgs == null) {
//				return null;
//			}
//
//			String projects = (String) sessionAttrsMap.get(CK_PROJECT);
//			String status = (String) sessionAttrsMap.get(CK_STATUS);
//			String issuetype = (String) sessionAttrsMap.get(CK_ISSUETYPE);
//
//			Boolean showAll = MAP.getAsBool(sessionAttrsMap, SK__SHOW_ALL_TASK, false);
//
//			Sdn sdn = Sdn.get();
//
//			JiraBin.JqlLoader.JqlFilterCustom jiraCmdFilter = new JiraBin.JqlLoader.JqlFilterCustom();
//
//			jiraCmdFilter.project = SPLIT.allByComma(projects);
//			jiraCmdFilter.status = SPLIT.allByComma(status);
//			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);
//
//			return new UserContext(sdn, hlpArgs, showAll, jiraCmdFilter);
//		}
//
//		public static UserContext getFromCookie() {
//
//			String nlHtml2sysNl = EHtml5.NLH2NL(ZkCookie.getCookieValue(CK_HLP, ""));
//			if (X.empty(nlHtml2sysNl)) {
//				return null;
//			}
//			String[] hlp = SPLIT.argsByNL(nlHtml2sysNl);
//
//			String projects = ZkCookie.getCookieValue(CK_PROJECT, "");
//			String status = ZkCookie.getCookieValue(CK_STATUS, "");
//			String issuetype = ZkCookie.getCookieValue(CK_ISSUETYPE, "");
//
//			Boolean showAll = ZkCookie.getCookieValueAs(SK__SHOW_ALL_TASK, Boolean.class, false);
//
//			Sdn sdn = Sdn.get();
//
//			JiraBin.JqlLoader.JqlFilterCustom jiraCmdFilter = new JiraBin.JqlLoader.JqlFilterCustom();
//
//			jiraCmdFilter.project = SPLIT.allByComma(projects);
//			jiraCmdFilter.status = SPLIT.allByComma(status);
//			jiraCmdFilter.issuetype = SPLIT.allByComma(issuetype);
//
//			return new UserContext(sdn, hlp, showAll, jiraCmdFilter);
//		}
//
//		public static void fillFromUser(FunctionV successCallback) {
//			AtomicReference<Tbxm_CfrmSEL> fHlp = new AtomicReference<>();
//			Function<String, Object> handlerInput = (v1) -> {
//
//				String[] hlp = SPLIT.argsByNL(v1);
//				List<JiraBinExt.IssueContract> issueContracts = JiraBinExt.loadAllTasks_Models(hlp, "SUP");
//				L.info("Validate call succesfully:" + X.sizeOf(issueContracts));
//
////				ZKR.setCookie(cookieNameLP, v1, false);
//
//				ZKSession.getSessionAttrs().putAs(CK_HLP, hlp);
//
//				if (successCallback != null) {
//					successCallback.apply();
//				}
//
//				fHlp.get().window.onClose();
//
//				JtApp.infoCurrent("Store creds in session", ZKI.Level.WARN);
//
//				return null;
//			};
//
////			Ln auth = Tbx2_CfrmSerializableEventListener.toLn("input login:pass + jiraUrl", "auth", ARR.EMPTY_STR, ARR.of("set pattern login:pass", "set jira.url"), h);
////			window.appendChild(auth);
//			Tbxm_CfrmSEL ev = new Tbxm_CfrmSEL("input login:pass + jiraUrl", "", "", handlerInput);
//			fHlp.set(ev);
//			ev.onEvent();
//
//		}
//	}
//
//	private static boolean checkAndShowPage() {
//
//		UserContext pageContext = UserContext.get();
//		if (pageContext == null) {
//			return false;
//		}
//
//		Sdn sdn = pageContext.sdn;
//		String[] hlp = pageContext.hlpArgs;
//
//		Boolean showAll = pageContext.showAll;
//
//		try {
//
//			ViewIssuesBuilder walkIssues = new ViewIssuesBuilder(sdn, new JiraBin.JqlLoader(hlp, pageContext.jiraCustomFilter), showAll);
//
//			walkIssues.onBuildAndAdd_IssuesView();
//
//			if (showAll) {
//				walkIssues.mapNotes.entrySet().stream().forEach(e -> NFForm.openFormRequired(e.getKey(), sdn));
//			}
//
//		} catch (Exception ex) {
//			ZKI.alert(ERR.getRootCause(ex));
//			return false;
//		}
//		return true;
//	}
//
//
//}
