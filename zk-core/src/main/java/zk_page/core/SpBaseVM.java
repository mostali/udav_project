package zk_page.core;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;
import mpc.env.Env;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import mpc.fs.query.QueryUrl;
import utl_web.UWeb;
import zk_os.AppZosConfig;
import zk_os.AppZosCore;
import zk_page.index.RSPath;
import zk_old_core.sd.core.SdMan;
import zk_old_core.mdl.PageDirModel;
import zk_os.sec.ZKUser;
import zk_old_core.sd.Sd3EE;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author dav 13.05.2022   09:37
 */
public class SpBaseVM implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(SpVM.class);

	private PageDirModel pageDirModel;

//	protected String subdomain3;

	protected String pathNU;

	protected String queryStr;
	protected transient QueryUrl queryUrl;

	public QueryUrl getQuery() {
		return queryUrl == null ? queryUrl = QueryUrl.of(queryStr) : queryUrl;
	}

//	transient QueryUrl query;

//	public QueryUrl getQuery_org() {
//		return queryUrl != null ? queryUrl : (queryUrl = ZK.getRequestQuery());
//	}


	//	protected String address;

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

//		subdomain3 = AppZosWeb.getSd3(servletRequest);

//		address = (String) execution.getAttribute("javax.servlet.forward.servlet_path");
//		if (address.startsWith("/")) {
//			address = address.substring(1);
//		}

		queryStr = UWeb.getQueryStringForwarded(servletRequest);

		checkAndApplyAuthBySuperKey();

		roles = ROLE.getRolesFlags();

		usr_cookie = ZKUser.getUserUUID().toString();

		isMobile = UWeb.isMobile();
//		ZK.showInfoInLog(":::" + ZKUser.getUserUUID());

	}

	public void checkAndApplyAuthBySuperKey() {
		Sec.checkAndApplyAuthBySuperKey(getQuery());
	}

	public PageDirModel findPageDirModel(PageDirModel... defRq) throws Sd3EE {
		return pageDirModel != null ? pageDirModel : (pageDirModel = SdMan.findPageModel(ppi(), defRq));
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

	public String subdomain3() {
		return ppi.subdomain3();
	}

//	public String subdomain3AsIndex() {
//		String sd3 = ppi.subdomain3();
//		return X.empty(sd3) ? PageSP.SD3_INDEX_ALIAS : sd3;
//	}

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
			sbCfg.TABNL(1, "master-repo:" + AppZosCore.getMasterRepo() + ", index:" + AppZosCore.getMasterPage());
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
}
