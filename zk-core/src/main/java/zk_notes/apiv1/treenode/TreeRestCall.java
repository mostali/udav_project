package zk_notes.apiv1.treenode;

import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mp.utl_odb.tree.trees.api_expiremental.UTreeRest;
import mpc.fs.ext.EXT;
import mpe.core.U;
import mpe.wthttp.CleanDataResponseException;
import mpu.IT;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import udav_net.apis.zznote.NodeID;
import zk_os.AFC;
import zk_page.core.PagePathInfoWithQuery;

import java.nio.file.Path;

public class TreeRestCall {

	final PagePathInfoWithQuery curPPI;

	public static TreeRestCall ofPPI(PagePathInfoWithQuery curPPI) {
		return new TreeRestCall(curPPI);
	}

	public TreeRestCall(PagePathInfoWithQuery curPPI) {
		this.curPPI = curPPI;
		this.nodeID = null;
		this.oper = null;
	}

	final NodeID nodeID;
	final Pare3<String, String, String> oper;

	public TreeRestCall(NodeID nodeID, Pare3<String, String, String> oper) {
		this.nodeID = nodeID;
		this.oper = oper;
		IT.NE(oper.key(), "oper");
		IT.NE(oper.val(), "val");

		curPPI = null;
	}

	public Pare<Integer, String> apply() {
		ICtxDb tree = loadTreeFromPath();
		return UTreeRest.apply(tree, getOper(), getKey(), getVal());
	}

	private Ctx10Db loadTreeFromPath() {
		Path atiTree = AFC.EVENTS.getRpaEventsStatePath(getSd3(), getPagename(), getItemname(), EXT.SQLITE);
		Ctx10Db tree = Ctx10Db.of(atiTree);
		if (tree.isExistDb()) {
			return tree;
		}
		Path last = curPPI.path(3, null);
		boolean isPut = IT.NN(last, "except last path operation").getFileName().toString().equals("put");
		if (isPut) {
			tree.checkLazyCreateDb();
		}
		return tree;
	}

	private String getSd3() {
		return curPPI != null ? curPPI.subdomain3Rq() : nodeID.sd3Rq();
	}

	private String getPagename() {
		return curPPI != null ? curPPI.pathStr(1) : nodeID.pageRq();
	}

	private String getItemname() {
		return curPPI != null ? curPPI.pathStr(2) : nodeID.itemRq();
	}

	private String getOper() {
		return curPPI != null ? curPPI.pathStr(3) : oper.key();
	}

	private String getVal() {
		return curPPI != null ? curPPI.queryUrl().getFirstAsStr("v", null) : oper.ext();
	}

	private String getKey() {
		if (curPPI == null) {
			return oper.val();
		}
		String key = curPPI.queryUrl().getFirstAsStr("k", null);
		if (key == null) {
			throw CleanDataResponseException.C400("set key value arg <k>");
		} else if (U.__NULL__.equals(key)) {
			key = null;
		}
		return key;
	}

}
