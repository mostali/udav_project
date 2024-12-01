package mp.utl_odb.tree.trees;

import mp.utl_odb.tree.UTree;

// for store html snippets
public class UTreeH {
	private static final UTree _INSTANCE_H = new UTree("_INSTANCE_HTML");

	public static void setH(String key, String value) {
		_INSTANCE_H.put(key, value);
	}

	public static void removeKeyH(String key) {
		_INSTANCE_H.remove(key);
	}

	public static String getH(String key) {
		return _INSTANCE_H.get(key, null);
	}
}
