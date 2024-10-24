package zk_old_core.apis.impl;

import lombok.SneakyThrows;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.trees.api_expiremental.TreeCallEE;
import mp.utl_odb.tree.trees.api_expiremental.UTreeRest;
import mpc.exception.EmptyException;
import mpu.str.USToken;
import mpc.str.condition.StringConditionType;
import mpu.core.QDate;
import mpc.fs.query.QueryUrl;
import mpu.pare.Pare;
import org.zkoss.zul.Window;
import mpe.http.CleanDataResponseException;
import utl_rest.StatusException;
import zk_old_core.old.WithAgna;
import zk_form.WithHeadRsrc;
import zk_form.WithLogo;
import zk_old_core.old.WithUsrLogo;
import zk_old_core.AppZosCore_Old;
import zk_form.notify.ZKI_Log;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

@PageRoute(sd3 = ".*", sd3_eqt = StringConditionType.REGEX, pagename = "$", eqt = StringConditionType.STARTS)
public class ApiCallPage_OLD extends PageSP implements WithLogo, WithAgna, WithHeadRsrc, WithUsrLogo {
	public static final String D3_URLPATH_PREFIX = "$";
	public static final String QUERY_ARG_VAL = "v";

	public ApiCallPage_OLD(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Integer oid = null;
	String sd3 = null, pagename = null;
	String path = null;
	String path0 = null, method = null, path2 = null, path3 = null;
	QDate now;

	public static boolean is(String pagename) {
		return pagename.startsWith(D3_URLPATH_PREFIX);
	}

	@SneakyThrows
	public void buildPageImpl() {

		now = QDate.now();

		PagePathInfoWithQuery ppiq = ppiq();

		this.sd3 = ppiq.subdomain3();
//		pagename0 = ppiq.pagename();//$
		pagename = ppiq.pathStr(1, null);
//		String pagepame0 = ppiq.path_str(1, null);
		method = ppiq.pathStr(2, null);
//		method = ppiq.path_str(2, null);
//		path3 = ppiq.path_str(3, null);

		path = ppiq.path();

		QueryUrl queryUrl = ppiq.queryUrl();

		if (true) {
			String k = queryUrl.getFirstAsStr("k", null);
			String v = queryUrl.getFirstAsStr("v", null);
			Object call = TreeCallEE.TreeCall.byKeyValue(Pare.of(sd3.equals("") ? "." : sd3, pagename.equals("") ? "." : pagename), method, k, v).call();
			throw CleanDataResponseException.of(call.toString(), true);
		}
		oid = USToken.lastGreedy(sd3, QUERY_ARG_VAL, Integer.class);


		ZKI_Log.log("%s[0], %s[1], %s[2], %s[3]", path0, method, path2, path3);

		String key = D3_URLPATH_PREFIX.equals(pagename) ? oid.toString() : pagename.substring(D3_URLPATH_PREFIX.length());


		UTree d3 = AppZosCore_Old.TREE_SUBDOMAINS();

		String val = queryUrl.getFirstAsStr(QUERY_ARG_VAL, null);

		try {
			String[] pathArgs = {method, path2};
			String dataRsp = UTreeRest.apply(d3, oid.toString(), pathArgs, key, val);
			throw CleanDataResponseException.of(dataRsp, true);
		} catch (EmptyException ex) {
			throw StatusException.C409(key);
		}

	}


}
