package udav_gl;

import lombok.SneakyThrows;
import mpu.X;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.util.List;

public class UGL {

    static String project = "root/tt";  // или ID проекта (12345)
    static String token = "glpat-MWUGOdG6SPxkpwV7jUkp1286MQp1OjEH.01.0w1trbdvu";  // Personal Access Token
    static String host = "http://q.com:8585";

    @SneakyThrows
    public static void main(String[] args) {


        // Create a GitLabApi instance
        GitLabApi gitLabApi = new GitLabApi(host, token);

        Pager<Issue> issues = gitLabApi.getIssuesApi().getIssues(1234);

        if (true) {
            while (issues.hasNext()) {
                List<Issue> next = issues.next();
                X.p("part:");
                X.p(next);
            }

            X.exit(issues);
        }

// You can now use the various APIs, for example, to get a list of projects
        try {
            List<Project> projects = gitLabApi.getProjectApi().getProjects();
            for (Project project : projects) {
//                project.
                        System.out.println(project.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
