package mp.tslice.markup;

import mp.tslice.SliceIterator;
import mp.tslice.USlice;

import java.util.List;

public class BodyIterator extends SliceIterator {
	public final List<Object> parent;

	public BodyIterator(List<Object> parent, List<List<Object>> rows) {
		super(rows);
		this.parent = parent;
	}

	public static BodyIterator createIterator(List<Object> parent, List<List<Object>> rows) {
		return new BodyIterator(parent, rows);
	}

	public static BodyIterator createBodyIterator(List<List<Object>> manySliceRows, String... name_value_ext) {
		return createBodyIterator(manySliceRows, 0, name_value_ext);
	}

	public static BodyIterator createBodyIterator(List<List<Object>> manySliceRows, Integer level, String... name_value_ext) {
		SliceIterator it = SliceIterator.createIterator(manySliceRows);
		List<List<Object>> slice0 = it.next(level, name_value_ext);
		if (slice0 == null || slice0.size() < 1) {
			return null;
		} else if (slice0.size() == 1) {
			throw new BodyIteratorInvalid(slice0);
		}
		List<Object> parent = USlice.cutHeadRow(slice0);
		return BodyIterator.createIterator(parent, slice0);
	}

	public List<List<Object>> nextChildSlice() {
		return next();
	}

	public static class BodyIteratorInvalid extends IllegalArgumentException {
		public final List<List<Object>> slice0;

		public BodyIteratorInvalid(List<List<Object>> slice0) {
			super(String.valueOf(slice0));
			this.slice0 = slice0;
		}
	}
}
