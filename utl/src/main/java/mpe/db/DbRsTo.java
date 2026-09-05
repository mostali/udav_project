package mpe.db;

import mpc.types.abstype.AbsType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbRsTo {

	public static List<List<AbsType>> toMapAsList(ResultSet rs) throws SQLException {
		return toMap0(rs, true);
	}

	public static List<Map<String, AbsType>> toMapAsMap(ResultSet rs) throws SQLException {
		return toMap0(rs, false);
	}


	public static List<Map<String, ?>> toMapSimpleMap(ResultSet rs) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			rows.add(toMapSingleResultAsSimpleMap(rs));
		}
		return rows;
	}

	public static <T> List<T> toSeq(ResultSet rs, Db.QMapResult qMapResult) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			rows.addAll(toMapSingleResult(rs, qMapResult).rowAsList);
		}
		return rows;
	}

	private static List toMap0(ResultSet rs, boolean asList_orMap) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			rows.add(toMapSingleResult(rs, asList_orMap));
		}
		return rows;
	}

	//
	//


	public static Map<String, ?> toMapSingleResultAsSimpleMap(ResultSet rs) throws SQLException {
		return AbsType.asLinkedMapWithObject(toMapSingleResultAsList(rs));
	}

	public static List<AbsType> toMapSingleResultAsList(ResultSet rs) throws SQLException {
		return (List<AbsType>) toMapSingleResult(rs, true);
	}

	//MAIN
	private static Object toMapSingleResult(ResultSet rs, boolean asList_orMap) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		int cols = metaData.getColumnCount();

		QColl collector = new QColl(asList_orMap);

		for (int ci = 1; ci <= cols; ci++) {
			String cName = metaData.getColumnName(ci);
			int cType = metaData.getColumnType(ci);
			AbsType val = AbsType.toAbsTypeSql(ci, cName, cType, rs);
			collector.add(val);

		}
		return collector.asList_orMap ? collector.rowAsList : collector.rowAsMap;
	}

	private static QColl toMapSingleResult(ResultSet rs, Db.QMapResult rslt) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		QColl collector = new QColl(rslt);
		for (int ci = 1; ci <= cols; ci++) {
			String cName = metaData.getColumnName(ci);
			int cType = metaData.getColumnType(ci);
			AbsType val = AbsType.toAbsTypeSql(ci, cName, cType, rs);
			collector.add(val);
		}
		return collector;
	}

}
