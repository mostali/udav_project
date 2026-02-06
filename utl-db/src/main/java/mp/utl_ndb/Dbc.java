package mp.utl_ndb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpe.db.Db;
import mpe.db.JdbcUrl;
import mpu.IT;
import mpc.types.abstype.AbsType;
import mpf.contract.IContract;
import mpc.map.MapTableContract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class Dbc {
	@Getter
	private final Db db;

	@SneakyThrows
	public static <C extends IContract> List<C> query(Class<C> contract, JdbcUrl jdbcUrl, String sql, Object... args) {
		return query_(contract, jdbcUrl, sql, args);
	}

	public static <C extends IContract> List<C> query_(Class<C> contract, JdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		List<List<AbsType>> rows = Db.queryList_(jdbcUrl, sql, args);
		List<C> contracts = new ArrayList();
		for (List<AbsType> row : rows) {
			Map data = AbsType.asMapWithStringValues(IT.notEmpty(row));
			C c = MapTableContract.buildContract_MarkNotRq(data, contract);
			contracts.add(c);
		}
		return contracts;
	}
}