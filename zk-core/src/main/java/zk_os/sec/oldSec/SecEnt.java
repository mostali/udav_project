//package zk_os.sec;
//
//import mpu.core.EQ;
//import zk_notes.node_state.AppStateFactory;
//import zk_notes.node_state.impl.PlaneState;
//import zk_os.db.net.WebUsr;
//
//@Deprecated
//public class SecEnt {
//
////	public static boolean isPlaneOwner() {
////		return isPlaneOwner(Sdn.PLANERQ());
////	}
//
////	public static boolean isPlaneOwner(String plane) {
////		return !Sec.isAnonimUnsafe() && isPlaneOwner(Sec.getUser(), plane);
////	}
//
//	public static boolean isPlaneOwner(WebUsr user, String plane) {
//		PlaneState planeState = AppStateFactory.forPlane(plane);
//		return EQ.equalsSafe(planeState.get_USER(null), user.getAliasOrLogin());
////		return X.equalsSafe(plane, user. getNetNidNamed(null));
//	}
//}
