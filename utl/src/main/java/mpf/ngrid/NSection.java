package mpf.ngrid;

import mpc.rfl.RFL;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Tuple;
import mpu.str.Rt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class NSection<R extends NRowItem> extends ArrayList<R> {

	public final Tuple groupKey;

	public NSection(String groupKey) {
		this(Tuple.ofObjs(IT.NN(groupKey, "set group key")));
	}

	public NSection(Tuple groupKey) {
		super();
		this.groupKey = groupKey;
	}

	@Override
	public String toString() {
		return toString0(this);
	}

	public static String toString0(NSection group) {
		return Rt.buildReport(group, group.groupKey.key().toString()).toString();
	}

	public List<String> toHeaderRow() {
		return groupKey.toValuesString();
	}

	protected List<List> valuesRows;

	public List<List> toValuesRows(boolean... fresh) {

		if (ARG.isDefEqTrue(fresh)) {
			valuesRows = null;
		}

		if (valuesRows != null) {
			return valuesRows;
		}

		Class<RowsBuilder> rowsBuilder = (Class<RowsBuilder>) groupKey.extAs(Class.class, null);
		if (rowsBuilder != null) {
			RowsBuilder builder = RFL.instWithArg(rowsBuilder, this);
			valuesRows = builder.toValuesRows();
			return valuesRows;
		}

		valuesRows = new ArrayList<>();

		{ //HEADER
			List<String> headerRow = toHeaderRow();
			valuesRows.add(headerRow);
		}

		{ //BODY
			List<List> allRows = (List) stream().flatMap(s -> s.toValuesRows().stream()).collect(Collectors.toList());
			valuesRows.addAll(allRows);
		}

		return valuesRows;
	}

	public String ggName() {
		return (String) groupKey.keyAs(String.class);
	}
}
