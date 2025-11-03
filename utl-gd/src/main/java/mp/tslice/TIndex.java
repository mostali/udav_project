package mp.tslice;

import com.google.common.collect.FluentIterable;
import mpu.core.ARR;
import mpu.X;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class TIndex {
	final List<XKey> index;
	final List<List> rows;

	public TIndex(List<List> rows) {
		this.rows = rows;
		this.index = createIndex(rows);
	}
	public static Integer getLevelRow(List<Object> row) {
		if (row.isEmpty()) {
			return null;
		}
		for (int i = 0; i < row.size(); i++) {
			Object obj = row.get(i);
			if (!X.emptyObj_Str(obj)) {
				return i;
			}
		}
		return null;
	}

	private static TIndex create(List<List> rows) {
		return new TIndex(rows);
	}


	public static List<XKey> createIndex(List<List> rows) {
		List<XKey> index = new ArrayList<>();
		java.util.Map<Integer, List<List<Object>>> map = new LinkedHashMap<>();
		for (int i = 0; i < rows.size(); i++) {
			List row = rows.get(i);
			Integer x = getLevelRow(row);
			Object key = x == null ? null : row.get(x);
			XKey xrow = XKey.create(i, x, key, row);
			index.add(xrow);
		}
		return index;
	}

	public static java.util.Map<String, List<XKey>> toProps(List<XKey> index, int level) {
		if (index.get(0).y != 0) {
			index = freshIndex(index);
		}
		java.util.Map<String, List<XKey>> props = new LinkedHashMap<>();
		int startOffset = 0;
		while (true) {
			List<XKey> next = next(index, level, startOffset);
			if (next.isEmpty()) {
				return props;
			}
			props.put(next.get(0).keyAsString(), next);
			startOffset = next.size() == 1 ? next.get(0).y + 1 : next.get(1).y + 1;
		}
	}

	public static List<List<XKey>> childs(List<XKey> index, int level, int startOffset) {
		if (index.get(0).y != 0) {
			index = freshIndex(index);
		}
		List childs = new ArrayList();
		while (true) {
			List<XKey> next = next(index, level, startOffset);
			if (next.isEmpty()) {
				return childs;
			}
			childs.add(next);
			startOffset = next.size() == 1 ? next.get(0).y + 1 : next.get(1).y + 1;
		}
	}

	public static List<XKey> nextWithKey(List<XKey> index, int level, int startOffset, Object key) {
		if (index.get(0).y != 0) {
			index = freshIndex(index);
		}
		while (true) {
			List<XKey> next = next(index, level, startOffset);
			if (next.isEmpty()) {
				return next;
			}
			if (Objects.equals(next.get(0).key, key)) {
				return next;
			}
			startOffset = next.size() == 1 ? next.get(0).y + 1 : next.get(1).y + 1;
		}
	}

	public static List<XKey> next(List<XKey> freshIndex, int level, int startOffset) {
		List next = new ArrayList();
		for (int i = startOffset; i < freshIndex.size(); i++) {
			XKey row = freshIndex.get(i);
			if (row.isNull()) {
				continue;
			}
			if (level == row.x) {
				if (!next.isEmpty()) {
					return next;
				}
				next.add(row);
			} else if (row.x < level) {
				if (!next.isEmpty()) {
					return next;
				}
			} else if (!next.isEmpty()) {
				next.add(row);
			}
		}
		return next;
	}

	private static List<XKey> freshIndex(List<XKey> ufos) {
		List l = new ArrayList();
		for (int i = 0; i < ufos.size(); i++) {
			XKey row = ufos.get(i);
			l.add(XKey.of(i, row));
		}
		return l;
	}

	public static class Core {
		private final XKey head;
		private final List<XKey> body;

		public List<XKey> body() {
			return body;
		}

		public XKey head() {
			return head;
		}

		public Core(List<XKey> rows) {
			this(ARR.cutItemFirst(rows), rows);
		}

		public Core(XKey head, List<XKey> body_rows) {
			this(head, body_rows, true);
		}

		public Core(XKey head, List<XKey> body, boolean fresh) {
			this.head = head;
			this.body = body;
			//TODO fresh index (y) in rows
		}

		public Core of(List<XKey> body) {
			return new Core(body);
		}

		public Core of(XKey head, List<XKey> body) {
			return new Core(head, body);
		}

		@Override
		public String toString() {
			return "Core{" +
				   "head=" + head +
				   ", body=" + body +
				   '}';
		}
	}

	public static class Map {
		final int level;
		final String parentName;
		final List<List> rows;
		final java.util.Map<String, List<XKey>> props;

		public Map(int level, String parentName, List<List> rows) {
			this.level = level;
			this.parentName = parentName;
			this.rows = rows;

			List<XKey> template = nextWithKey(createIndex(rows), level, 0, parentName);
			props = toProps(template, level);
		}

		public static Map of(int level, String parentName, List<List> rows) {
			return new Map(level, parentName, rows);
		}

		public XKey getValue(String name) {
			return props.get(name).get(0);
		}

		public String getValueAsString(String name) {

			return getValue(name).valueAsString();
		}

		public String getExtAsString(String name) {
			return getValue(name).extAsString();
		}
	}

	public static class XKey {
		private final int y;
		private final int x;
		private final Object key;
		private final List row;

		private final Object value;
		private final Object ext;

		public int x() {
			return x;
		}

		public int y() {
			return y;
		}

		public Object key() {
			return key;
		}

		public List row() {
			return row;
		}

		public Object value() {
			return value;
		}

		public Object ext() {
			return ext;
		}

		public XKey(int y, Integer x, Object key, List row) {
			this.y = y;
			this.x = x == null ? -1 : x;
			this.key = key;
			this.row = ARR.cutItemLastRecursively(row, null);

			value = this.x < 0 ? null : (this.x + 1 < row.size() ? row.get(this.x + 1) : null);
			ext = this.x < 0 ? null : (this.x + 2 < row.size() ? row.get(this.x + 2) : null);
		}

		public static XKey create(int y, Integer x, Object key, List row) {
			return new XKey(y, x, key, row);
		}

		public static Object of(int y, XKey row) {
			return new XKey(y, row.x, row.key, row.row);
		}

		public static boolean isComment(List<XKey> keys) {
			return FluentIterable.from(keys).anyMatch(XKey::isComment);
		}

		public static boolean isComment(XKey key) {
			return key.keyAsString().startsWith("//");
		}

		public boolean isNull() {
			return x < 0;
		}

		@Override
		public String toString() {
			return "XKey{" +
				   "y=" + y +
				   ", x=" + x +
				   ", key=" + key +
				   ", size=" + row.size() +
				   '}';
		}

		public String keyAsString() {
			return key.toString();
		}

		public String valueAsString() {
			return value == null ? null : value.toString();
		}

		public String extAsString() {
			return ext == null ? null : ext.toString();
		}
	}
}
