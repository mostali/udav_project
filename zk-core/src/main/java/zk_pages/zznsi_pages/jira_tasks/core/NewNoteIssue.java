package zk_pages.zznsi_pages.jira_tasks.core;

import mpe.cmsg.std.JqlCallMsg;
import mpu.X;
import mpu.core.ARR;
import mpu.str.SPLIT;
import mpu.str.Sb;
import mpu.str.WRAP;
import org.zkoss.zul.Window;
import udav_net.bincall.jira.IssueContract;
import zk_notes.factory.NFNew;
import zk_notes.node_state.ObjState;
import zk_os.core.Sdn;
import zk_pages.zznsi_pages.jira_tasks.JtApp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NewNoteIssue extends NewNoteJt {

	public final IssueContract issueContact;

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
		List<String> labels = SPLIT.allByComma(WRAP.unwrapBody(issueContact.getLabels(), "[", "]"));
		return ARR.containsAny(labels, "rmm", "old");
	}

	public Window build(boolean isUp) {

		if (hasStatus_Old()) {
			return null;
		}

		String dataNote = buildNodeData().get();

		RecoveryState.RecModel recModel = RecoveryState.getRecModel(nodeName(), null);

		if (recModel == null) {
			//it new issue
			return openNewDefault(dataNote);
		}

		new RecoveryStateDeserialize(formState()).deserializeNewOrPrev(recModel);

		boolean isNewFile = isNewProps();
		boolean isNewData = isNewData();

		String nodeName = nodeName();

		NewNoteJt newNote0 = new NewNoteJt(hlp, sdn, nodeName);

		if (isNewFile) {
			return newNote0.writeFormAndOpenWindow(Optional.of(dataNote), null);
		} else {
			if (isNewData) {
				newNote0.writeForm(Optional.of(dataNote), null, null);
			}
			return newNote0.openRequired();
		}

	}

//
//    @RequiredArgsConstructor
//    public static class NewNode {
//        final String data, prorps;
//    }

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

		Window win = NFNew.openNewForce(sdn, nodeName(), dataNote, optsAdd).val();

		return win;

	}


	public Optional<String> buildNodeData() {

		String urlJiraHost = JtApp.toLinkIssue(hlp[2], nodeName());

		String summary = issueContact.getSummary();

		//
		//

		Map statusMap = issueContact.getStatus(null);
		Map issueTypeMap = issueContact.getIssueType(null);
		Map prioMap = issueContact.getPriority(null);

		IssueContract.StatusType statusType = issueContact.getStatusType(null);
		IssueContract.IssueType issueType = issueContact.getIssueTypeType(null);
		IssueContract.PrioType priorityType = issueContact.getPriorityType(null);

		//
		//

		Sb sb = new Sb();
		sb.NL(JqlCallMsg.LINE0 + urlJiraHost);
//        sb.NL();

		//
		//

		String cmtSize = " " + JtApp.ICO_COMMENT + "*" + X.sizeOf(issueContact.getComments(ARR.EMPTY_LIST));

		if (X.notNullAll(issueType, statusType)) {
			sb.append(" " + JtApp.ICO_TYPE + issueType.nameRu);
			sb.append(" " + JtApp.ICO_STATUS + statusType.nameRu);
			sb.append(" " + cmtSize);
			sb.NL();
		} else {
			sb.NL(JtApp.ICO_TYPE + "IssueType: " + (issueType != null ? issueType : issueTypeMap));
			sb.NL(JtApp.ICO_STATUS + "StatusType: " + (statusType != null ? statusType : statusMap) + cmtSize);
			sb.NL();
		}

		sb.append(" " + JtApp.ICO_USER + issueContact.assignee());
		sb.append(JtApp.ICO_PRIO + (priorityType != null ? priorityType.nameRu : prioMap));
		sb.ENDLINE();

		sb.NL(summary);

		return Optional.of(sb.toString());
	}
}
