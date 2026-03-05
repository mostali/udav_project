package mp.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import mpc.json.GsonMap;

@RequiredArgsConstructor
public class Issue0 {

	public final Issue issue;

	public static Issue0 of(Issue issue) {
		return new Issue0(issue);
	}

	public <T> T toStringJsonObj() {
		return (T) GsonMap.toMapFromObj(issue);
	}

	@Override
	public String toString() {
		return "Issue0:" + issue;
	}
}
