package mpf.ngrid;

import mpu.str.Rt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NGrid<R extends NSection<I>, I extends NRowItem> extends ArrayList<R> {

	public List<List> toValuesRows() {
		return stream().map(g -> g.toValuesRows()).flatMap(s -> s.stream()).collect(Collectors.toList());
	}

	@Override
	public String toString() {
//		return getClass().getSimpleName() + "*" + X.sizeOf(this);
		return toString0(this);
	}

	public static String toString0(NGrid grid) {
		return Rt.buildReport(grid).toString();
	}

}
