package mp.jira.api;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import mp.jira.JiraCli;
import mpc.env.Env;
import mpc.json.GsonMap;
import mpu.X;
import mpu.core.ARG;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Issue0 {

    public static class IssueQk extends Issue0 {

        public IssueQk(Issue issueKey) {
            super(issueKey);
        }

        @Override
        public JiraCli jc() {
            return null;
        }
    }

    ;
    private final String issueKey;
    private Issue issue;

    public static void main(String[] args) {
        Issue0 issue0 = loadIssue("NSI-3479");
        X.exit(issue0.getComments());
        X.exit(issue0);
    }

    public static Issue0 loadIssue(String issueKey) {
        return loadIssue(Env.getUserName(), issueKey);
    }

    public static Issue0 loadIssue(String user, String issueKey) {
        JiraCli jiraCli = JiraCli.buildCli_ByUsr(user);
        Issue issue = jiraCli._Com().getIssue(issueKey);
        return new Issue0(issue) {
            @Override
            public JiraCli jc() {
                return jiraCli;
            }
        };
    }

    public static Issue0 loadIssue(String[] hlp, String issueKey) {
        JiraCli jiraCli = JiraCli.ofAuth(hlp);
        Issue issue = jiraCli._Com().getIssue(issueKey);
        return new Issue0(issue) {
            @Override
            public JiraCli jc() {
                return jiraCli;
            }
        };
    }

    public Issue0(String issueKey) {
        this.issueKey = issueKey;
    }

    public Issue0(Issue issue) {
        this.issueKey = issue.getKey();
        this.issue = issue;
    }

    public static List<Comment> getAllCommentsOrReload(Issue0 issue) {
        List<Comment> allComments = getAllComments(issue.getIssueOrLoad());
        if (X.notEmpty(allComments)) {
            return allComments;
        }
        allComments = issue.jc().issue(issue.issueKey).getComments();
        return allComments;
    }

    public static List<Comment> getAllComments(Issue issue) {
        return StreamSupport.stream(issue.getComments().spliterator(), false).collect(Collectors.toList());
    }

    public static String toJson(Object issue) {
        return GsonMap.toMapFromObj(issue).toStringPrettyJson();
    }

    public List<Comment> getComments() {
        return getAllComments(issue);
    }


    public Issue getIssueOrLoad(boolean... fresh) {
        if (issue == null || ARG.isDefEqTrue(fresh)) {
            return issue = jc()._Com().getIssue(issueKey);
        }
        return issue;
    }

    public abstract JiraCli jc();
//    public  JiraCli jc(){
//        throw new UnsupportedOperationException("nio");
//    }
}
