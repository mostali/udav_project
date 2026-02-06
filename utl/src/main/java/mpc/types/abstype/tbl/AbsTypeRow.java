package mpc.types.abstype.tbl;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;
import mpc.types.abstype.AbsType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AbsTypeRow {
	private final List<AbsType> row;

	private Map<String, AbsType> lmap;

	public static StringBuilder toString(List<Object> row) {
		StringBuilder sb = new StringBuilder();
		if (row == null) {
			return sb.append("ROW:N");
		} else if (row.isEmpty()) {
			return sb.append("ROW:0");
		}
		String sep = "' '";
		String str = row.stream().map(X::toString).collect(Collectors.joining(sep));
		return sb.append(str);
	}

	public Map<String, AbsType> toMap(boolean... fresh) {
		if (this.lmap == null || ARG.isDefEqTrue(fresh)) {
			this.lmap = new LinkedHashMap();
			row.stream().forEach(t -> lmap.put(t.name(), t));
		}
		return this.lmap;
	}

	public static AbsTypeRow of(List<AbsType> row) {
		return new AbsTypeRow(row);
	}

	@Override
	public String toString() {
		return "AbsTypeRow{" + "row=" + row + '}';
	}
}
