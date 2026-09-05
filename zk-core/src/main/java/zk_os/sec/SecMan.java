package zk_os.sec;

import mpe.cmsg.ns.ISpaceID;
import mpt.IAnonim;
import zk_os.coms.AFCSec;
import zk_os.coms.SpaceType;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;

public class SecMan {

	public static boolean isAllowedEditPlane() {
		return isAllowedEditPlane(Sdn.PLANE());
	}

	public static boolean isAllowedEditPlane(Sdn sdn) {
		return isAllowedEditPlane(sdn.plane());
	}

	public static boolean isAllowedEditPlane(String plane) {
		return isAllowedEditPlane(WebUsr.get(), plane);
	}

	public static boolean isAllowedEditPlane(WebUsr webUsr, Sdn sdn) {
		return isAllowedEditPlane(webUsr, sdn.plane());
	}

	public static boolean isAllowedEditPlane(WebUsr webUsr, String plane) {
		return webUsr.isMainRole_ADMIN_OWNER() || webUsr.isEditorFor(plane);
	}

	//
	//

	public static boolean isAllowed_OPERATION(WebUsr usr, ISpaceID node, SpaceType spaceType, UO oper) {
		return usr.isMainRole_ADMIN_OWNER() || usr.isEditorFor(node.spaceName()) || AFCSec.isAllowed(usr, spaceType, node, oper);
	}

	public static boolean isOwner() {
		return WebUsr.get().isMainRole_OWNER();
	}

	public static boolean isAdmin() {
		return WebUsr.get().isMainRole_ADMIN();
	}

	public static boolean isAnonim() {
		return WebUsr.get().isMainRole_ANONIM();
	}

	public static boolean isOwnerOrAdmin() {
		return WebUsr.get().isMainRole_ADMIN_OWNER();
	}

	public static boolean isNotAnonimUnsafe() {
		return !isAnonimUnsafe();
	}

	public static boolean isAnonimUnsafe() {
		return IAnonim.isAnonimUnsafeTrue(Sec.getUser(null));
	}
}
