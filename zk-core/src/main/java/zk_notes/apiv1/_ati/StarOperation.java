package zk_notes.apiv1._ati;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.STR;
import udav_net.apis.zznote.NoteApi;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class StarOperation extends TreeOper {

	public StarOperation(TreeRestCall treeRestCall, int level) {
		super(treeRestCall, level);
	}

	@Override
	public Pare<Integer, String> apply() {
		if (getClass() != StarOperation.class) {
			return rspTreeLs();
		}
		return new StarOperationDefault(treeRestCall, level).apply();
	}

	@Override
	public Pare<Integer, String> applyRoot() {
		return Pare.of(CODE_NI, "applyRoot^" + level);
	}

	public String getKeyItem(String... defRq) {
		Path path = pathKey(level, null);
		if (path == null) {
			String keyQueryItem = getKeyQueryItem(null);
			if (keyQueryItem != null) {
				return getKeyQueryItem(keyQueryItem);
			}
		}
		return path != null ? path.toString() : ARG.toDefThrowMsg(() -> X.f("Not found key(use path or query arg 'k'"), defRq);
	}

	public String getKeyQueryItem(String... defRq) {
		return treeRestCall.curPPI.queryUrl().getFirstAsStr(NoteApi.PK_K, defRq);
	}

	public String getValItem(String... defRq) {
		return treeRestCall.curPPI.queryUrl().getFirstAsStr(NoteApi.PK_V, defRq);
	}

	@Override
	public ICtxDb tree(ICtxDb... defRq) {
		Path atiTreePath = getAtiTreePath();
		return ICtxDb.of(atiTreePath, defRq);
	}

	public ICtxDb treeOrCreate() {
		ICtxDb tree = tree(null);
//		Path atiTreePath = getAtiTreePath();
		return tree != null ? tree : Ctx10Db.of(getAtiTreePath()).withCreateDbIfNotExist(true);
	}

	private Pare<Integer, String> rspTreeLs() {
		List<ICtxDb.CtxModel> treeModels = treeModels();
		String rspTreeItems = treeModels.stream().map(m -> m.getKey()).collect(Collectors.joining(STR.NL));
		if (X.empty(treeModels)) {
			return Pare.of(CODE_EMPTY_TREE, "(empty tree)^" + level);
		}
		return Pare.of(CODE_SUCCES_GET, rspTreeItems);
	}


	class StarOperationDefault extends StarOperation {

		public StarOperationDefault(TreeRestCall treeRestCall, int level) {
			super(treeRestCall, level);
		}

		@Override
		public Pare<Integer, String> apply() {
			String key = getKeyItem(null);
//			String key = getKeyItem(null);
			if (X.notEmpty(key)) {
				ICtxDb tree = tree(null);
				if (tree == null) {
					return Pare.of(CODE_TREE_NOT_FOUND, "except tree (put before?)");
				}
				return Pare.of(CODE_SUCCES_GET, tree.getValue(key));
			}
			return super.apply();//default
		}
	}

	private List<ICtxDb.CtxModel> treeModels() {
		ICtxDb tree = tree(null);
		return tree != null ? tree.getModels() : ARR.EMPTY_LIST;
	}

}
