package zk_notes;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.AppZosProps;
import zk_page.core.SpVM;

import java.util.List;

public class AppNotesCore {

	public static final Logger L = LoggerFactory.getLogger(AppNotesCore.class);

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	public static UTree TREE_QUICK_HISTORY__PAGED() {
		return UTree.tree(AppNotesCore.class, "quick-list-" + SpVM.get().ppi().pagename0());
	}

	@Deprecated
	public static List<String> loadTopCtxValues(String listName) {
		String pares = TREE_QUICK_HISTORY__PAGED().getValue(listName);
		List<String> paresList = SPLIT.allByNL(pares);
		return paresList;

	}

	@RequiredArgsConstructor
	public static class UTreeAuth {

		public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 100, 0};

		public static UTree getTreeDb() {
			return (UTree) UTree.tree(AppNotesCore.class, "auth").withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
		}

		public static Ctx3Db.CtxModelCtr getRowByToken(String token) {
			UTree authTree = AppNotesCore.UTreeAuth.getTreeDb();
			Ctx3Db.CtxModelCtr ctxTimeModelByValue0 = authTree.getModelBy(CKey.Val.of(token));
			return ctxTimeModelByValue0;
		}

		public static void store(String nid, String token, CharSequence json) {
			getTreeDb().put(nid, token, json);
		}

	}

}
