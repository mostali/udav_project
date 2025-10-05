package mp.tslice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SliceIterator implements Iterator<List<List<Object>>> {
	private final List<List<Object>> rows;
	private Integer offset = 0;

	public SliceIterator(List<List<Object>> rows$) {
		rows = rows$;
	}

	public static List<List<List<Object>>> toList(List<List<Object>> rows) {
		return toList(createIterator(rows));
	}

	public static List<List<List<Object>>> toList(Iterator<List<List<Object>>> it) {
		List<List<List<Object>>> list = new ArrayList();
		do {
			List<List<Object>> slice = it.next();
			if (slice == null) {
				break;
			}
			list.add(slice);
		} while (true);
		return list;
	}

	public static SliceIterator createIterator(List<List<Object>> rows) {
		return new SliceIterator(rows);
	}

	@Override
	public boolean hasNext() {
		if (offset >= rows.size()) {
			return false;
		}
		return USlice.findBord(rows, offset) != null;
	}

	public List<List<Object>> next(Integer level, String... name_value_ext) {
		while (hasNext()) {
			List<List<Object>> slice = next();
			if (slice == null || slice.isEmpty()) {
				return null;
			}
			if (USlice.isRowEquals(slice.get(0), level, name_value_ext)) {
				return slice;
			}
		}
		return null;
	}

	private Integer[] border;

	public Integer getFirstBorderIndex() {
		return getCurrentBorder()[0];
	}

	public Integer[] getCurrentBorder() {
		return border;
	}

	public Integer getCurrentOffset() {
		return offset;
	}

	@Override
	public List<List<Object>> next() {
		int length = rows.size();
		if (offset >= length) {
			border = null;
			return null;
		}
		border = USlice.findBord(rows, offset);
		if (border == null) {
			return null;
		} else {
			this.offset = border[1] + 1;
		}
		List<List<Object>> entity = USlice.getSliceEntity(rows, border);
		if (isTrim) {
			USlice.trim(entity);
		}
		if (!entity.isEmpty() || !isReturnWithLevel) {
			return entity;
		}
		return next();
	}

	private boolean isTrim = true;

	public SliceIterator setIsTrim(boolean isTrim) {
		this.isTrim = isTrim;
		return this;
	}

	private boolean isReturnWithLevel = true;

	public SliceIterator setIsReturnWithLevel(boolean isReturnWithLevel) {
		this.isReturnWithLevel = isReturnWithLevel;
		return this;
	}

}
