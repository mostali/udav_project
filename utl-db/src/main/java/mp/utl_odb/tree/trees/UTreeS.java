package mp.utl_odb.tree.trees;

import mp.utl_odb.tree.UTree;

// System
public class UTreeS {
	private static final UTree _INSTANCE_S = new UTree("_INSTANCE_SYSTEM");

	public static void setS(String key, String value) {
		_INSTANCE_S.put(key, value);
	}

	public static void removeKeyS(String key) {
		_INSTANCE_S.remove(key);
	}

	public static String getS(String key) {
		return _INSTANCE_S.get(key, null);
	}
}
