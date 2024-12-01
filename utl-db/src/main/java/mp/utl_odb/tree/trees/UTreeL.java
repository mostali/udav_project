package mp.utl_odb.tree.trees;

import mp.utl_odb.tree.CtxtlDb;
import mp.utl_odb.tree.UTree;

//Limited
public class UTreeL {
	private static final UTree _INSTANCE_L = new UTree("_INSTANCE_LIMITED");

	public static void setL(String key, String value) {
		_INSTANCE_L.put(key, value);
	}

	public static void removeKeyL(String key) {
		_INSTANCE_L.remove(key);
	}

	@Deprecated
	public static String getL(String key) {
		return _INSTANCE_L.get(key, null);
	}

	@Deprecated
	public static String getL_TLA(String key, CtxtlDb.TimeAccess... timeAccesses) throws CtxtlDb.UtreeDelayException {
		return _INSTANCE_L.getTA(key, timeAccesses);
	}

	@Deprecated
	public static String getL_TLA(String key, long delay) throws CtxtlDb.UtreeDelayException {
		return _INSTANCE_L.getTA(key, delay);
	}
}
