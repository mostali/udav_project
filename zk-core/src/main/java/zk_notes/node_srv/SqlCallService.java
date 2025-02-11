package zk_notes.node_srv;

import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mpc.types.abstype.AbsType;
import mpe.wthttp.SqlCallMsg;
import mpu.IT;
import zk_notes.node.NodeDir;

import java.util.List;

public class SqlCallService {

	@SneakyThrows
	public static Object doSqlCall_VALUE(NodeDir node, boolean allowNullValue) {
		List<List<AbsType>> lists = doSqlCall(node);
		IT.state(lists.size() == 1, "Except single row from sql call node '%s'", node.id());
		IT.state(lists.get(0).size() == 1, "Except single column from sql call node '%s'", node.id());
		Object value = lists.get(0).get(0).getValue();
		return allowNullValue ? value : IT.NN(value, "Except not null value from sql call node '%s'", node.id());
	}

	@SneakyThrows
	public static List<List<AbsType>> doSqlCall(NodeDir node) {
//		String data = node.state().nodeDataCached(true);
		String data = InjectNode.inject(node, null, TrackMap.getTrackId());
		SqlCallMsg sqlCallMsg = SqlCallMsg.of(data);
		return doSqlCall(sqlCallMsg);
	}

	@SneakyThrows
	public static List<List<AbsType>> doSqlCall(SqlCallMsg sqlCallMsg) {
		sqlCallMsg.throwIsErr();
		List<List<AbsType>> maps = Db.queryList_(sqlCallMsg.getJdbcUrl(), sqlCallMsg.getSql());
		return maps;
	}
}
