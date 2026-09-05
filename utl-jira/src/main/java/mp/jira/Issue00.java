//package mp.jira;
//
//import com.atlassian.jira.rest.client.api.domain.Issue;
//import lombok.RequiredArgsConstructor;
//import mpc.json.GsonMap;
//
//@RequiredArgsConstructor
//public class Issue00 {
//
//	public final Issue issue;
//
//	public static Issue00 of(Issue issue) {
//		return new Issue00(issue);
//	}
//
//	public <T> T toStringJsonObj() {
//		return (T) GsonMap.toMapFromObj(issue);
//	}
//
//	@Override
//	public String toString() {
//		return "Issue0:" + issue;
//	}
//
//    public void getComments() {
//    }
//}
