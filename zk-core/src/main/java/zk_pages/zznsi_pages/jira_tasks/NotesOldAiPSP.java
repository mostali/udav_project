//package zkbea_apps.jira_tasks;
//
//import lombok.SneakyThrows;
//import mpc.env.AP;
//import mpc.fs.UF;
//import mpc.fs.UFS;
//import mpc.map.MAP;
//import mpe.core.ERR;
//import mpu.IT;
//import mpu.X;
//import mpu.core.ARR;
//import mpu.core.ARRi;
//import mpu.core.QDate;
//import mpu.func.Function2;
//import mpu.pare.Pare;
//import mpu.str.SPLIT;
//import mpu.str.STR;
//import mpu.str.TKN;
//import mpu.str.UST;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Window;
//import udav_net.bincall.JiraBin;
//import zk_com.base.Lb;
//import zk_com.base.Tbx;
//import zk_com.base_ctr.Menupopup0;
//import zk_com.core.IZState;
//import zk_com.sun_editor.IPerPage;
//import zk_form.WithLogo;
//import zk_form.events.Tbx_CfrmSerializableEventListener;
//import zk_form.notify.ZKI;
//import zk_notes.coms.SeNoteTbxm;
//import zk_notes.factory.NFOpen;
//import zk_notes.node_srv.fsman.NodeFileTransferMan;
//import zk_notes.node_state.FormState;
//import zk_os.coms.AFC;
//import zk_os.sec.ROLE;
//import zk_page.ZKR;
//import zk_page.ZKS;
//import zk_page.ZKSession;
//import zk_page.core.PageRoute;
//import zk_page.core.PageSP;
//import zk_page.core.SpVM;
//import zk_page.with_com.WithSearch;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Function;
//
//@PageRoute(pagename = "tas1ks", role = ROLE.ANONIM)
//public class NotesOldAiPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {
//
//	private static final Logger L = LoggerFactory.getLogger(NotesOldAiPSP.class);
//
//
//	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";
//
//	public static final String APK_JIRA_URL = "jira.url";
//	public static final String cookieNameLP = "jira_lp";
//
////	private LogPageHeaderProps pageHeader = null;
//
//	public NotesOldAiPSP(Window window, SpVM spVM) {
//		super(window, spVM);
//	}
//
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
////		FormPropsApply main = getFormsState("main", false, true);
////		String bg = main.get("bg", null);
//
//		ZKS.BGIMAGE(window, "url(_bg_img/bg_i_sec.png)", "contain", "top", "repeat");
////		ZKS.BGIMAGE(window, "url(_img/bg_dark_light.jpg)", "contain", "top", "repeat");
//
//
//		Menupopup0 menu = LogoCom.getMainMenu();
////		Menupopup0 menu = getLogoOrCreate().getContextMenu();
//		menu.addMI_DeleteFile(AFC.PAGES.getDir(sdn()).toString(), null);
//
//		//
//		//
//		//
//
//		menu.addMI_SESSSION_BOOLATTR(SK__SHOW_ALL_TASK, false, true);
//
//		//
//		//
//		//
//
//		AtomicReference<String[]> pare3Ref = new AtomicReference<String[]>();
//
//		//
//		//
//		//
//
//		authOk:
//		if (pare3Ref.get() == null) {
//			String[] hlpFromSes = ZKSession.getSessionAttrs().getAs("hlp", String[].class, null);
//			if (hlpFromSes != null) {
//				pare3Ref.set(hlpFromSes);
//				break authOk;
//			}
//
//			Function2<String, String, Object> handlerConf2 = (v1, v2) -> {
//
//				String[] apply = lpGetterBySemicolon.apply(v1, true);
//				String[] hlp = ARR.merge(apply, v2);
//				List<JiraBin.IssueContract> issueContracts = JiraBin.loadAllTasksByAuth(hlp);
//				L.info("Validate call succesfully:" + X.sizeOf(issueContracts));
//
//				ZKR.setCookie(cookieNameLP, v1, false);
//
//				ZKSession.getSessionAttrs().putAs("hlp", hlp);
//
//				ZKR.restartPage();
//
////				Listbox0.
//				return null;
//			};
//			Function<String, Object> handlerConf1 = (v1) -> {
//
//				String[] hlp = SPLIT.argsByNL(v1);
//				List<JiraBin.IssueContract> issueContracts = JiraBin.loadAllTasksByAuth(hlp);
//				L.info("Validate call succesfully:" + X.sizeOf(issueContracts));
//
//				ZKR.setCookie(cookieNameLP, v1, false);
//
//				ZKSession.getSessionAttrs().putAs("hlp", hlp);
//
//				ZKR.restartPage();
//
//				return null;
//			};
//
////			Ln auth = Tbx2_CfrmSerializableEventListener.toLn("input login:pass + jiraUrl", "auth", ARR.EMPTY_STR, ARR.of("set pattern login:pass", "set jira.url"), h);
////			window.appendChild(auth);
//			new Tbx_CfrmSerializableEventListener("input login:pass + jiraUrl", "", "", handlerConf1).onEvent(null);
////			new Tbx2_CfrmSerializableEventListener("input login:pass + jiraUrl", ARR.EMPTY_ARGS, ARR.of("set pattern login:pass", "set jira.url"), handlerConf2).onEvent(null);
//
//			return;
//		}
//
////		showPage(EnvTlp.ofSysAcc("jira", "dav").readAsHLP3());
//
//		showPage(pare3Ref.get());
//
////		nw();
//	}
//
//	private void nw() {
//
//		String cookieNameURL = APK_JIRA_URL;
//		String cookieValueLP = ZKR.getCookieValue(cookieNameLP, null);
//		String cookieValueURL = ZKR.getCookieValue(cookieNameURL, null);
//		if (cookieValueURL == null) {
//			cookieValueURL = AP.get(APK_JIRA_URL, null);
//		}
//		if (UST.URL(cookieValueURL, null) == null) {
//			cookieValueURL = null;
//		}
//		{
//			Tbx tbxmLP = (Tbx) Tbx.of("Set login:pass. This data stored only in your cookie").width(400);
//			Tbx tbxmUrl = (Tbx) (cookieValueURL == null ? Tbx.of("Set jira.url") : Tbx.of(cookieValueURL, "Set jira.url")).width(400);
//			if (cookieValueLP != null) {
//				try {
//					String[] lp = lpGetterBySemicolon.apply(cookieValueLP, false);
//					tbxmLP.setValue(lp[0]);
//				} catch (Exception ex) {
//					L.error("Illegal cookie value:" + cookieValueLP, ex);
//					ZKR.deleteCookie(cookieNameLP);
//				}
//			}
//
//			tbxmLP.onOK(e -> {
//				String inputValue = tbxmLP.getValue();
//				String[] lp = lpGetterBySemicolon.apply(inputValue, true);
//				if (lp == null) {
//					ZKR.deleteCookie(cookieNameLP);
//				} else {
//					ZKR.setCookie(cookieNameLP, inputValue, false);
//				}
//				ZKR.restartPage();
//			});
//
//			tbxmUrl.onOK(e -> {
//				String inputValue = tbxmUrl.getValue();
//				if (UST.URL(inputValue, null) == null) {
//					ZKR.deleteCookie(cookieNameLP);
//				}
//				ZKR.setCookie(cookieNameURL, inputValue, false);
//				ZKR.restartPage();
//			});
//			tbxmUrl.onCHANGED(e -> {
//				L.info("OnChaged:" + tbxmUrl.getValue());
//			});
//
//			window.appendChild(tbxmLP);
//			window.appendChild(tbxmUrl);
//
//		}
//
//		if (cookieValueLP == null) {
//			window.appendChild((Component) Lb.ERR("set login:pass").block());
//			return;
//		} else if (cookieValueURL == null) {
//			window.appendChild((Component) Lb.ERR("set jira.url").block());
//			return;
//		}
//
//		//
//		//
//		String[] loginPassArgs = lpGetterBySemicolon.apply(cookieValueLP, false);
//		if (cookieNameURL != null) {
//			loginPassArgs = ARR.merge(loginPassArgs, cookieValueURL);
//		}
//
//
//		boolean hasAuth = true;
//		if (hasAuth) {
//			showPage(loginPassArgs);
//			return;
//		}
//	}
//
//	private void showPage(String[] hlpArgs) {
//
//		Boolean showAll = MAP.getAsBool(ZKSession.getSessionAttrsMap(), SK__SHOW_ALL_TASK, false);
//
//		List<JiraBin.IssueContract> issues;
//		try {
//			issues = JiraBin.loadAllTasksByAuth(hlpArgs);//by user.name & AP:app.org
//		} catch (Exception ex) {
//			issues = ARR.asLL();
////				window.appendChild((Component) Lb.ERR("Check jira-url").block());
//			ZKI.alert(ERR.getRootCause(ex));
//		}
//
//		//
//		//
//
////		List<JiraBin.IssueContract> issues = JiraBin.loadAllTasksAsJOC(1);
//
//		Pare<String, String> sdn = ppi().sdnHybryd();
//
//		Map<String, Path> mapNotes = UFS.toMapByFn(AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn));
//
//
//		for (JiraBin.IssueContract obj : issues) {
//			String keyAndNN = obj.getKey();
//			FormState formState = FormState.ofFormName_OrCreate(sdn, keyAndNN);
//			List<String> labels = SPLIT.allByComma(STR.unwrapBody(obj.getLabels(), "[", "]"));
//			if (labels.contains("rmm") || (labels.contains("old"))) {
//				continue;
//			}
//			if (!formState.existPropsFile()) {
//				String prio = "\n" + "<<<" + obj.getPriorityType() + ">>>";
////				String url = "\n" + "https://job-jira.otr.ru/browse/" + keyAndNN;
////				String jiraHost = AP.get(APK_JIRA_URL, null);
//				String jiraHost = hlpArgs[2];
//				String url = "\n" + UF.normFileEnd(jiraHost) + "/browse/" + keyAndNN;
//				String dataNote = obj.getSummary() + prio + url;
//				Window win = NodeFileTransferMan.AddNewForm.addNewFormAndOpen(keyAndNN, dataNote).val();
//				String color = ARRi.rand(obj.getPriorityType().colorTheme);
//				formState.set(FormState.BG_COLOR, color);
//				formState.set("user", hlpArgs[0]);
//				formState.set("created", QDate.now().mono14_y4s2());
//				ZKS.BGCOLOR(win, color);
//				continue;
//			}
//			NFOpen.openNoteWin_required_open(keyAndNN, sdn);
//			if (showAll) {
//				mapNotes.remove(keyAndNN);
//			}
//		}
//
//		if (showAll) {
//			mapNotes.entrySet().stream().forEach(e -> NFOpen.openNoteWin_required_open(e.getKey(), sdn));
//		}
//	}
//
//
//}
