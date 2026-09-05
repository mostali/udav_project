package mpc.types.opts;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import lombok.Getter;
import mpc.exception.FIllegalStateException;
import mpu.X;
import mpu.core.ARG;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Парсер командной строки с поддержкой нескольких значений на один ключ (MultiMap).
 * <p>
 * Пример:
 * <pre>
 *   CmmdOpt opt = CmmdOpt.parse("-id 1 -id 2 -name Ivan --flag", true);
 *   opt.get("id");          // -> [1, 2]
 *   opt.getFirst("id");     // -> 1
 *   opt.getLast("id");      // -> 2
 *   opt.contains("flag");   // -> true
 * </pre>
 */
public class Cmmd implements Multimap<String, Object> {

	public static void main(String[] args) {
		Cmmd opt = Cmmd.parse("-id 1 -id 2 -name Ivan --verbose -id 3", true);

		System.out.println(opt.get("id"));            // [1, 2]
		System.out.println(opt.getFirst("id"));       // 1
		System.out.println(opt.getLast("id"));        // 2
		System.out.println(opt.getFirstLong("id"));   // 1L
		System.out.println(opt.contains("verbose"));  // true
		System.out.println(opt.keyCount());           // 3
		System.out.println(opt.size());               // 4 (все пары key=value)
		System.out.println(opt.entries());            // [id=1, id=2, name=Ivan, verbose=true]
		System.out.println(opt.keysSingly());            // [id=1, id=2, name=Ivan, verbose=true]
	}

	private final @Getter String command;

	private final ListMultimap<String, Object> map;
	private final Set<String> dblKeys;

	private Cmmd(String command, ListMultimap<String, Object> map) {
		this(command, map, null);
	}

	private Cmmd(String command, ListMultimap<String, Object> map, Set<String> dblKeys) {
		this.command = command;
		this.map = map != null ? map : LinkedListMultimap.create();
		this.dblKeys = dblKeys;
	}

	public static Cmmd ofStrict(String line, Cmmd... defRq) {
		try {
			return Cmmd.parse(line, true);
		} catch (Exception ex) {
			return ARG.throwErr(ex, defRq);
		}
	}

	public static Cmmd of(String line, Cmmd... defRq) {
		try {
			return Cmmd.parse(line, false);
		} catch (Exception ex) {
			return ARG.throwErr(ex, defRq);
		}
	}

	public static Cmmd parse(String line, boolean strict) {
		ListMultimap<String, Object> args = LinkedListMultimap.create();

		Set<String> dblKeys = new HashSet<>();

		if (line == null) {
			return new Cmmd("", args);
		}

		line = line.trim();

		if (line.isEmpty()) {
			return new Cmmd(line, args);
		}

		String[] tokens = line.split("\\s+");

		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];

			if (!token.startsWith("-")) {
				if (strict) {
					throw new FIllegalStateException("Except token with '-'\n%s", line);
				}
				continue;
			}

			// флаг вида --name
			if (token.startsWith("--")) {
				String key = token.substring(2);
				args.put(key, true);
				dblKeys.add(key);
				continue;
			}

			String key = token.substring(1);

			if (i + 1 >= tokens.length) {
				if (strict) {
					throw new FIllegalStateException("Except last token value\n%s", line);
				}
				continue;
			}

			String value = tokens[i + 1];

			if (value.startsWith("-")) {
				if (strict) {
					throw new FIllegalStateException("Except token value for key=%s\n%s", key, line);
				}
				args.put(key, "");
				continue;
			}

			try {
				if (value.startsWith("\\-")) {
					value = value.substring(1);
				}
				long longVal = Long.parseLong(value);
				args.put(key, longVal);
			} catch (NumberFormatException e) {
				if ("true".equalsIgnoreCase(value)) {
					args.put(key, Boolean.TRUE);
				} else if ("false".equalsIgnoreCase(value)) {
					args.put(key, Boolean.FALSE);
				} else {
					args.put(key, value);
				}
			}

			i++; // пропускаем значение
		}

		Cmmd cmmd = new Cmmd(line, args, dblKeys);

		return cmmd;
	}

	// ---------------------------------------------------------------------
	// Удобные методы
	// ---------------------------------------------------------------------

	/**
	 * Возвращает первое значение по ключу или null.
	 */
	@Nullable
	public Object getFirst(String key) {
		List<Object> values = map.get(key);
		return values.isEmpty() ? null : values.get(0);
	}

	/**
	 * Возвращает первое значение по ключу как String или null.
	 */
	@Nullable
	public String getFirstString(String key) {
		Object v = getFirst(key);
		return v == null ? null : String.valueOf(v);
	}

	/**
	 * Возвращает первое значение по ключу как Long или null.
	 */
	@Nullable
	public Long getFirstLong(String key) {
		Object v = getFirst(key);
		if (v == null) {
			return null;
		}
		if (v instanceof Long) {
			return (Long) v;
		}
		try {
			return Long.parseLong(String.valueOf(v));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Возвращает первое значение по ключу как Boolean или null.
	 */
	@Nullable
	public Boolean getFirstBoolean(String key) {
		Object v = getFirst(key);
		if (v == null) {
			return null;
		}
		if (v instanceof Boolean) {
			return (Boolean) v;
		}
		return Boolean.parseBoolean(String.valueOf(v));
	}

	/**
	 * Возвращает последнее значение по ключу или null.
	 */
	@Nullable
	public Object getLast(String key) {
		List<Object> values = map.get(key);
		return values.isEmpty() ? null : values.get(values.size() - 1);
	}

	/**
	 * true, если ключ присутствует.
	 */
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	/**
	 * Количество уникальных ключей.
	 */
	public int keyCount() {
		return map.keySet().size();
	}

	/**
	 * Сколько значений лежит по конкретному ключу.
	 */
	public int valueCount(String key) {
		return map.get(key).size();
	}

	// ---------------------------------------------------------------------
	// Реализация Multimap<String, Object>
	// ---------------------------------------------------------------------

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(@Nullable Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(@Nullable Object value) {
		return map.containsValue(value);
	}

	@Override
	public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
		return map.containsEntry(key, value);
	}

	@Override
	public boolean put(@Nullable String key, @Nullable Object value) {
		return map.put(key, value);
	}

	@Override
	public boolean remove(@Nullable Object key, @Nullable Object value) {
		return map.remove(key, value);
	}

	@Override
	public boolean putAll(@Nullable String key, Iterable<?> values) {
		return map.putAll(key, values);
	}

	@Override
	public boolean putAll(Multimap<? extends String, ?> multimap) {
		return map.putAll(multimap);
	}

	@Override
	public Collection<Object> replaceValues(@Nullable String key, Iterable<?> values) {
		return map.replaceValues(key, values);
	}

	@Override
	public Collection<Object> removeAll(@Nullable Object key) {
		return map.removeAll(key);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@NotNull
	@Override
	public List<Object> get(@Nullable String key) {
		return map.get(key);
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@NotNull
	@Override
	public Multiset<String> keys() {
		return map.keys();
	}

	@NotNull
	@Override
	public Collection<Object> values() {
		return map.values();
	}

	@NotNull
	@Override
	public Collection<Map.Entry<String, Object>> entries() {
		return map.entries();
	}

	@NotNull
	@Override
	public Map<String, Collection<Object>> asMap() {
		return map.asMap();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Cmmd)) {
			return false;
		}
		return map.equals(((Cmmd) o).map);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	public Set<String> keysSingly() {
		return X.empty(dblKeys) ? keySet() : keySet().stream().filter(k -> !dblKeys.contains(k)).collect(Collectors.toSet());
	}
}