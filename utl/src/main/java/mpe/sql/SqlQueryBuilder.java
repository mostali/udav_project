package mpe.sql;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import mpu.IT;
import mpu.str.STR;
import mpv.sql_morpheus.SQLPlatform;

import java.util.*;

public class SqlQueryBuilder {
	public enum SqlType {
		TEXT, INT, LONG, VARCHAR;

		public String col(String colName) {
			return '\'' + colName + "' " + name();
		}

		public String col(String colName, SQLPlatform tdb) {
			switch (tdb) {
				case SQLITE:
					return col(colName);
				case POSTGRE:
					return colName + " " + name();
				default:
					throw new WhatIsTypeException(tdb);
			}
		}
	}

	// create temporary table iii ( OPDAYDATE date , ... )
	public static final CreateTableQuery CREATE_TABLE = new CreateTableQuery();

	// insert into iii values( '2020-11-02' , ... )
	public static final InsertIntoTableQuery INSERT_INTO_TABLE = new InsertIntoTableQuery();
	public static final UpdateTableQuery UPDATE_TABLE = new UpdateTableQuery();
	public static final SelectFromTableQuery SELECT_TABLE = new SelectFromTableQuery();

	// truncate table1, table2
	public static final TruncateTableQuery TRUNCATE_TABLE = new TruncateTableQuery();

	// drop [ IF EXISTS ] table1, table2
	public static final DropTableQuery DROP_TABLE = new DropTableQuery();

	public static final SumQuery SUM = new SumQuery();

	public static class SumQuery {
		public static final String SQL_SUM_SINGLE = "select SUM(%s) from %s where %s='%s'";

		public static String sum(String table, String col, String whereCol, Object colVal) {
			return String.format(SQL_SUM_SINGLE, col, table, whereCol, colVal);
		}
	}

	public static class DropTableQuery {

		public static String dropTable(boolean checkIfExist, String... tableNames) {
			StringBuilder tables = SeqBuilder.generateSequenceUnwrap(Arrays.asList(IT.notEmpty(tableNames)), false);
			String ifExist = checkIfExist ? "IF EXISTS " : "";
			return "DROP TABLE " + ifExist + tables;
		}
	}


	public static class TruncateTableQuery {
		public static String truncateTable(String... tableNames) {
			StringBuilder tables = SeqBuilder.generateSequenceUnwrap(Arrays.asList(IT.notEmpty(tableNames)), false);
			return "TRUNCATE TABLE " + tables;
		}

		public static String truncateTable_SQLITE(String tableName, boolean withSequence) {
			IT.state(!withSequence, "ni");
			//DELETE FROM SQLITE_SEQUENCE WHERE name='table_name'; //clear COUNTERS ?
			return "DELETE FROM " + tableName + ";";
		}
	}

	public static class UpdateTableQuery {
		//UPDATE table_name
		//SET column1 = value1, column2 = value2, ...
		//WHERE condition;
		public static String update(String tableName, String where_col, Object where_value, String set_col, Object set_value) {
			return "UPDATE " + tableName + " SET " + set_col + "=" + autoWrapString(set_value) + " WHERE " + where_col + "=" + autoWrapString(where_value);
		}
	}

	private static Object autoWrapString(Object val) {
		return !(val instanceof CharSequence) ? val : STR.wrapIfNot(val.toString(), "'", true);
	}

	public static class SelectFromTableQuery {
		//UPDATE table_name
		//SET column1 = value1, column2 = value2, ...
		//WHERE condition;
		public static String selectAll(String tableName) {
			return "SELECT * FROM " + tableName;
		}

		public static String selectWhere(String tableName, String where_col, Object where_value) {
			return "SELECT * FROM " + tableName + " WHERE " + where_col + "=" + where_value;
		}
	}

	public static class InsertIntoTableQuery {
		public static String insertSingleValuesToTable(String tableName, List values) {
			return insertManyValuesToTable(tableName, Arrays.asList(values));
		}

		public static String insertManyMapValuesToTable(String tableName, List<Map<String, Object>> valuesWithMapValues) {
			Collection<Collection> valuesWithValues = new ArrayList<>();
			for (Map<String, Object> values : valuesWithMapValues) {
				valuesWithValues.add(values.values());
			}
			return insertManyValuesToTable(tableName, valuesWithValues);
		}

		public static String insertManyValuesToTable(String tableName, Collection<? extends Collection> valuesWithValues, boolean... protectSingleQuote) {
			if (ARG.isDefEqTrue(protectSingleQuote)) {
				List<List> collNew = new ArrayList();
				for (Collection row : valuesWithValues) {
					List rowNew = new ArrayList();
					for (Object obj : row) {
						String str = String.valueOf(obj);
						if (!str.contains("'")) {
							rowNew.add(obj);
						} else {
							rowNew.add(str.replace("'", "''"));
						}
					}
					collNew.add(rowNew);
				}
				valuesWithValues = collNew;
			}
			List<StringBuilder> bodys = new ArrayList<>();
			for (Collection values : valuesWithValues) {
				StringBuilder body = SeqBuilder.generateSequence(values, true, true);
				bodys.add(body);
			}
			StringBuilder valuesOfValues = SeqBuilder.generateSequence(bodys, false, false);
			return "INSERT INTO " + tableName + " VALUES " + valuesOfValues;
		}
	}

	public static class CreateTableQuery {

		public static String createTable(String tableName, boolean checkIfNotExist, Collection sqlTypeDeclares) {
			return createTable(tableName, checkIfNotExist, false, sqlTypeDeclares);
		}

		public static String createTable(String tableName, boolean checkIfNotExist, Object... sqlTypeDeclares) {
			return createTable(tableName, checkIfNotExist, false, Arrays.asList(sqlTypeDeclares));
		}

		public static String createTmpTable(String tableName, boolean checkIfNotExist, Object... sqlTypeDeclares) {
			return createTable(tableName, checkIfNotExist, true, Arrays.asList(sqlTypeDeclares));
		}

		public static String createTable(String tableName, boolean checkIfNotExist, boolean isTemporary, Collection sqlTypeDeclares) {
			StringBuilder body = SeqBuilder.generateSequenceUnwrap(sqlTypeDeclares, false);
			String ifNotExist = checkIfNotExist ? "IF NOT EXISTS " : "";
			return "CREATE" + (isTemporary ? " TEMPORARY " : " ") + "TABLE " + ifNotExist + tableName + " (" + body + ")";
		}
	}


}
