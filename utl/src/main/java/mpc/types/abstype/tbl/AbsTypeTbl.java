package mpc.types.abstype.tbl;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARG;
import mpu.str.ToString;
import mpv.sql_morpheus.SQLType;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AbsTypeTbl {

	private final String tablename;

	private final List<AbsTypeRow> rows;

	public List<AbsTypeRow> rows() {
		return this.rows;
	}

	public int size() {
		return this.rows.size();
	}

	public static AbsTypeTbl of(List<AbsTypeRow> row, String... tablename) {
		return new AbsTypeTbl(ARG.toDefOrNull(tablename), row);
	}

	@Override
	public String toString() {
		return "AbsTypeTbl{" + "tablename=" + tablename + ", row=" + ToString.toNiceStringCompact(rows) + '}';
	}

	public List<Object> getColumnValue(String colName, boolean... returnValue) {
		return rows().stream().map(r -> {
			if (ARG.isDefEqTrue(returnValue)) {
				return r.toMap().get(colName).getValue();
			}
			return r.toMap().get(colName);
		}).collect(Collectors.toList());
	}

	public List<String> getColumnNames() {
		return X.cast(getColumnValue("name", true));
	}

	public List<String> getColumnSqlTypeNames() {
		return X.cast(getColumnValue("type", true));
	}

	public List<SQLType> getColumnSqlTypes() {
		return getColumnSqlTypeNames().stream().map(SQLType::of).collect(Collectors.toList());
	}
}
