package mp.utl_odb.tree.trees;


import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpc.env.Env;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UTreeNext {

	public static <K, V> Map.Entry<K, V> nextIndex0(String dbName, String key, Map<K, V> map, Map.Entry<K, V>... defRq) {
		Set<Map.Entry<K, V>> collection = map.entrySet();
		return nextIndex0(dbName, key, collection, defRq);
	}

	public static <T> T nextIndex0(String dbName, String key, Collection<T> collection, T... defRq) {
		if (X.notEmpty(collection)) {
			if (X.sizeOf(collection) == 1) {
				return ARRi.first(collection);
			} else {
				long next = nextIndex(dbName, key, 0, collection.size() - 1);
				return ARRi.item(collection, (int) next);
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Collection is empty"), defRq);
	}

	public static long nextIndex0(String dbName, String key, long maxindex) {
		return nextIndex(dbName, key, 0, maxindex);
	}

	public static <T> T nextItemDEFAULT_DB(Collection<T> items, Object key, Object keyExt) {
		return nextItemDEFAULT_DB(items, key + ":" + keyExt);
	}

	public static <T> T reverseItem(Collection<T> items, Object key, Object keyExt) {
		return reverseItem(items, key + ":" + keyExt);
	}

	public static <T> T reverseItem(Collection<T> items, Object key) {
		IT.NE(items);
		long next = reverseIndex(Env.getAppName(), key + "", 0, items.size() - 1, true);
		return ARRi.item(items, (int) next);
	}

	public static <T> T nextItemDEFAULT_DB(Collection<T> items, Object key) {
		IT.NE(items);
		long next = nextIndex(Env.getAppName(), key + "", 0, items.size() - 1, true);
		return ARRi.item(items, (int) next);
	}

	public static long nextIndex(String dbName, String key, long minIndex, long maxindex, boolean... skipError) {
		minIndex = checkIndexOrReplace(minIndex, maxindex, skipError);
		IT.state(minIndex <= maxindex);
		UTree tree = TREE(dbName);
		Long current = tree.getValueAs(key, Long.class, null);
		current = current == null ? minIndex : ++current;
		long newIndex = current > maxindex ? minIndex : current;
		tree.putAppend(key, newIndex);
		return newIndex;
	}

	public static long reverseIndex(String dbName, String key, long minIndex, long maxindex, boolean... skipError) {
		minIndex = checkIndexOrReplace(minIndex, maxindex, skipError);
		IT.state(minIndex <= maxindex);
		UTree tree = TREE(dbName);
		Long current = tree.getValueAs(key, Long.class, null);
		current = current == null || current == 0 ? maxindex : --current;
		tree.putAppend(key, current);
		return current;
	}

	private static long checkIndexOrReplace(long minIndex, long maxindex, boolean[] skipError) {
		IT.isPosOrZero(minIndex);
		if (ARG.isDefEqTrue(skipError)) {
			if (minIndex > maxindex) {
				minIndex = maxindex;
			}
		}
		return minIndex;
	}

	@NotNull
	private static UTree TREE(String treeName) {
		return UTree.treeApp(Ctx3Db.DEF_NS, treeName);
	}
}
