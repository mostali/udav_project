package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import zk_notes.node.NodeDir;
import zk_notes.node_state.impl.FormState;
import zk_os.core.Sdn;

@RequiredArgsConstructor
public class NewNote {
	public final String[] hlp;
	public final Sdn sdn;

	public final String name;

	public String formName() {
		return name;
	}

	public FormState formState() {
		return FormState.ofName(sdn, formName());
	}

	public NodeDir nodeDir() {
		return NodeDir.ofNodeName(sdn, formName());
	}

	public boolean isNewProps() {
		return !formState().existPropsFile();
	}

	public boolean isNewData() {
		return !formState().existPropsFile(true);
	}

	public boolean isNewIssueInTree() {
		return RecoveryState.getRecModel(formName(), null) == null;
	}

}
