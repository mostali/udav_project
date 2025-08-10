package zk_notes.apiv1._ati;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.exception.NI;
import mpc.fs.ext.EXT;
import mpe.core.U;
import mpe.wthttp.CleanDataResponseException;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.STR;
import udav_net.apis.zznote.NodeID;
import udav_net.apis.zznote.NoteApi;
import zk_os.AFC;
import zk_page.core.PagePathInfoWithQuery;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//	Path _pagePath = null;
	Path _itemPath = null;
//	Path _operPath = null;

	public Optional<TreeOper> of(int level) {
		Path path = path(level, null);
		if (path == null) {
			return null;
		}
		switch (path.toString()) {
			case "*":
				return Optional.of(new StarOperation(this, level));
			case "!":
				return Optional.of(new ClaimOperation(this, level));
			default:
				return Optional.empty();
		}
	}

	public Pare<Integer, String> apply() {

		for (int level = 0; level <= 2; level++) {
			Optional<TreeOper> partLevel = of(level);
			if (partLevel == null) {
				return new StarOperation(this, level).applyRoot();
			} else if (partLevel.isPresent()) {
				return partLevel.get().apply();
			}
		}

		Optional<TreeOper> partLevel = of(3);
		if (partLevel == null) {
			return new StarOperation(this, 2).apply();
		}


		if (true) {
			return Pare.of(TreeOper.CODE_WRONG_LOGIC, "bad");
//			for (int i = 2; i >= 0; i--) {
//				if (path(i, null) != null) {
//					return new StarOperation(i).apply();
//				}
//			}
//			return new StarOperation(0).apply();
		}

//		_pagePath =;
//
//		boolean isRootRequst = _pagePath == null;
//		if (isRootRequst) { //_ati/
//			return Pare.of(200, "ni root");
//		}

//		_itemPath = path2_item(null);
//		boolean isEmptyItem = _itemPath == null;
//		if (isEmptyItem) {
////			Path atiTree = AFC.EVENTS.getRpaEventsStatePath(getSd3(), getPagename(), getItemname(), EXT.SQLITE);
//			return Pare.of(200, "ni root page");
//		}

//		_operPath = path3_oper(null);
//		boolean isEmptyOper = _operPath == null;
//		ICtxDb atiTree = null;
//		if (isEmptyOper) {
		Path atiTreePath = AFC.EVENTS.getRpaEventsStatePath(getSd3(), _itemPath.toString(), _itemPath.toString(), EXT.SQLITE);
		List<Ctx10Db.CtxModel10> models = (Ctx10Db.of(atiTreePath)).getModels();
		if (X.empty(models)) {
			return Pare.of(200, U.__NULL__ + "(empty tree)");
		}
		String rspTreeItems = models.stream().map(m -> m.getKey()).collect(Collectors.joining(STR.NL));
		return Pare.of(200, rspTreeItems);
//		}
//		UTreeRest.TreeCase treeCase = UTreeRest.TreeCase.valueOf(_operPath.toString(), null);
//		IT.state(treeCase != null, "illegal operation");

//		switch (treeCase) {
//			case get:
////				atiTree.getModelByKey(key);
////				if (targetVl == null) {
//////					throw TreeRestEE.EE.KEY_NOT_FOUND.I(key);
////					return Pare.of(400, X.f("Tree key not found.  Key '%s'", key));
////				}
//		}
//		ICtxDb tree = loadTreeFromPath();
//		Pare<Integer, String> apply = UTreeRest.apply(atiTree, getOper().toString(), getKey(), getVal());
//		return apply;
//		return Pare.of(400, "bad");
	}

//	private Ctx10Db loadTreeFromPath() {
//		Path atiTree = AFC.EVENTS.getRpaEventsStatePath(getSd3(), getPagename(), getItemname(), EXT.SQLITE);
//		Ctx10Db tree = Ctx10Db.of(atiTree);
//		if (tree.isExistDb()) {
//			return tree;
//		}
//		Path last = path3_oper();
//		boolean isPut = IT.NN(last, "except last path operation").getFileName().toString().equals("put");
//		if (isPut) {
//			tree.checkLazyCreateDb();
//		}
//		return tree;
//	}


	public String getSd3() {
		return curPPI != null ? curPPI.subdomain3Rq() : nodeID.sd3Rq();
	}

	private String getPagename() {
		return curPPI != null ? curPPI.pathStr(1) : nodeID.pageRq();
	}

	private String getItemname() {
		return curPPI != null ? curPPI.pathStr(2) : nodeID.itemRq();
	}

	public Path path(int level, Path... defRq) {
		return curPPI.path(1 + level, defRq);
	}
//
//	public Path path2_item(Path... defRq) {
//		return curPPI.path(2, defRq);
//	}
//
//	public Path path3_oper(Path... defRq) {
//		return curPPI.path(3, defRq);
//	}

	private String getOper() {
		return curPPI != null ? curPPI.pathStr(3) : oper.key();
	}

	private String getVal() {
		return curPPI != null ? curPPI.queryUrl().getFirstAsStr(NoteApi.PK_V, null) : oper.ext();
	}

	private String getKey() {
		if (curPPI == null) {
			return oper.val();
		}
		String key = curPPI.queryUrl().getFirstAsStr(NoteApi.PK_K, null);
		if (key == null) {
			throw CleanDataResponseException.C400("set key value arg <k>");
		} else if (U.__NULL__.equals(key)) {
			key = null;
		}
		return key;
	}

}
