package mp.tslice;

import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.RW;
import mpc.fs.UF;
import mpc.fs.UFS;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MultiSlice implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<List<Object>> rows;
	private final Integer index;

	public Integer getIndex() {
		return index;
	}

	public MultiSlice(List<List<Object>> rows, Integer index) {
		this.rows = rows;
		this.index = index;
	}

	public MultiSlice() {
		rows = new ArrayList<>();
		index = 0;
	}

	public static MultiSlice loadFrom(String user, String sheetId, String listWithRange, boolean useCache) throws IOException {
		String file = null;
		if (useCache) {
			file = UF.createCompoundFileName_clearStringCyrKeepSlash("./tmp/", user, sheetId, listWithRange);
			if (UFS.isFileWithContent(file)) {
				return loadFromFile(file);
			}
		}
		MultiSlice ent = MultiSlice.of(USlice.loadTableValues(user, sheetId, listWithRange));
		if (useCache) {
			ent.storeToFile(file);
			return ent;
		}
		return ent;
	}

	@Deprecated
	public static MultiSlice loadFrom(String user, String sheetId, String listWithRange) {
		return of(USlice.loadTableValues(user, sheetId, listWithRange));
	}

	public static MultiSlice loadFrom(Path user, String sheetId, String listWithRange) {
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

	public static MultiSlice of(List<List<Object>> rows) {
		return of(rows, null);
	}

	public static MultiSlice of(List<List<Object>> rows, Integer index) {
		return new MultiSlice(rows, index);
	}


	@Override
	public String toString() {
		return "SliceEntity{" +
				"index=" + index + ", count=" + rows.size() +
				", rows=" + rows +
				'}';
	}


	private transient SingleSlice first;

	public SingleSlice firstSlice() {
		if (first == null) {
			first = nextSlice();
		}
		return first;
	}

	public SingleSlice nextSlice() {
		SliceIterator iterator = sliceIterator();
		if (!iterator.hasNext()) {
			return null;
		}
		SingleSlice next = SingleSlice.of(iterator.next(), iterator.getFirstBorderIndex());
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
		MultiSlice that = (MultiSlice) o;
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

	public MultiSlice trim() {
		USlice.trimStart(getValues());
		USlice.trimEnd(getValues());
		return this;
	}

	public List<SingleSlice> getSlices0() {
		return getSlices().stream().filter(X::notEmpty).map(SingleSlice::of).collect(Collectors.toList());
	}

	public List<List<List<Object>>> getSlices() {
		return ARR.toList(sliceIterator());
	}

	public List<SingleSlice> getSlicesExploded() {
		return new SingleSlice.Exploder(Seed.of(getValues(), true)).explodedSlices();
	}

	public String name() {
		return firstRow().name();
	}

	public String val() {
		return firstRow().val();
	}

	public String ext() {
		return firstRow().ext();
	}
}