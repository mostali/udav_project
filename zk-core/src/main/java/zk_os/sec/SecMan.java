package zk_os.sec;

import mpe.call_msg.core.INodeID;
import mpe.call_msg.core.ISpaceID;
import mpt.IAnonim;
import zk_os.coms.AFC;
import zk_os.coms.AFCSec;
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

//	public static boolean isAllowedNode_FORM_RUN(INodeID node) {
//		return isAllowedNode_FORM_RUN(WebUsr.get(), node);
//	}

//	public static boolean isAllowedNode_FORM_RUN(WebUsr usr, INodeID node) {
//		return isAllowed_FORM_OPERATION(usr, node, AFCSec.I_SECR);
//	}

	//

//	public static boolean isAllowedNode_FORM_EDIT(INodeID node) {
//		return isAllowedNode_FORM_EDIT(WebUsr.get(), node);
//	}

//	public static boolean isAllowedNode_FORM_EDIT(WebUsr usr, INodeID node) {
//		return isAllowedNode_FORM_OPERATION(usr, node, AFCSec.I_SECE);
//	}

	//

//	public static boolean isAllowedNode_FORM_VIEW(INodeID node) {
//		return isAllowedNode_FORM_VIEW(WebUsr.get(), node);
//	}

//	public static boolean isAllowedNode_FORM_VIEW(WebUsr usr, INodeID node) {
//		return isAllowedNode_FORM_OPERATION(usr, node, AFCSec.I_SECV);
//	}

	//

//	public static boolean isAllowedNode_FORM_OPERATION(INodeID node, int oper) {
//		return isAllowedNode_FORM_OPERATION(WebUsr.get(), node, oper);
//	}

//	public static boolean isAllowedNode_FORM_OPERATION(WebUsr usr, INodeID node, int oper) {
////		return usr.isMainRole_ADMIN_OWNER() || usr.isEditorFor(node.sdn()) || AFCSec.isAllowed(usr, AFC.SpaceType.NODES, node, oper);
//		return isAllowed_FORM_OPERATION(usr, node, oper);
//	}

//	public static boolean isAllowed_FORM_OPERATION(WebUsr usr, ISpaceID formID, int oper) {
//		return isAllowed_OPERATION(usr, formID, AFC.SpaceType.NODES, oper);
//	}

//	public static boolean isAllowed_PAGE_OPERATION(WebUsr usr, ISpaceID page, int oper) {
//		return isAllowed_OPERATION(usr, page, AFC.SpaceType.PAGES, oper);
//	}

//	public static boolean isAllowed_SPACE_OPERATION(WebUsr usr, ISpaceID page, int oper) {
//		return isAllowed_OPERATION(usr, page, AFC.SpaceType.SPACES, oper);
//	}

	public static boolean isAllowed_OPERATION(WebUsr usr, ISpaceID node, AFC.SpaceType spaceType, UO oper) {
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
