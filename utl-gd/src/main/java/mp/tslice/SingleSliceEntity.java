package mp.tslice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SingleSliceEntity extends MultiSliceEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public SingleSliceEntity() {
		super();
	}

	public SingleSliceEntity(List<List<Object>> rows) {
		this(rows, 0);
	}

	public SingleSliceEntity(List<List<Object>> rows, Integer index) {
		super(rows, index);
	}

	@Override
	public String toString() {
		return "SingleSliceEntity{" +
			   "index=" + super.getIndex() + ", count=" + getValues().size() +
			   ", rows=" + getValues() +
			   '}';
	}

	public static SingleSliceEntity of(List<List<Object>> rows) {
		return of(rows, null);
	}

	public static SingleSliceEntity of(List<List<Object>> rows, Integer index) {
		return new SingleSliceEntity(rows, index);
	}


	public Integer getLevel(List<List<Object>> rows) {
		if (rows.isEmpty()) {
			return null;
		}
		for (List<Object> row : rows) {
			Integer level = USlice.getLevelRow(row);
			if (level != null) {
				return level;
			}
		}
		return null;
	}

	public MultiSliceEntity body() {
		List<List<Object>> values = getValues();
		if (values == null || values.size() <= 1) {
			return MultiSliceEntity.of(new ArrayList<>(), 0);
		}
		int startChild = 1;
		List<List<Object>> childRows = values.subList(startChild, values.size());
		return MultiSliceEntity.of(new ArrayList<>(childRows), startChild);
	}

}