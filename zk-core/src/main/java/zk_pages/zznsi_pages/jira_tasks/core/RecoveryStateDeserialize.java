package zk_pages.zznsi_pages.jira_tasks.core;

import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.types.tks.FID;
import mpu.IT;
import mpu.X;
import org.jetbrains.annotations.Nullable;
import org.zkoss.zul.Window;
import zk_notes.node_state.FileState;
import zk_notes.node_state.impl.FormState;

public class RecoveryStateDeserialize extends RecoveryState {

	private final Window window;

	public RecoveryStateDeserialize(FormState formState) {
		this(null, formState);
	}

	public RecoveryStateDeserialize(Window window, FormState formState) {
		super(formState);
		this.window = window;
	}

	public boolean deserializeNewOrPrev(RecoveryState.RecModel recModel) {

		L.info("deserializeNewOrPrev starting in db -> {}", TREE().toStringLog());

//		RecModel recModel = RecoveryState.getRecModel(formName(), null);

		IT.state(recModel != null, "here must be model. check ranee model?");

		boolean has = false;

		{   // up new state
			GsonMap valueAs = (GsonMap) recModel.m10.getValueAs(GsonMap.class, null);
			if (valueAs != null) {
				String stringPrettyJson = valueAs.toStringPrettyJson();
				formState.writeFcProps(stringPrettyJson);
				L.info("applyNewOrPrev FORMSTATE {} -> {}", formState.nodeName(), X.toStringLine(stringPrettyJson));
				has = true;
			}
		}
		{ //store ext node data
			String extVal = recModel.m10.getExt();
			if (extVal != null) {
				formState.writeFcData(extVal, FileState.FILE_OUT_OK);
				L.info("applyNewOrPrev EXT {} -> {} ", formState.nodeName(), X.toStringLine(extVal));
				has = true;
			}
		}

		return has;


	}


}
