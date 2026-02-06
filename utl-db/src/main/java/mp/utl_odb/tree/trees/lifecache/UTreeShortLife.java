package mp.utl_odb.tree.trees.lifecache;

import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.CKey;
import mpc.fs.Ns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class UTreeShortLife extends UTreeLifeCacheAbstract {

	public static final Logger L = LoggerFactory.getLogger(UTreeShortLife.class);

	@Override
	public CacheType cacheType() {
		return CacheType.SHORT_LIFE;
	}

	public static UTreeShortLife tree(String appTreeName) {
		return AppCore.of().treeShortLife(appTreeName);
	}

	public static UTreeShortLife tree(Ns ns, String dbName) {
		return tree(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static UTreeShortLife tree(Path path) {
		return (UTreeShortLife) new UTreeShortLife(path).withUpdateMode(UpdateMode.ALWAYSLAST);
	}

	public UTreeShortLife(Path path) {
		super(path);
	}

	public CtxModelCtr getModel_WithShortLife(CKey key, long shortLifeMs) throws ModelLifeMsException {
		return getModel_WithLife(key, shortLifeMs);
	}

	public CtxModelCtr getModel_WithShortLife(String key, long shortLifeMs) throws ModelLifeMsException {
		return getModel_WithLife(CKey.of(key), shortLifeMs);
	}

}
