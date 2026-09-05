package mpf.ngrid;

import java.util.List;

public abstract class RowsBuilder {
	public abstract List<List> toValuesRows(boolean... fresh);
}
