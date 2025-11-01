package mpe.db;

import mpc.types.abstype.AbsType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

	private static List toMap0(ResultSet rs, boolean asList_orMap) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			rows.add(toMapSingleResult(rs, asList_orMap));
		}
		return rows;
	}

	//
	//

	public static List<AbsType> toMapSingleResultAsList(ResultSet rs) throws SQLException {
		return (List<AbsType>) toMapSingleResult(rs, true);
	}

	public static Map<String, ?> toMapSingleResultAsSimpleMap(ResultSet rs) throws SQLException {
		return AbsType.asLinkedMapWithObject(toMapSingleResultAsList(rs));
	}

//	public static Map<String, AbsType> toMapSingleResultAsMap(ResultSet rs) throws SQLException {
//		return (Map<String, AbsType>) toMapSingleResult(rs, false);
//	}

	//MAIN
	private static Object toMapSingleResult(ResultSet rs, boolean asList_orMap) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		List<AbsType> rowAsList = null;
		Map<String, AbsType> rowAsMap = null;
		if (asList_orMap) {
			rowAsList = new ArrayList<>();
		} else {
			rowAsMap = new LinkedHashMap<>();
		}
		for (int ci = 1; ci <= cols; ci++) {
			String cName = metaData.getColumnName(ci);
			int cType = metaData.getColumnType(ci);
			AbsType val = AbsType.toAbsTypeSql(ci, cName, cType, rs);
			if (asList_orMap) {
				rowAsList.add(val);
			} else {
				rowAsMap.put(cName, val);
			}
		}
		return asList_orMap ? rowAsList : rowAsMap;
	}

}
