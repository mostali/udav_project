package mp.utl_odb.tree.trees.lifecache;

import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.CKey;
import mpc.fs.Ns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class UTreeEveryLife extends UTreeLifeCacheAbstract {

	public static final Logger L = LoggerFactory.getLogger(UTreeEveryLife.class);

	public static final UTreeEveryLife APP_TREE_LIMIT_ACTION_EVERY = (UTreeEveryLife) AppCore.of().treeEveryLife("limit-action").withAutoCleanCfg(new Integer[]{0, 1000, -1, 5000, 1000, 0});

	@Override
	public CacheType cacheType() {
		return CacheType.EVERYMS;
	}

	public static UTreeEveryLife tree(String appTreeName) {
		return AppCore.of().treeEveryLife(appTreeName);
	}

	public static UTreeEveryLife tree(Ns ns, String dbName) {
		return tree(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static UTreeEveryLife tree(Path path) {
		return (UTreeEveryLife) new UTreeEveryLife(path).withUpdateMode(UpdateMode.ALWAYSLAST);
	}

	public UTreeEveryLife(Path path) {
		super(path);
	}


	public CtxModelCtr getModel_WithEveryLife(String key, long lifeEveryMs) throws ModelLifeMsException {
		return getModel_WithLife(CKey.of(key), lifeEveryMs);
	}

}
