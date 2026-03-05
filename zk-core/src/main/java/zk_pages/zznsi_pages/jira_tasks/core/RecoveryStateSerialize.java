package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.types.tks.FID;
import mpe.cmsg.std.JqlCallMsg;
import mpu.X;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FileState;
import zk_notes.node_state.impl.FormState;
import zk_os.db.net.WebUsr;
import zk_pages.zznsi_pages.jira_tasks.JtApp;

public class RecoveryStateSerialize extends RecoveryState {

	public RecoveryStateSerialize(FormState formState) {
		super(formState);
	}

	public void serialize() {
		storeSnapshotModel(formState);
	}

	private static void storeSnapshotModel(FormState issueFormState) {

		Ctx10Db uTree = TREE();

		L.info("storeSnapshotModel starting in db -> {}", uTree);

		String dataNodeChild2 = issueFormState.readFcData(FileState.FILE_OUT_OK, null);

		if (X.empty(dataNodeChild2)) {
			L.info("storeSnapshotModel out not found");
			return;
		}


		String issueKey = issueFormState.nodeName();
		FID fidKey = toTaskUserKey(issueKey);

		Ctx10Db.CtxModel10 m = uTree.getModelByKeyOrCreate(fidKey.toString());

		{
			m.setExt(dataNodeChild2);
		}

		{ //store full json state
			GsonMap gsonMap = issueFormState.readFcDataAsGsonMap(null);
			if (gsonMap != null) {
				m.setValue(gsonMap.toStringPrettyJson());
			}
		}


		uTree.saveModelAsUpdate(m);

		L.info("storeSnapshotModel {} -> {} ", issueFormState.nodeName(), m);

	}
}
