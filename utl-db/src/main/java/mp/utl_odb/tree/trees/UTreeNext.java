package mp.utl_odb.tree.trees;


import mp.utl_odb.tree.UTree;
import mpc.*;
import mpc.args.ARG;
import mpc.arr.ArrItem;
import mpc.ERR;
import mpc.exception.RequiredRuntimeException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UTreeNext {

	public static <K, V> Map.Entry<K, V> next0(String dbName, String key, Map<K, V> map, Map.Entry<K, V>... defRq) {
		Set<Map.Entry<K, V>> collection = map.entrySet();
		return next0(dbName, key, collection, defRq);
	}

	public static <T> T next0(String dbName, String key, Collection<T> collection, T... defRq) {
		if (X.notEmpty(collection)) {
			if (X.sizeOf(collection) == 1) {
				return ArrItem.first(collection);
			} else {
				long next = next(dbName, key, 0, collection.size() - 1);
				return ArrItem.item(collection, (int) next);
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Collection is empty"), defRq);
	}

	public static long next0(String dbName, String key, long maxindex) {
		return next(dbName, key, 0, maxindex);
	}


	public static long next(String dbName, String key, long minIndex, long maxindex) {
		ERR.isPosOrZero(minIndex);
		ERR.state(minIndex <= maxindex);
		UTree tree = UTree.tree(UTreeNext.class.getSimpleName(), dbName);
		Long current = tree.getAs(key, Long.class, null);
		current = current == null ? minIndex : ++current;
		long newIndex = current > maxindex ? minIndex : current;
		tree.put(key, newIndex);
		return newIndex;
	}
}
