package mp.tslice;

import lombok.RequiredArgsConstructor;
import mpu.X;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SingleSlice extends MultiSlice implements Serializable {

	private static final long serialVersionUID = 1L;

	public SingleSlice() {
		super();
	}

	public SingleSlice(List<List<Object>> rows) {
		this(rows, 0);
	}

	public SingleSlice(List<List<Object>> rows, Integer index) {
		super(rows, index);
	}

	@Override
	public String toString() {
		return X.f("SingleSlice*%s#%s [%s]\n%s", X.sizeOf(getValues()), X.toStringNN(super.getIndex(), ""), name(), getValues());
	}

	public static SingleSlice of(List<List<Object>> rows) {
		return of(rows, null);
	}

	public static SingleSlice of(List<List<Object>> rows, Integer index) {
		return new SingleSlice(rows, index);
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

	public MultiSlice body() {
		List<List<Object>> values = getValues();
		if (values == null || values.size() <= 1) {
			return MultiSlice.of(new ArrayList<>(), 0);
		}
		int startChild = 1;
		List<List<Object>> childRows = values.subList(startChild, values.size());
		return MultiSlice.of(new ArrayList<>(childRows), startChild);
	}

	@RequiredArgsConstructor
	static class Exploder {
		final Seed seed;

		private List<SingleSlice> explodedSlices;

		@Override
		public String toString() {
			return X.f("Exploder*%s %s", X.sizeOf(explodedSlices), seed);
		}

		public List<SingleSlice> explodedSlices() {
			if (explodedSlices != null) {
				return explodedSlices;
			}

			explodedSlices = new ArrayList<>();

			Integer indexOfLastSeedSpace = Seed.findColIndexOfLastSeedSpace.apply(seed.values, 3);

			Seed seedCuted = seed.trimLeft(indexOfLastSeedSpace);

//			Seed swaped = seed.toSwaped();

//			Seed.isBlankMatrixRowItem
//			Integer indexOfLastSeedSpace = Seed.findIndexOfLastSeedSpace.apply(swaped.values, 3);
//			if (indexOfLastSeedSpace == -1) {
//				return explodedSlices;
//			} else if (indexOfLastSeedSpace == swaped.values.size() - 1) {
//				return explodedSlices;
//			}

//			swaped.trimLeft();

			//return
//			Seed seed = swaped.cloneRotate(false);

			return MultiSlice.of(seedCuted.values).getSlices0();

		}

	}


}