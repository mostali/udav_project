package zk_notes.node_srv.types.jqlMsg;

import lombok.RequiredArgsConstructor;
import mpc.html.EHtml5;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import udav_net.bincall.JiraBin;
import zk_os.core.Sdn;
import zk_page.ZKSession;
import zk_page.ZkCookie;

import java.util.Map;

@RequiredArgsConstructor
public class JtUserContext {
	public static final String SK__SHOW_ALL_TASK = "ShowAllTask";
	public static final String CK_HLP = "jira_tasks_hlp";
	public static final String CK_PROJECT = "jira_tasks_project";
	public static final String CK_STATUS = "jira_tasks_status";
	public static final String CK_ISSUETYPE = "jira_tasks_issuetype";
	public final Sdn sdn;
	public final String[] hlpArgs;
	public final JiraBin.JqlLoader.JqlFilterCustom jiraCustomFilter;

	public static JtUserContext get() {
		return get0().val();
	}

	public static Pare<Boolean, JtUserContext> get0() {
		JtUserContext fromCookie = getFromCookie();
		if (fromCookie != null) {
			return Pare.of(true, fromCookie);
		}
		JtUserContext fromSesseion = getFromSesseion();
		if (fromSesseion != null) {
			return Pare.of(false, fromCookie);
		}
		return null;
	}

	public static JtUserContext getFromSesseion() {

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

		return new JtUserContext(sdn, hlpArgs, jiraCmdFilter);
	}

	public static JtUserContext getFromCookie() {

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

		return new JtUserContext(sdn, hlp, jiraCmdFilter);
	}

}
