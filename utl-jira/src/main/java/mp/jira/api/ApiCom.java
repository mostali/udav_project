package mp.jira.api;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import mp.jira.Issue0;
import mp.jira.JiraCli;

public class ApiCom extends Api0 {

	public ApiCom(JiraCli jiraCli) {
		super(jiraCli);
	}

	public Issue getIssue(String issueKey) {
		return rc().getIssueClient().getIssue(issueKey).claim();
	}

	public Issue0 getIssue0(String issueKey) {
		return Issue0.of(getIssue(issueKey));
	}

	public void deleteIssue(String issueKey, boolean deleteSubtasks) {
		rc().getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
	}

	public BasicIssue createIssue(String projectKey, Long issueType, String issueSummary) {
		return createIssue(projectKey, issueType, issueSummary, null, null);
	}

	public BasicIssue createIssue(String projectKey, Long issueType, String issueSummary, String assignee, String desc) {
		IssueRestClient issueClient = rc().getIssueClient();
		IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey, issueType, issueSummary);
		issueInputBuilder.setAssigneeName(assignee);

		issueInputBuilder.setDescription(desc);

		IssueInput newIssue = issueInputBuilder.build();
//		newIssue.getFields().set

		if (L.isInfoEnabled()) {
			L.info("Run project issue create PK={} ISSUE={} SUMMARY={} - {}", projectKey, issueType, issueSummary, newIssue);
		}
		return issueClient.createIssue(newIssue).claim();
	}

	public String createIssue(String projectCodeName, long taskTypeId, String taskTitle, String taskDescription) {
		BasicIssue task = jc._Com().createIssue(projectCodeName, taskTypeId, taskTitle);
		String issueKey = task.getKey();

		jc._Edit().updateIssueDescription(issueKey, taskDescription);

		return issueKey;
	}

}
