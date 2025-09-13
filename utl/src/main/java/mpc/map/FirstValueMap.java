//package mpc.map;
//
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.Multimap;
//import lombok.RequiredArgsConstructor;
//import mpe.core.P;
//import mpu.X;
//import mpu.core.ARRi;
//import mpu.pare.Pare;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//public class FirstValueMap implements Map {
//
//	final Map src;
//
//	public static FirstValueMap of(Map from) {
//		return new FirstValueMap(from);
//	}
//
//	@Override
//	public int size() {
//		return src.size();
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return src.isEmpty();
//	}
//
//	@Override
//	public boolean containsKey(Object key) {
//		return src.containsKey(key);
//	}
//
//	@Override
//	public boolean containsValue(Object value) {
//		for (Object entry0 : src.entrySet()) {
//			Entry entry = (Entry) entry0;
//			Object entryValue = entry.getValue();
//			if (entryValue instanceof Collection) {
//				Collection c = (Collection) entryValue;
//				if (X.empty(c)) {
//					return false;
//				}
//				return X.equals(ARRi.first(c), value);
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public Object get(Object key) {
//		for (Object entry0 : src.entrySet()) {
//			Entry entry = (Entry) entry0;
//			if (X.notEquals(key, entry.getKey())) {
//				continue;
//			}
//			Object entryValue = entry.getValue();
//			if (entryValue instanceof Collection) {
//				Collection c = (Collection) entryValue;
//				if (X.empty(c)) {
//					return null;
//				}
//				return ARRi.first(c);
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public Object put(Object key, Object value) {
//		return src.put(key, value);
//	}
//
//	@Override
//	public Object remove(Object key) {
//		return src.remove(key);
//	}
//
//	@Override
//	public void putAll(@NotNull Map m) {
//		src.putAll(m);
//	}
//
//	@Override
//	public void clear() {
//		src.clear();
//	}
//
//	@Override
//	public @NotNull Set keySet() {
//		return src.keySet();
//	}
//
//	@Override
//	public @NotNull Collection values() {
//		return src.values();
//	}
//
//	@Override
//	public @NotNull Set<Entry> entrySet() {
//		return src.entrySet();
//	}
//}
//
