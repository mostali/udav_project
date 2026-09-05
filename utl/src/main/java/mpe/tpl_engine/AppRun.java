package mpe.tpl_engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.fs.ext.EXT;
import mpe.str.table.TablePrint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AppRun {

	public final static Map<EXT, Function<In, Out>> RUNNERS = new HashMap<>();

	//
	// IN

	@RequiredArgsConstructor
	public static class StringIn implements In<String> {
		public final String in;

		@Override
		public String getIn() {
			return in;
		}
	}

	public interface In<IN> {
		static StringIn ofString(String in) {
			return new StringIn(in);
		}

		IN getIn();
	}

	//
	// OUT

	@RequiredArgsConstructor
	public static class SqlCallOut implements Out {

		public final @Getter String sql;
		public final @Getter List<List> table;

		public String toStringTable(String returnIfEmptyTable) {
			return TablePrint.toStringFromListList(table, returnIfEmptyTable, 100);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "::\n" + toStringTable("empty rows");
		}
	}

	@RequiredArgsConstructor
	public static class StringOut implements Out {
		public final @Getter String out;

		@Override
		public String toString() {
			return getClass().getSimpleName() + "::\n" + getOut();
		}
	}

	public interface Out {

		static StringOut ofString(String out) {
			return new StringOut(out);
		}

		static SqlCallOut ofSqlCall(String sql, List<List> table) {
			return new SqlCallOut(sql, table);
		}

	}
}
