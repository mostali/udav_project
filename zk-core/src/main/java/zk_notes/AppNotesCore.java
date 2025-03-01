package zk_notes;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.trees.UTreeAutoCleaner;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.AppZosProps;
import zk_page.core.SpVM;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AppNotesCore {

	public static final Logger L = LoggerFactory.getLogger(AppNotesCore.class);

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	public static UTree TREE_QUICK_HISTORY__PAGED() {
		return UTree.tree(AppNotesCore.class, "quick-list-" + SpVM.get().ppi().pagename0());
	}

	@Deprecated
	public static List<String> loadTopCtxValues(String listName) {
		String pares = TREE_QUICK_HISTORY__PAGED().get(listName);
		List<String> paresList = SPLIT.allByNL(pares);
		return paresList;

	}

	@RequiredArgsConstructor
	public static class UTreeAuth {

		public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 100, 0};

		public static UTree getTreeDb() {
			return (UTree) UTree.tree(AppNotesCore.class, "auth").setAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
		}

		public static CtxtDb.CtxTimeModel getRowByToken(String token) {
			UTree authTree = AppNotesCore.UTreeAuth.getTreeDb();
			CtxtDb.CtxTimeModel ctxTimeModelByValue0 = authTree.getCtxTimeModelByValue(token);
			return ctxTimeModelByValue0;
		}

		public static void store(String nid, String token, CharSequence json) {
			getTreeDb().put(nid, token, json);
		}

	}

}
