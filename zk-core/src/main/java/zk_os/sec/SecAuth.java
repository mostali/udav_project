package zk_os.sec;

import mpc.env.APP;
import mpc.net.query.QueryUrl;
import mpu.X;
import mpu.core.ARG;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import udav_net.apis.zznote.NoteApi;
import zk_os.AppZosConfig;
import zk_os.db.net.RootZkosWebUsr;
import zk_os.db.net.WebUsr;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class SecAuth {


	public static final String ZN_REMOTE_USER = "ZN-Remote-User";

	public static final String SKA = "ska";

	@Deprecated
	public static boolean trySKA(String queryString) {
		if (!APP.SIMPLE_AUTH_ENABLE) {
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
		WebUsr webUsr = RootZkosWebUsr.get(null);
		if (webUsr == null) {
			webUsr = RootZkosWebUsr.load();
		}
		ZAuth zAuth = webUsr.createZAuth(true);
		WebUsr usr = zAuth.webUsr();
		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("Apply {} setAuthByUserUuid", usr.getFirst_name());
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

	public static void checkAndApplyAuthBySuperKey(String superKey) {
		boolean isSuperKeyEquals = AppZosConfig.SUPER_KEY.equals(superKey);
		if (!isSuperKeyEquals) {
			return;
		}
		Authentication skBear = Sec.getSkBear();
		SecurityContextHolder.getContext().setAuthentication(skBear);
		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("ENABLE checkAndAuthBySuperKey:" + skBear);
		}
	}
}
