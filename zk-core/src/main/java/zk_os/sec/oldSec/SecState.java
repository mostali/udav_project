//package zk_os.sec;
//
//import com.github.benmanes.caffeine.cache.LoadingCache;
//import mp.utl_odb.netapp.usr.InaUser;
//import mpe.UCaffeine;
//import mpt.IAnonim;
//import mpu.X;
//import mpu.core.QDate;
//import zk_os.core.Sdn;
//import zk_os.db.net.WebUsr;
//
//import java.util.concurrent.TimeUnit;
//
//@Deprecated
//public class SecState {
//
//	public final QDate last;
//	public final boolean isPlaneHolder;
//	public final boolean isAdminOrOwnerRole;
//	public final boolean isPlaneHolder_Or_AdminOrOwnerRole;
//	public final boolean isOwnerRole, isAdminRole, isEditorRole;
//	//	public final boolean isRunnerRole;
//	public final boolean isAnonim;
//
//	public final Sdn sdn;
//	public final WebUsr usr;
//
//	public static final int CASHE_SEC = (int) TimeUnit.MINUTES.toSeconds(3);
//	public static final int MAX_USERS_CACHE = 100;
//
//	private static final LoadingCache<WebUsr, SecState> cacheSecState = UCaffeine.buildCache(SecState::of, true, CASHE_SEC, MAX_USERS_CACHE);
//
//	@Override
//	public String toString() {
//		return "SecState{" +
//				X.f("[ %s -> %s / %s / %s | %s ]", isOwnerRole, isAdminRole, isEditorRole, isPlaneHolder, isAnonim) +
//				"last=" + last.mono4_h2m2() +
//				", sdn=" + sdn.toStringPath() +
//				", usr=" + usr.getId() + " # " + usr.getAliasOrLogin() +
//				'}';
//	}
//
//	public static SecState getSecState() {
//		WebUsr usr = WebUsr.get(null);
//		if (usr != null) {
//			if (usr.hasPersonalId()) {
//				return cacheSecState.get(usr);
//			}
//		}
//		return new SecState(usr);
//	}
//
//	private SecState(WebUsr webUsr) {
//		this.sdn = Sdn.get();
//		this.usr = webUsr;
//		this.last = QDate.now();
//
//		this.isAnonim = IAnonim.isAnonimUnsafeTrue(webUsr);
//
//		this.isOwnerRole = !isAnonim && Sec.isOwnerRole();
//		this.isAdminRole = !isAnonim && Sec.isAdminRole();
//		this.isEditorRole = !isAnonim && Sec.isEditorRole();
//
//		this.isPlaneHolder = !isAnonim && SecMan.isPlaneOwner();
//
//		this.isAdminOrOwnerRole = isOwnerRole || isAdminRole;
//
//		this.isPlaneHolder_Or_AdminOrOwnerRole = isAdminOrOwnerRole || isPlaneHolder;
//
//	}
//
//	public static SecState of(WebUsr webUsr) {
//		return new SecState(webUsr);
//	}
//}
