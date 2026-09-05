//package mp.utl_odb.tree.trees.lifecache;
//
//import mp.utl_odb.netapp.AppCore;
//import mp.utl_odb.tree.ctxdb.CKey;
//import mpc.fs.Ns;
//import mpe.db.JdbcUrl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.nio.file.Path;
//
//public class UTreeTtlNew extends UTreeLifeCacheAbstract {
//
//	public static final Logger L = LoggerFactory.getLogger(UTreeTtlNew.class);
//
//	@Override
//	public CacheType cacheType() {
//		return CacheType.SHORT_LIFE;
//	}
//
//	public static UTreeTtlNew tree(String appTreeName) {
//		return AppCore.of().treeShortLife(appTreeName);
//	}
//
//	public static UTreeTtlNew tree(Ns ns, String dbName) {
//		return tree(ns.path(JdbcUrl.buildDbFileName(dbName)));
//	}
//
//	public static UTreeTtlNew tree(Path path) {
//		return (UTreeTtlNew) new UTreeTtlNew(path).withUpdateMode(UpdateMode.ALWAYSLAST);
//	}
//
//	public UTreeTtlNew(Path path) {
//		super(path);
//	}
//
//	public CtxModelCtr getModel_WithMaxTtl(CKey key, long shortLifeMs) throws ModelLifeMsException {
//		return getModel_WithMaxTTL(key, shortLifeMs);
//	}
//
//	public CtxModelCtr getModel_WithMaxTtl(String key, long shortLifeMs) throws ModelLifeMsException {
//		return getModel_WithMaxTTL(CKey.of(key), shortLifeMs);
//	}
//
////	public CtxModelCtr getModel_WithMaxTtl(String key, long shortLifeMs, Function<String, CtxModelCtr> loader) throws ModelLifeMsException {
////		return getModel_WithMaxTTL(CKey.of(key), shortLifeMs);
////	}
//
//
//}
