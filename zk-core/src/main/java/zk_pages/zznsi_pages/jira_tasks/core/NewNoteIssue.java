package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import mpe.cmsg.std.JqlCallMsg;
import mpu.X;
import mpu.core.ARR;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.Sb;
import org.zkoss.zul.Window;
import udav_net.bincall.jira.IssueContract;
import zk_notes.factory.NFForm;
import zk_notes.factory.NFNew;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.FormState;
import zk_os.coms.AFCC;
import zk_os.core.Sdn;
import zk_pages.zznsi_pages.jira_tasks.JtApp;

import java.util.List;
import java.util.Map;

public class NewNoteIssue extends NewNote {

//	public static FunctionV1<FormState> createDefault = (formState) -> {
//		formState.set(ObjState.NOTE_SIZE, 2);
//		formState.set(CN.WIDTH, "400px");
//		formState.set(CN.HEIGHT, "250px");
//
//		if (!WebUsr.isAnonim()) {
//			formState.set("user", WebUsr.login());
//		}
//		formState.set("created", QDate.now().mono14_y4s2());
//
//		formState.set(ObjState.LINK_VISIBLE, false);
//	};

	public final IssueContract issueContact;

	@Override
	public String formName() {
		return issueContact.getKey();
	}

	public NewNoteIssue(String[] hlp, Sdn sdn, IssueContract issueContact) {
		super(hlp, sdn, issueContact.getKey());
		this.issueContact = issueContact;
	}

	public static NewNoteIssue of(String[] hlp, Sdn sdn, IssueContract issueContact) {
		return new NewNoteIssue(hlp, sdn, issueContact);
	}

	public boolean hasStatus_Old() {
		if (issueContact == null) {
			return false;
		}
		List<String> labels = SPLIT.allByComma(STR.unwrapBody(issueContact.getLabels(), "[", "]"));
		return ARR.containsAny(labels, "rmm", "old");
	}

	public Window build(boolean isUp) {

		if (hasStatus_Old()) {
			return null;
		}

		String dataNote = buildDataNote();

		RecoveryState.RecModel recModel = RecoveryState.getRecModel(formName(), null);

		if (recModel == null) {
			//it new issue
			return openNewDefault(dataNote);
		}

		new RecoveryStateDeserialize(formState()).deserializeNewOrPrev(recModel);

		boolean isNewFile = isNewProps();
		boolean isNewData = isNewData();

		String nodeName = formName();
		if (isNewFile) {
			return NFNew.openNewWithState(sdn, nodeName, dataNote, null);
		} else {
			if (isNewData) {
				FormState props = AppStateFactory.ofFormName_WithContent(sdn, nodeName, dataNote, true);
			}
			Window window = NFForm.openFormRequired(sdn, name);
//			Pare<NodeDir, Window> nodeDirWindowPare = (Pare<NodeDir, Window>) window;
//			Pare<NodeDir, Window> nodeDirWindowPare = NFNew.openINE(sdn, name, dataNote);
			return window;
		}

	}


	@RequiredArgsConstructor
	public static class NewNode {
		final String data, prorps;
	}

	private Window openNewDefault(String dataNote) {

		NFNew.OptsAdd optsAdd = NFNew.OptsAdd.newOpts();

		NFNew.OptsBe optBe = optsAdd.getOptBe();

		IssueContract.PrioType priorityType = issueContact.getPriorityType(IssueContract.PrioType.UNDEFINED);

		optBe.setZkColor(priorityType.zkColor);
		optBe.setWidth_height(ARR.of(500, 250));
		optBe.setNoteSize(2);
		optBe.setLinkIsVisible(false);
		optBe.setPos(ObjState.Position.REL);
		//			formState.set("user", WebUsr.login());

		Window win = NFNew.openNewRewrite(sdn, formName(), dataNote, optsAdd).val();

		return win;

	}


	public String buildDataNote() {


		String urlJiraHost = JtApp.toLinkIssue(hlp[2], formName());

		String summary = issueContact.getSummary();

		//
		//

		Map statusMap = issueContact.getStatus(null);
		Map issueTypeMap = issueContact.getIssueType(null);
		Map prioMap = issueContact.getPriority(null);

		IssueContract.StatusType statusType = issueContact.getStatusType(null);
		IssueContract.IssueType issueType = issueContact.getIssueTypeType(null);
		IssueContract.PrioType priorityType = issueContact.getPriorityType(null);

//		Function<String, String> statuser = SYMJ.getter(SYMJ.ROUND_DBL, status, s -> X.notEmpty(status));

		//
		//

		Sb sb = new Sb();
		sb.NL(JqlCallMsg.LINE0 + urlJiraHost);
		sb.NL(summary);

		sb.append(JtApp.TASK_ICON + (priorityType != null ? priorityType : prioMap));
		//
		//
		if (X.notNullAll(issueType, statusType)) {
			sb.append(" - " + issueType + " - " + statusType);
		} else {
			sb.NL("IssueType: " + (issueType != null ? issueType : issueTypeMap));
			sb.NL();
			sb.NL("StatusType: " + (statusType != null ? statusType : statusMap));
			sb.NL();
		}


		return sb.toString();
	}
}
