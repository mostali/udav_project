package mp.utl_odb_exp.fixture;

import mpe.db.Db;
import mpe.db.JdbcUrl;
import mpu.Sys;
import mpu.IT;
import mpu.X;
import mpc.types.abstype.AbsType;
import mpu.str.UST;
import mpe.sql.SqlQueryBuilder;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SwapStatus {

	public static void main(String[] args) throws SQLException {

		Path jdbcUrl = null;
		JdbcUrl of = JdbcUrl.of(jdbcUrl);
		List<List<AbsType>> rows = Db.queryList_(of, SqlQueryBuilder.SelectFromTableQuery.selectAll("groups"));

		for (List<AbsType> row : rows) {
			Map data = AbsType.asMapWithStringValues(IT.notEmpty(row));
			String yidStr = (String) data.get("yid");
			if (X.emptyObj_Str_Cll_Num(yidStr)) {
				Sys.e("Row has empty yid:" + data);
				continue;
			}
			Long yid = UST.LONG(yidStr, null);
			if (yid == null) {
				Sys.e("YID is not long:" + data);
				continue;
			}
			String status = (String) data.get("status");
			if (X.empty(status)) {
//				CDonor.enbaleDisableProject(of, yid, true);
				Sys.pf("Enable project '%s'", yid);
				continue;
			}
			switch (status) {
				case "0":
				case "1":
					Sys.pf("Skip project '%s' with '%s'", yid, status);
					continue;
				default:
//					CDonor.enbaleDisableProject(of, yid, true);
					Sys.pf("Enable project '%s' CHANGE '%s'", yid, status);
					continue;
			}
		}
		Sys.exit();

	}
}
