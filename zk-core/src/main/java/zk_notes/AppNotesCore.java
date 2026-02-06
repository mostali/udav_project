package zk_notes;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_os.AppZosCore;
import zk_os.AppZosProps;
import zk_page.core.SpVM;

import java.util.List;

public class AppNotesCore {

	public static final Logger L = LoggerFactory.getLogger(AppNotesCore.class);

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	private static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 100, 1000, 5000, 100, 0};

	public static Ctx10Db TREE_NOTIFY_GLOB() {
		Ctx10Db zkOs = Ctx10Db.of("__zk_os", "notify-global");
//		UTree uTree = UTree.treeApp("notify-global");
		zkOs.withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
		return zkOs;
	}

	public static UTree TREE_QUICK_NOTES_HISTORY() {
		return UTree.tree(AppNotesCore.class, "quick-list-" + SpVM.get().ppi().pagenameRq());
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
