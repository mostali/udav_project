package zk_pages.zznsi_pages.jira_tasks.core;

import mpe.cmsg.std.JqlCallMsg;
import mpe.img.EColor;
import mpu.core.ARR;
import mpu.str.STR;
import org.zkoss.zul.Window;
import zk_notes.factory.NFNew;
import zk_os.core.Sdn;

public class NewNoteJql extends NewNote {

	public NewNoteJql(String[] hlp, Sdn sdn, String name) {
		super(hlp, sdn,name);
	}

	public static NewNoteJql of(String[] hlp, Sdn sdn, String name) {
		return new NewNoteJql(hlp, sdn, name);
	}

	Window openNew(boolean isUp, String jqlExpression) {

		if (jqlExpression == null) {
			return null;
		}

		RecoveryState.RecModel recModel = RecoveryState.getRecModel(formName(), null);

		//hack - because store before?
		jqlExpression = STR.removeStartString(jqlExpression, JqlCallMsg.LINE0, true);

		String dataNote = JqlCallMsg.LINE0 + jqlExpression;

		if (recModel == null) {

			//it new
			return openNewSuper(dataNote);

		} else {

			return openExistedRec(dataNote, recModel);

		}


	}

	private Window openNewSuper(String dataNote) {

		NFNew.OptsAdd optsAdd = NFNew.OptsAdd.newOpts();
		optsAdd.getOptBe().setLinkIsVisible(false);
		optsAdd.getOptBe().setZkColor(EColor.GRAY);
		optsAdd.getOptBe().setTop_left(ARR.of(10.0, 65.0));
		optsAdd.getOptBe().setWidth_height(ARR.of(550, 250));
		optsAdd.getOptBe().setNoteSize(2);

		return NFNew.openNewRewrite(sdn, formName(), dataNote, optsAdd).val();
	}

	private Window openExistedRec(String dataNote, RecoveryState.RecModel recModel) {
		if (recModel == null) {
			recModel = RecoveryState.getRecModel(formName(), null);
		}

		Window window = NFNew.openNewINE(sdn, formName(), dataNote).val();

		new RecoveryStateDeserialize(window, formState()).deserializeNewOrPrev(recModel);

		return window;
	}
}
