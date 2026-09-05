package zk_os.sec;

import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.net.query.QueryUrl;
import mpe.NT;
import mpe.core.UBool;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare3;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import zk_notes.types.AuthRspContract;
import zk_os.AppZosConfig;
import zk_os.db.WebUsrService;
import zk_os.db.net.RootWebUsr;
import zk_os.db.net.WebUsr;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class SecAuth {

	public static final String ZN_REMOTE_USER = "ZN-Remote-User";

	public static final String SKA = "ska";

	@Deprecated
	public static boolean trySKA(String queryString) {
		if (!UBool.isTrue(APP.SIMPLE_AUTH_ENABLE)) {
			return false;
		}
		QueryUrl qUrl = QueryUrl.of(queryString);
		String firstAsStr = qUrl.getFirstAsStr(SKA, null);
		return AppZosConfig.SUPER_KEY.equals(firstAsStr);
	}

	public static boolean trySetAuth_byHeaderUserUuid(ServletRequest request) {
		Authentication auth = Sec.getAuth(null);
		if (auth != null) {
			return false;
		}
		String header = ((HttpServletRequest) request).getHeader(ZN_REMOTE_USER);
		return trySetAuth_byHeaderUserUuid(header);
	}


	public static boolean trySetAuth_byHeaderUserUuid(String header) {
		if (X.empty(header)) {
			return false;
		}
		if (!header.equals(AppZosConfig.SUPER_KEY_HEADER)) {
			return false;
		}
		WebUsr webUsr = RootWebUsr.get(null);
		if (webUsr == null) {
			webUsr = RootWebUsr.load();
		}

		Sec.setAuth(webUsr.createZAuth(), null);//

		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("Apply {} setAuthByUserUuid", webUsr.getFirst_name());
		}
		return true;
	}

	public static boolean hasSkaStrict(QueryUrl queryUrl, boolean... any) {
		if (AppZosConfig.SUPER_KEY == null) {
			return false;
		}
		String super_key = queryUrl.getFirstAs(SKA, String.class, null);
		if (X.empty(super_key)) {
			return false;
		}
		return ARG.isDefEqTrue(any) ? true : AppZosConfig.SUPER_KEY.equals(super_key);
	}

	public static void checkAndApplyAuthBySKA(String superKey) {
		boolean isSuperKeyEquals = AppZosConfig.SUPER_KEY.equals(superKey);
		if (!isSuperKeyEquals) {
			return;
		}
		Authentication skBear = Sec.getSkBeaRoot();
		SecurityContextHolder.getContext().setAuthentication(skBear); //ska
		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("ENABLE checkAndAuthBySuperKey:" + skBear);
		}
	}

	public static class ByToken {

		public static boolean createZAuth_ByToken_AndApply(Pare3<Boolean, String, String> authRslt, String name) {

			WebUsr webUsr = createWebUser_ByToken(authRslt.ext(), name);

			if (webUsr != null) {
				Sec.setAuth(webUsr.createZAuth(), true);
				return true;
			}

			return false;
		}

		@SneakyThrows
		private static WebUsr createWebUser_ByToken(String authRspJson, String name) {

			AuthRspContract authRspContract = AuthRspContract.of(authRspJson);

			NT nt = authRspContract.getNet();
			switch (nt) {
				case TG:
				case VK: {

					long nid = authRspContract.getNid();
//					String name = authRspContract.getN(null);

					WebUsrService webUsrService = WebUsrService.get();

					WebUsr webUsr;

					WebUsr webUsrExisted = webUsrService.loadUserByNetNid(nt.name(), nid);
					if (webUsrExisted != null) {
						webUsrService.updateIfEmptyNameOrHasDiff(webUsrExisted, name);
						return webUsrExisted;
					}

					webUsr = webUsrService.createAndSaveNewWebUsr(nt, nid, name);

					return webUsr;
				}

				default:
					throw new WhatIsTypeException(nt);

			}

		}
	}

}
