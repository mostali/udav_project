package zk_page.core;

import mpc.env.Env;
import mpc.net.query.QueryUrl;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare3;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import mpe.call_msg.core.NodeID;
import utl_web.UWeb;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.SecAuth;
import zk_os.sec.ZKUser;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

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

		applyAuth_BySKA();

		roles = ROLE.getRolesFlags();

		usr_cookie = ZKUser.getUserUUID().toString();

		isMobile = UWeb.isMobile();

	}

//	private boolean checkedSka = false;

	public void applyAuth_BySKA() {
//		if (!checkedSka) {
		Sec.applyAuth_bySKA(getQuery());
//			checkedSka = true;
//		}
	}

	public String pagenameRq() {
		return ppi.pagenameRq();
	}

	public String subdomain3Rq() {
		return ppi.planeRq();
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
			sbRequest.TABNL(1, "sec-level:" + ARR.as(ROLE.EDITOR.has(roles), ROLE.ADMIN.has(roles), ROLE.OWNER.has(roles)));
			sbRequest.TABNL(1, "is_mobile:" + isMobile);
			sbRequest.TABNL(1, "usr_cookie:" + usr_cookie);
		}

		Sb sbSec = Sec.info();

		return Pare3.of(sbCfg, sbRequest, sbSec);
	}

	public Sdn sdn() {
		return ppi().sdnUnsafe();
	}

	public NodeID nodeId(NodeID... defRq) {
		String item = ppi().item();
		if (item != null) {
			return NodeID.of(sdn(), item);
		}
		return ARG.toDefThrowMsg(() -> X.f("Except item"), defRq);
	}

	public PagePathInfo ppi() {
		return ppi;
	}

	private PagePathInfoWithQuery pagePathInfoWithQuery;

	public PagePathInfoWithQuery ppiq() {
		return pagePathInfoWithQuery != null ? pagePathInfoWithQuery : (pagePathInfoWithQuery = PagePathInfoWithQuery.current());
	}

	public boolean hasSkaStrict(boolean... any) {
		return SecAuth.hasSkaStrict(getQuery(), any);
	}


}
