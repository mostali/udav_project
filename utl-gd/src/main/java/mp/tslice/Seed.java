package mp.tslice;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.func.Function2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Seed {

	@Override
	public String toString() {
		return X.f("Seed %s", X.sizeOfColColAsList(values));
	}

	public final List<List<Object>> values;

	public Seed(List<List<Object>> values) {
		this.values = values;
	}

	private List<List<Object>> valuesSwaped;

	public Seed toSwaped() {
		return Seed.of(valuesSwaped(), true);
	}

	public List<List<Object>> valuesSwaped() {
		if (valuesSwaped != null) {
			return valuesSwaped;
		} else if (X.empty(values)) {
			return ARR.EMPTY_LIST;
		}
		return valuesSwaped = ARR.swapCoordinates((List) values, true);
	}

	public static Predicate<List> isBlankRow = (rows) -> rows.stream().noneMatch(X::notEmptyObj_Str);

//	public static Function2<List, Integer, Boolean> isBlankRowFrom = (rows, index) -> {
//		for (int i = index; i < rows.size(); i++) {
//			if (X.notEmptyObj_Str(rows.get(i))) ;
//			return true;
//		}
//	};

	public static Function2<List<List<Object>>, Integer, Boolean> isBlankMatrixRowItem = (rows, itemIndex) -> rows.stream().noneMatch(r -> X.notEmptyObj_Str(ARRi.item(r, itemIndex)));

	public static Function2<List<List<Object>>, Integer, Integer> findColIndexOfLastSeedSpace = (rows, rowsSpace) -> {
		int tc = rowsSpace;
		nextCol:
		for (int x = 0; x < rows.get(0).size(); x++) {
			for (int i = 0; i < rows.size(); i++) {
				List<Object> row = rows.get(i);
				if (X.notEmptyObj_Str(row.get(x))) {
					tc = rowsSpace;
					continue nextCol;
				}
				if (--tc == 0) {
					return x;
				}
//				if (isBlankRow.test(rows.get(i))) {
//
//				} else {
//					tc = rowsSpace;
//				}
			}
		}
		return -1;
	};

//	public static Function2<List<List<Object>>, Integer, Integer> findIndexOfLastSeedSpaceSwaped = (rows, rowsSpace) -> {
//		int tc = rowsSpace;
//		for (int i = 0; i < rows.size(); i++) {
//			if (isBlankRow.test(rows.get(i))) {
//				if (--tc == 0) {
//					return i;
//				}
//			} else {
//				tc = rowsSpace;
//			}
//		}
//		return -1;
//	};

	public static Seed of(List<List<Object>> list, boolean normSize) {
		if (normSize) {
			ARR.normalizeSize(list);
		}
		return new Seed(list);
	}

	public static Seed ofRow(List list) {
		return new Seed(ARR.asAL(list));
	}

	public boolean isEmptyRow(int index) {
		return isBlankRow.test(ARRi.item(values, index, ARR.EMPTY_LIST));
	}

//	public boolean isEmptyRowByOffsetX(int offsetX) {
//		for (int i = 0; i < values.size(); i++) {
//			List r = values.get(i);
//			if (!isBlankRowFrom.apply(r, offsetX)) {
//				return false;
//			}
//		}
//		return true;
//	}

	public Seed trimLeft(int fromIndex) {
		List l = new ArrayList();
		values.stream().forEach(r -> l.add(ARR.sublist(r, fromIndex)));
		return Seed.of(l,false);
	}

	public void trimLeft() {
		ARR.trimLeft(values, isBlankRow::test);
	}

	public Seed cloneRotate(boolean clockwise) {
		return new Seed(ARR.swapCoordinates((List) values, clockwise));
	}
}
