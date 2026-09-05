package mp.jira.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import mp.jira.JiraCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class Api0 {

	public static final Logger L = LoggerFactory.getLogger(Api0.class);

	public final JiraCli jiraClient;

	public JiraRestClient rc() {
		return jiraClient.getRestClient();
	}

}
