package mp.jira.api;

import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Issue;
import mp.jira.JiraCli;

public class ApiExp extends Api0 {

	public ApiExp(JiraCli jiraCli) {
		super(jiraCli);
	}

	public void voteForAnIssue(Issue issue) {
		rc().getIssueClient().vote(issue.getVotesUri()).claim();
	}

	public int getTotalVotesCount(String issueKey) {
		BasicVotes issueVotes = jc._Com().getIssue(issueKey).getVotes();
		return issueVotes == null ? 0 : issueVotes.getVotes();
	}


}
