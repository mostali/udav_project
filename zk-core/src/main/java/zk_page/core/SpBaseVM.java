package zk_page.core;

import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpc.net.query.QueryUrl;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import utl_web.UWeb;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.ZKUser;
import zk_page.index.RSPath;
import zk_notes.node_state.FormState;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpBaseVM implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(SpVM.class);

	protected String pathNU;

	protected String queryStr;
	protected transient QueryUrl queryUrl;

	public QueryUrl getQuery() {
		return queryUrl == null ? queryUrl = QueryUrl.of(queryStr) : queryUrl;
	}

	protected String usr_cookie;
	protected boolean forwarded;

	protected boolean isMobile;

	boolean[] roles = {false, false, false, false};

	private PagePathInfo ppi;

	protected void initRequestAttributes() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();

		pathNU = utl_web.UWeb.getServletPathWithoutContext(servletRequest);

		forwarded = execution.isForwarded();
		if (!forwarded) {
			throw new IllegalStateException("Who is call not forward model? " + pathNU);
		}
		ppi = new PagePathInfo(servletRequest, (Boolean) null);

		queryStr = UWeb.getQueryStringForwarded(servletRequest);

		checkAndApplyAuthBySuperKey();

		roles = ROLE.getRolesFlags();

		usr_cookie = ZKUser.getUserUUID().toString();

		isMobile = UWeb.isMobile();

	}

	private boolean checkedSka = false;

	public void checkAndApplyAuthBySuperKey() {
		if (!checkedSka) {
			Sec.checkAndApplyAuthBySuperKey(getQuery());
			checkedSka = true;
		}
	}

	public boolean isRootPage() {
		return ppi.isEmptyPagename();
	}

	public String pagenameRq() {
		return ppi.pagenameRq();
	}

	public String subdomain3Rq() {
		return ppi.subdomain3Rq();
	}

	public boolean isUserOwner() {
		return ROLE.OWNER.has(roles);
	}

	public boolean isUserAdmin() {
		return ROLE.ADMIN.has(roles);
	}

	public boolean isUserSimple() {
		return ROLE.USER.has(roles);
	}

	public Pare3<Sb, Sb, Sb> showDebugLog() {

		Sb sbCfg = new Sb();
		{
			sbCfg.NL("Cfg");
			sbCfg.TABNL(1, "rpa:" + Env.RPA);
		}

		Sb sbRequest = new Sb();
		{
			sbRequest.TABNL(1, "ppi:" + ppi.toString());
			sbRequest.TABNL(1, "query:" + QueryUrl.of(queryStr).buildReport());
			sbRequest.TABNL(1, "path:" + pathNU + ", forwarded:" + forwarded);
			sbRequest.TABNL(1, "sec-level:" + ARR.as(isUserSimple(), ROLE.EDITOR.has(), isUserSimple(), isUserAdmin(), isUserOwner()));
			sbRequest.TABNL(1, "is_mobile:" + isMobile);
			sbRequest.TABNL(1, "usr_cookie:" + usr_cookie);
		}

		Sb sbSec = Sec.info();

		return Pare3.of(sbCfg, sbRequest, sbSec);
	}

	public PagePathInfo ppi() {
		return ppi;
	}

	private PagePathInfoWithQuery pagePathInfoWithQuery;

	public PagePathInfoWithQuery ppiq() {
		return pagePathInfoWithQuery != null ? pagePathInfoWithQuery : (pagePathInfoWithQuery = PagePathInfoWithQuery.current());
	}

	public boolean hasSka() {
		return Sec.hasSka(getQuery());
	}

	public String getUrlTo(RSPath rsPath) {
		return getUrlTo(rsPath, null);
	}

	public String getUrlTo(RSPath rsPath, String path2_nodeName) {
		PagePathInfo ppi0 = ppi();
		switch (rsPath) {
			case ROOT:
				return rsPath.toRootLink();
			case PLANE:
				return rsPath.toPlaneLink(ppi0.subdomain30());
			case PAGE:
				return rsPath.toPageLink(ppi0.subdomain30(), ppi0.pagename0(), path2_nodeName);
			default:
				throw new WhatIsTypeException();
		}
	}

	public List<FormState> getAllFormComStates() {
		return getAllFormStates().stream().map(FormState::stateCom).collect(Collectors.toList());
	}

	public List<FormState> getAllFormStates() {
		Pare<String, String> sdn = ppi().sdnRq();
		return AFC.FORMS.DIR_FORMS_LS_CLEAN(sdn).stream().//;
				map(p -> AppStateFactory.ofFormDir_orCreate(sdn, p)).collect(Collectors.toList());
	}

	public Map<int[], FormState> getAllFormComStatesAsGrid() {
		Map<int[], FormState> comsCoors = getAllFormComStates().stream().map(s -> Pare.of(s, s.fields().get_TOP_LEFT(null))).filter(p -> p.val() != null).collect(Collectors.toMap(k -> k.val(), v -> v.key()));
		return comsCoors;
	}
}
