package mp.utl_odb.query_core;

public class GroupByParam {
	private String[] cols;

	private GroupByParam(String[] columns) {
		this.cols = columns == null ? new String[0] : columns;
	}

	public static GroupByParam param(String... columns) {
		return new GroupByParam(columns);
	}

	public String[] columns() {
		return cols;
	}

	public static GroupByParam empty() {
		return param(null);
	}

}