package zk_page.core;

import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpc.fs.query.QueryUrl;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import utl_web.UWeb;
import zk_notes.AppNotes;
import zk_os.AFCC;
import zk_os.AppZosConfig;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.ZKUser;
import zk_page.index.RSPath;
import zk_page.node_state.FormState;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public void checkAndApplyAuthBySuperKey() {
		Sec.checkAndApplyAuthBySuperKey(getQuery());
	}

	public boolean isRootPage() {
		return ppi.isEmptyPagename();
	}

	public boolean isPathAdmin() {
		return isFromPath("admin");
	}

	public boolean isFromPath(String path) {
		return pagename().equals(path);
	}

	public String pagename() {
		return ppi.pagename();
	}

	public String pagenameOr() {
		return X.empty(pagename()) ? AFCC.PAGE_INDEX_ALIAS : subdomain3();
	}

	public String subdomainOrIndex() {
		return X.empty(subdomain3()) ? AFCC.SD3_INDEX_ALIAS : subdomain3();
	}

	public String subdomain3() {
		return ppi.subdomain3();
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
			sbCfg.TABNL(1, "cfg:" + AppZosConfig.toStringLog());
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

	public boolean isRootBlankQuery(String namedQeuryArg) {
		return isRootPage() && getQuery().hasBlank(namedQeuryArg);
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
				return rsPath.toPlaneLink(ppi0.subdomain3());
			case PAGE:
				return rsPath.toPlanPage(ppi0.subdomain3(), ppi0.pagename(), path2_nodeName);
			default:
				throw new WhatIsTypeException();
		}
	}

	public Path sdnPath(String... item) {
		Path sd = Paths.get(X.empty(subdomain3()) ? AFCC.SD3_INDEX_ALIAS : subdomain3());
		Path page = Paths.get(X.empty(pagename()) ? AFCC.PAGE_INDEX_ALIAS : pagename());
		return ARG.isDef(item) ? sd.resolve(page).resolve(item[0]) : sd.resolve(page);
	}


	public List<FormState> getAllFormComStates() {
		return getAllFormStates().stream().map(FormState::comState).collect(Collectors.toList());
	}

	public List<FormState> getAllFormStates() {
		Pare<String, String> sdn = Pare.of(subdomain3(), pagename());
		return AppNotes.getAllNotesOfPage(sdn).stream().//;
				map(p -> FormState.ofFormDir(sdn, p)).collect(Collectors.toList());
	}

	public Map<int[], FormState> getAllFormComStatesAsGrid() {
		Map<int[], FormState> comsCoors = getAllFormComStates().stream().map(s -> Pare.of(s, s.getTopLeftArgs(null))).filter(p -> p.val() != null).collect(Collectors.toMap(k -> k.val(), v -> v.key()));
		return comsCoors;
	}
}
