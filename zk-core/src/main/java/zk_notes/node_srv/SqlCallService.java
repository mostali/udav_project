package zk_notes.node_srv;

import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mpc.types.abstype.AbsType;
import mpe.db.IJdbcUrl;
import mpe.wthttp.SqlCallMsg;
import mpu.IT;
import zk_notes.node.NodeDir;
import zk_os.core.NodeData;

import java.util.List;

public class SqlCallService {

	@SneakyThrows
	public static Object doSqlCall_VALUE(NodeDir node, boolean allowNullValue) {
		List<List<AbsType>> lists = doSqlCall(node);
		IT.state(lists.size() == 1, "Except single row from sql call node '%s'", node.nodeId());
		IT.state(lists.get(0).size() == 1, "Except single column from sql call node '%s'", node.nodeId());
		Object value = lists.get(0).get(0).getValue();
		return allowNullValue ? value : IT.NN(value, "Except not null value from sql call node '%s'", node.nodeId());
	}

	@SneakyThrows
	public static List<List<AbsType>> doSqlCall(NodeDir node) {
		SqlCallMsg sqlCallMsg = SqlCallMsg.of(node.nodeDataInjected().nodeData);
		return doSqlCall(sqlCallMsg);
	}

	@SneakyThrows
	public static List<List<AbsType>> doSqlCall(SqlCallMsg sqlCallMsg) {
		sqlCallMsg.throwIsErr();
		IJdbcUrl jdbcUrl = sqlCallMsg.getJdbcUrl();
		List<List<AbsType>> maps = Db.queryList_(jdbcUrl, sqlCallMsg.getSql());
		return maps;
	}
}
