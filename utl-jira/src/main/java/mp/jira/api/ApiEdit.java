package mp.jira.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import mp.jira.JiraCli;
import mp.jira.JiraCliCreator;
import mpu.IT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ApiEdit extends Api0 {

	public ApiEdit(JiraCli jiraCli) {
		super(jiraCli);
	}

	public void addLabel(String issueKey, String... labels) {
		IT.notEmpty(labels, "labels is empty");

		ApiCom apiCom = jc._Com();
		Issue issue = apiCom.getIssue(issueKey);

		Set existLabels = issue.getLabels();
		List existLableList = Arrays.asList(existLabels);
		List newLableList = new ArrayList();
		for (String lbl : labels) {
			if (!existLableList.contains(lbl)) {
				newLableList.add(lbl);
			}
		}
		if (newLableList.isEmpty()) {
			return;
		}
		IssueInputBuilder ib = new IssueInputBuilder();
		existLabels.addAll(newLableList);
		ib.setFieldValue(IssueFieldId.LABELS_FIELD.id, existLabels);

		apiCom.rc().getIssueClient().updateIssue(issue.getKey(), ib.build()).claim();
	}

	public Issue changeAssignee(String issueKey, String assignee) {
		IT.notEmpty(issueKey, "issueKey is empty");
		IT.notEmpty(assignee, "assignee is empty");
		ApiCom apiCom = jc._Com();
		Issue issue = apiCom.getIssue(issueKey);
		IssueInputBuilder ib = new IssueInputBuilder();
		ib.setAssigneeName(assignee);

		apiCom.rc().getIssueClient().updateIssue(issue.getKey(), ib.build()).claim();

		return issue;
	}

	public void updateIssueDescription(String issueKey, String newDescription) {
		IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
		rc().getIssueClient().updateIssue(issueKey, input).claim();
	}

}
