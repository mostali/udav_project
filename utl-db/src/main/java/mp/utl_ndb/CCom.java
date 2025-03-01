//package mp.utl_ndb;
//
//import app_tsm.core.Srv;
//import app_tsmr.apiv1.grpvo.Grpvo;
//import lombok.SneakyThrows;
//import mpc.exception.RequiredRuntimeException;
//import mpc.map.MapTableContract;
//import mpf.contract.IContract;
//import mpu.X;
//import mpu.core.ARG;
//
//import java.sql.ResultSet;
//import java.util.List;
//import java.util.Map;
//
//public interface CCom extends IContract {
//
////	@SneakyThrows
////	public static List<CCom> fildAll(String sql) {
////		Dbc.query()
////		return Dbc.query().getJdbcTemplate().query(sql, (rs, rowNum) -> CCom.ofRs(rs));
////	}
//
//	@SneakyThrows
//	public static CCom ofRs(ResultSet rs) {
//		return of(Db.toMapSingleResultAsSimpleMap(rs));
//	}
//
//	public static CCom of(Map data) {
//		return MapTableContract.buildContract_MarkNotRq(data, CCom.class);
//	}
//
////	default String toStringSimple() {
////		return X.f("%s(%s)=%s", getName(), getNid(), getJob_count());
////	}
//}
