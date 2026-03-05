package mp.utl_odb.query_core;

public class DistinctParam {

	private String[] cols;

	private DistinctParam(String[] columns) {
		this.cols = columns == null ? new String[0] : columns;
	}

	public static DistinctParam param(String... columns) {
		return new DistinctParam(columns);
	}

	public String[] columns() {
		return cols;
	}

	public static DistinctParam empty() {
		return param(null);
	}

}