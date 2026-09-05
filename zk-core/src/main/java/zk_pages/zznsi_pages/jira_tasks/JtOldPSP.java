//package zk_pages.zznsi_pages.jira_tasks;
//
//import lombok.SneakyThrows;
//import mpc.env.AP;
//import mpc.fs.UF;
//import mpc.fs.UFS;
//import mpc.fs.UUFS;
//import mpc.map.MAP;
//import mpe.core.ERR;
//import mpu.IT;
//import mpu.core.ARR;
//import mpu.core.ARRi;
//import mpu.core.QDate;
//import mpu.func.Function2;
//import mpu.pare.Pare;
//import mpu.paree.Paree3;
//import mpu.str.SPLIT;
//import mpu.str.STR;
//import mpu.str.UST;
//import mpu.str.TKN;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Window;
//import udav_net.bincall.JiraBin;
//import udav_net.bincall.JiraBinExt;
//import udav_net.bincall.jira.IssueContract;
//import zk_com.base.Lb;
//import zk_com.base.Tbx;
//import zk_com.base_ctr.Menupopup0;
//import zk_com.core.IZState;
//import zk_com.sun_editor.IPerPage;
//import zk_form.WithLogo;
//import zk_form.notify.ZKI;
//import zk_notes.factory.NFForm;
//import zk_notes.factory.NFNew;
//import zk_notes.node_state.impl.FormState;
//import zk_os.coms.AFC;
//import zk_os.sec.ROLE;
//import zk_page.ZKR;
//import zk_page.ZKS;
//import zk_page.ZKSession;
//import zk_page.ZkCookie;
//import zk_page.core.PageRoute;
//import zk_page.core.PageSP;
//import zk_page.core.SpVM;
//import zk_notes.coms.SeNoteTbxm;
//import zk_page.with_com.WithSearch;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//
//@PageRoute(pagename = "tasks", role = ROLE.ANONIM)
//public class JtOldPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {
//
//	private static final Logger L = LoggerFactory.getLogger(JtOldPSP.class);
//
//	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";
//
//	public static final String APK_JIRA_URL = "jira.url";
//
////	private LogPageHeaderProps pageHeader = null;
//
//	public JtOldPSP(Window window, SpVM spVM) {
//		super(window, spVM);
//	}
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
//		nw();
//	}
//
//	private void nw() {
//		Function2<String, Boolean, String[]> lpGetter = (inputValue, skipErr) -> {
//			try {
//				IT.state(!inputValue.contains(";"), "illegal char ';' for store value in cookie");
//				String[] lp = TKN.two(inputValue, ":", null);
//				IT.state(lp != null, "Use pattern login:password");
//				IT.notBlank(lp[0], "set login");
//				IT.notBlank(lp[1], "set password");
//				return lp;
//			} catch (Exception ex) {
//				if (skipErr) {
//					ZKI.alert(ex);
//					return null;
//				}
//				throw ex;
//			}
//		};
//		String cookieNameLP = "jira.lp";
//		String cookieNameURL = APK_JIRA_URL;
//		String cookieValueLP = ZkCookie.getCookieValue(cookieNameLP, null);
//		String cookieValueURL = ZkCookie.getCookieValue(cookieNameURL, null);
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
//					String[] lp = lpGetter.apply(cookieValueLP, false);
//					tbxmLP.setValue(lp[0]);
//				} catch (Exception ex) {
//					L.error("Illegal cookie value:" + cookieValueLP, ex);
//					ZkCookie.deleteCookie(cookieNameLP);
//				}
//			}
//
//			tbxmLP.onOK(e -> {
//				String inputValue = tbxmLP.getValue();
//				String[] lp = lpGetter.apply(inputValue, true);
//				if (lp == null) {
//					ZkCookie.deleteCookie(cookieNameLP);
//				} else {
//					ZkCookie.setCookie(cookieNameLP, inputValue, false);
//				}
//				ZKR.restartPage();
//			});
//
//			tbxmUrl.onOK(e -> {
//				String inputValue = tbxmUrl.getValue();
//				if (UST.URL(inputValue, null) == null) {
//					ZkCookie.deleteCookie(cookieNameLP);
//				}
//				ZkCookie.setCookie(cookieNameURL, inputValue, false);
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
//		String[] loginPassArgs = lpGetter.apply(cookieValueLP, false);
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
//		List<IssueContract> issues;
//		try {
//			issues = JiraBinExt.loadAllTasks_Models(hlpArgs,"ARP,SUP,EXP,BSK,NSI");//by user.name & AP:app.org
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
//		Pare<String, String> sdn = ppi().sdnUnsafe();
//
//		Map<String, Path> mapNotes = UUFS.toMapByFn(AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn));
//
//
//		for (IssueContract obj : issues) {
//			String keyAndNN = obj.getKey();
//			FormState formState = FormState.ofName(sdn, keyAndNN);
//			List<String> labels = SPLIT.allByComma(STR.unwrapBody(obj.getLabels(), "[", "]"));
//			if (labels.contains("rmm") || (labels.contains("old"))) {
//				continue;
//			}
//			if (!formState.existPropsFile()) {
//				String prio = "\n" + "<<<" + obj.getPriorityType() + ">>>";
////				String url = "\n" + "https://job-jira.otr.ru/browse/" + keyAndNN;
//				String url = "\n" + UF.normFileEnd(AP.get(APK_JIRA_URL, null)) + "/browse/" + keyAndNN;
//				String dataNote = obj.getSummary() + prio + url;
//				Window win = NFNew.openNewAlertINE_inCurrentSdn(keyAndNN, dataNote).val();
//				String color = obj.getPriorityType().zkColor.nextColor();
//				formState.set(FormState.BG_COLOR, color);
//				formState.set("user", hlpArgs[0]);
//				formState.set("created", QDate.now().mono14_y4s2());
//				ZKS.BGCOLOR(win, color);
//				continue;
//			}
//			NFForm.openFormRequired( sdn,keyAndNN);
//			if (showAll) {
//				mapNotes.remove(keyAndNN);
//			}
//		}
//
//		if (showAll) {
//			mapNotes.entrySet().stream().forEach(e -> NFForm.openFormRequired( sdn,e.getKey()));
//		}
//	}
//
//
//}
