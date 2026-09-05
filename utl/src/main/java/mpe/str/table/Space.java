package mpe.str.table;

import mpc.env.APP;
import mpc.exception.FIllegalStateException;
import mpe.NT;
import mpf.zbin.ZBin;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.Hu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class Space implements Iterator<Space.Cause> {

	public static void main(String[] args) {
//		Path file = Paths.get("/home/dav/pjm/utl-poi/src/main/java/app_poi/db.xlsx");
//		Space objs = Space.ofXlsx(file);
//		X.exit(objs.trimCopy());

		NT.BEA.set();

//		Object o = ZBin.XD.invokeArgs(APP.getPathGdKey(), "1E2KPeDIJsryqKe3mFOsYLcKNI5FfynZBPYWoc8nqTj0", "anypagew!A1:Z", ARR.as(ARR.as("123", "1232"), ARR.as("123 2", "12322 2 ")));
//		X.exit(o);
		Object o = ZBin.XD.invokeArgs(APP.getPathGdKey(), "1E2KPeDIJsryqKe3mFOsYLcKNI5FfynZBPYWoc8nqTj0", "anypage!A1:Z");
		X.p(Space.of((List) o));


//		X.p(Space.of(Matrix.rspToValues(o)));
	}


	@Override
	public String toString() {
		List<List> lists = ll;
		return getClass().getSimpleName() + "" + ll.size() + "x" + ll.get(0) + "\n" + TablePrint.toStringFromListList(lists, "nodata");
	}


	public Space trimCopy() {
//		Cause cause1 = firstCause();
//		List<List> llTrimed = USpace.trimAll(ll, cause1.start, cause1.last());
		List<List> llTrimed = Matrix.trimAll(ll, false);
		return Space.of(llTrimed);
	}

	List<Space> all = null;

	public List<Space> findAll(boolean... trim) {
		if (all != null) {
			return all;
		} else {
			all = new ArrayList<>();
		}
		Cause cause;
		boolean isTrim = ARG.isDefEqTrue(trim);
		do {
			cause = firstCause(null);
			if (cause == null) {
				break;
			}
			Space space = cause.cleanAndPool_asSpace();
			if (isTrim) {
				all.add(space.trimCopy());
			} else {
				all.add(space);
			}
		} while (true);

		return all;
	}

	Cause cause;

	@Override
	public boolean hasNext() {
		cause = firstCause(null);
		return cause != null;
	}

	@Override
	public Cause next() {
		cause.cleanAndPool_asSpace();//clean
		return cause;
	}

	public Cause firstCause(Cause... defRq) {
		return cur.findCause(defRq);
	}

//	public static Space ofXlsx(Path xlsx, boolean normalize) {
//		XlsCallMsg xlsCallMsg = XlsCallMsg.of(XlsCallMsg.Method.TREAD, xlsx);
//		List<List> table = (List<List>) xlsCallMsg.call(true);
//		return Space.of(table, normalize);
//	}

//	public static Space ofGd(Path xlsx, boolean normalize) {
//		XlsCallMsg xlsCallMsg = XlsCallMsg.of("TREAD " + xlsx);
//		List<List> table = (List<List>) xlsCallMsg.call(true);
//		return Space.of(table, normalize);
//	}

	public static Space ofStrict(List<List> ll) {
		return of(ll, false);
	}

	public static Space of(List<List> ll) {
		return of(ll, true);
	}

	public static Space of(List<List> ll, boolean normalize) {
		if (ARG.isDefEqTrue(normalize)) {
			Matrix.normalizeWidth(ll);
		}
		return new Space(ll);
	}

	final List<List> ll; //table
	final int w; //table

	Cur cur;

	Space(List<List> ll) {
		this.ll = ll;
		if (X.emptyLL(ll)) {
			throw new CleanException();
		}
		List line = ll.get(0);
		w = line.size();
		for (int i = 1; i < ll.size(); i++) {
			if (ll.get(i).size() != w) {
				throw new FIllegalStateException("except normal size %s row %s", w, i);
			}
		}
		cur = new Cur(ll, 0, 0);
	}


	public static class CleanException extends RuntimeException {

	}

	//
	// ----------------------- Cause -------------------------------
	//

	public static class Cause {
		private Integer maxSpace = 1;

		@Override
		public String toString() {
			return "CAUSE*" + Hu.posXY(last()) + Hu.posXY_BR(start) + "=" + Cur.object(ll, start);
		}

		public Integer[] last() {
			return new Integer[]{lastX, lastY};
		}

		final List<List> ll;
		final Integer[] start;

		Integer lastY;
		Integer lastX;

		Cause(List<List> ll, Integer... start) {
			this.ll = ll;
			this.start = start;
			this.lastX = start[0];
			this.lastY = start[1];
		}

		public Space space() {
			List<List> lists = Matrix.copyAndClean(ll, start, last(), true);
			return Space.of(lists);
		}

		Boolean hasYlast = false;
		Boolean hasXlast = false;

		public void expand() {
			Supplier<Boolean> hasAll = () -> {
				if (!hasYlast && lastY < ll.size() - 1) {
					++lastY;
				}
				if (!hasXlast && lastX < ll.get(0).size() - 1) {
					++lastX;
				}
//				hasYlast = hasSpaceY(ll, lastX, lastY);
				hasYlast = hasSpaceYStrict(ll, lastX, lastY);
				hasXlast = hasSpaceX(ll, lastX, lastY);
				return hasYlast && hasXlast;
			};

			while (!hasAll.get()) ;

		}

		public static Boolean hasSpaceY(List<List> ll, Integer maxX, Integer checkY) {
			if (checkY >= ll.size() - 1) {
				return true;
			}
			List line = ll.get(checkY);
			for (int x = 0; x <= maxX; x++) {
				if (ARR.isIndex(x, line) && X.notEmptyObj_Str(line.get(x))) {
					return false;
				}
			}
			return true;
		}

		public static Boolean hasSpaceYStrict(List<List> ll, Integer maxX, Integer checkY) {
			if (checkY >= ll.size() - 1) {
				return true;
			}
			List line = ll.get(checkY);
			for (int x = 0; x <= maxX; x++) {
				if (X.notEmptyObj_Str(line.get(x))) {
					return false;
				}
			}
			return true;
		}

		public static Boolean hasSpaceX(List<List> ll, Integer checkX, Integer max) {
			if (checkX >= ll.get(0).size() - 1) {
				return true;
			}
			for (int y = 0; y <= max; y++) {
				List line = ll.get(y);
				Object val = line.get(checkX);
				if (X.notEmptyObj_Str(val)) {
					return false;
				}
			}
			return true;
		}

		public Space cleanAndPool_asSpace() {
			return Space.of(cleanAndPool());
		}

		public List<List> cleanAndPool() {
			return Matrix.copyAndClean(ll, start, last(), true);
		}

		public Space read() {
			return Space.of(Matrix.copyAndClean(ll, start, last(), false));
		}
	}

	//
	// ----------------------- copyAndClean -------------------------------
	//

	//
	// ----------------------- Cursor -------------------------------
	//

	static class Cur {
		final List<List> ll;
		int checkX;
		int checkY;

		@Override
		public String toString() {
			return "Cur[" + Hu.posXY(xy()) + "]*" + line(ll, checkX, checkY);
		}

		public Cur(List<List> ll, Integer checkX, Integer checkY) {
			this.ll = ll;
			this.checkX = checkX;
			this.checkY = checkY;
		}

		public List line() {
			return ll.get(checkY);
		}

		public static Object object(List<List> ll, Integer... coorXY) {
			return ll.get(coorXY[1]).get(coorXY[0]);
		}

		public static List line(List<List> ll, Integer... coorXY) {
			return ll.get(coorXY[1]);
		}

		public Object cell() {
			return object(ll, checkX, checkY);
		}

		public boolean hasObject() {
			List list = ll.get(checkY);
			for (int x = 0; x <= checkX; x++) {
				if (X.notEmptyObj_Str(list.get(x))) {
					return true;
				}
			}
			return false;
		}

		public Integer[] xy() {
			return new Integer[]{checkX, checkY};
		}

		public Cause findCause(Cause... defRq) {
			while (!hasObject()) {
				if (!nextPos()) {
					return ARG.throwMsg("clean causes", defRq);
				}
			}
			Cause cause = new Cause(ll, checkX, checkY);
			cause.expand();
			return cause;
		}

		private boolean nextPos() {
			boolean lastY = lastY();
			if (lastY) {
				if (lastX()) {
					return false;
				}
				checkX++;
				checkY = 0;
			} else {
				checkY++;
			}
			return true;
		}

		private boolean lastY() {
			return checkY == ll.size() - 1;
		}

		private boolean lastX() {
			return checkX == ll.get(0).size() - 1;
		}

	}
}
