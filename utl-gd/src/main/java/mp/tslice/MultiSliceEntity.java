package mp.tslice;

import mpu.core.RW;
import mpc.fs.UF;
import mpc.fs.UFS;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MultiSliceEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<List<Object>> rows;
	private final Integer index;

	public Integer getIndex() {
		return index;
	}

	public MultiSliceEntity(List<List<Object>> rows, Integer index) {
		this.rows = rows;
		this.index = index;
	}

	public MultiSliceEntity() {
		rows = new ArrayList<>();
		index = 0;
	}

	public static MultiSliceEntity loadFrom(String user, String sheetId, String listWithRange, boolean useCache) throws IOException {
		String file = null;
		if (useCache) {
			file = UF.createCompoundFileName_clearStringCyrKeepSlash("./tmp/", user, sheetId, listWithRange);
			if (UFS.isFileWithContent(file)) {
				return loadFromFile(file);
			}
		}
		MultiSliceEntity ent = MultiSliceEntity.of(USlice.loadTableValues(user, sheetId, listWithRange));
		if (useCache) {
			ent.storeToFile(file);
			return ent;
		}
		return ent;
	}

	public static MultiSliceEntity loadFrom(String user, String sheetId, String listWithRange) {
		return of(USlice.loadTableValues(user, sheetId, listWithRange));
	}


	public void storeToFile(String path) throws IOException {
		RW.Serializable2File.serialize(path, this, true);
	}

	public static <T> T loadFromFile(String path) throws IOException {
		return RW.Serializable2File.deserialize(path);
	}

	public TRow firstRow() {
		List<Object> row = rows.get(0);
		return TRow.of(row);
	}

	public List<List<Object>> getValues() {
		return rows;
	}

	public static MultiSliceEntity of(List<List<Object>> rows) {
		return of(rows, null);
	}

	public static MultiSliceEntity of(List<List<Object>> rows, Integer index) {
		return new MultiSliceEntity(rows, index);
	}


	@Override
	public String toString() {
		return "SliceEntity{" +
				"index=" + index + ", count=" + rows.size() +
				", rows=" + rows +
				'}';
	}


	private transient SingleSliceEntity first;

	public SingleSliceEntity firstSlice() {
		if (first == null) {
			first = nextSlice();
		}
		return first;
	}

	public SingleSliceEntity nextSlice() {
		SliceIterator iterator = sliceIterator();
		if (!iterator.hasNext()) {
			return null;
		}
		SingleSliceEntity next = SingleSliceEntity.of(iterator.next(), iterator.getFirstBorderIndex());
		if (first == null) {
			first = next;
			return first;
		} else if (equals(next)) {
			return null;
		}
		return next;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		MultiSliceEntity that = (MultiSliceEntity) o;
		return rows.equals(that.rows);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rows, index);
	}

	private transient SliceIterator tSliceIterator = null;

	//
	public SliceIterator sliceIterator() {
		if (tSliceIterator == null) {
			tSliceIterator = SliceIterator.createIterator(getValues());
		}
		return tSliceIterator;
	}

	public MultiSliceEntity trim() {
		USlice.trimStart(getValues());
		USlice.trimEnd(getValues());
		return this;
	}

}