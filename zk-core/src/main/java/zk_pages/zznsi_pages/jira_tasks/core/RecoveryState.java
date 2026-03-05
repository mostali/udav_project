package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.types.tks.FID;
import mpe.cmsg.std.JqlCallMsg;
import mpu.X;
import mpu.core.ARG;
import zk_notes.node.NodeDir;
import zk_notes.node_state.impl.FormState;
import zk_os.db.net.WebUsr;
import zk_pages.zznsi_pages.jira_tasks.JtApp;

@RequiredArgsConstructor
public class RecoveryState {

	public final FormState formState;

	public String formName() {
		return formState.objName();
	}

	public static Ctx10Db TREE() {
		return JtApp.RECOVERY_STATE_TREE();
	}

	public static FID toTaskUserKey(String issueKey) {
		return FID.of(WebUsr.login(), issueKey);
	}

	public static RecoveryStateSerialize JQL() {
		return new RecoveryStateSerialize(NodeDir.ofCurrentPage(JqlCallMsg.TYPE).state());
	}

	@RequiredArgsConstructor
	public abstract static class RecModel {
		final Ctx10Db.CtxModel10 m10;

		public static RecModel createNew(String nodeName, String val, String ext) {
			Ctx10Db tree = RecoveryState.TREE();
			Ctx10Db.CtxModel10 newRecoveryState = tree.put(RecoveryState.toTaskUserKey(nodeName).toString(), val, ext);
			return RecModel.of(newRecoveryState, tree);
		}

		@Override
		public String toString() {
			return m10.getKey() + "::" + m10.getExt() + "\nStore in " + db().toStringLog();
		}

		public abstract ICtxDb db();

		public static RecModel of(Ctx10Db.CtxModel10 model, ICtxDb db) {
			return new RecModel(model) {

				@Override
				public ICtxDb db() {
					return db;
				}
			};
		}

	}

	public static RecModel getRecModel(String issueKey, RecModel... defRq) {
		Ctx10Db tree = TREE();
		Ctx10Db.CtxModel10 modelByKey = tree.getModelByKey(toTaskUserKey(issueKey).toString());
		return modelByKey != null ? RecModel.of(modelByKey, tree) : ARG.throwMsg(() -> X.f("Except RecModel by key '%s'", issueKey), defRq);
	}

//	public static RecModel putRecModel(String issueKey, String data, RecModel... defRq) {
//		Ctx10Db tree = TREE();
//		Ctx10Db.CtxModel10 modelByKey = tree.getModelByKey(toTaskUserKey(issueKey).toString());
//		return modelByKey != null ? RecModel.of(modelByKey, tree) : ARG.throwMsg(() -> X.f("Except RecModel by key '%s'", issueKey), defRq)
//	}
}
