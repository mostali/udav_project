package mp.jira;


import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.Getter;
import lombok.SneakyThrows;
import mp.jira.api.*;
import mpc.env.AP;
import mpc.env.APP;
import mpc.env.Env;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpe.core.P;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class JiraCli {

	public static final Logger L = LoggerFactory.getLogger(JiraCli.class);

	public static final String EPK_JIRA_URL = "JIRA_URL";
	public static final String EPK_JIRA_LOGIN = "JIRA_LOGIN";
	public static final String EPK_JIRA_PASS = "JIRA_PASS";

	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

//		getAllTasksByJql

		JiraCli jiraCli = JiraCli.buildCli_ByUsr("dav");

		Iterable<Issue> allTasksByJql = jiraCli._Jql().getAllTasksByJql("project in (NSI, BSK) AND issuetype = Sub-task AND status in (\"In Progress\", Paused) AND assignee in (currentUser())");

		X.exit(allTasksByJql);
//		Iterable<Issue> allTasks = jiraCli._Jql().getAllDefaultTasksByProjects(SPLIT.allByComma("EXP, SUP, TSE, BU, ARP, ESD, ACT, INC, EB"));
//		Issue issue = dav1.createClient().getIssue("SUP-1495556");

		P.exit(allTasksByJql);


		String orgUrl = APP.getAppOrg("org");

		NI.stop(orgUrl);

		Env.setAppName("jm");
		if (false) {
//			String urlWithComment = orgUrl+"/browse/ISUUUEEEE______ID?focusedCommentId=12137758&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";
			String urlWithComment = orgUrl + "/browse/ISUUUEEEE______ID?focusedCommentId=12177597&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-12177597";

			JiraCli cli = new JiraCliCreator("dav").buildByUsername();
			cli._Attach().downloadFilesFromComment(urlWithComment, "/home/dav/pjbf_tasks/73/", true);
			return;
		}


//		List<Issue> issues = ARR.toList(allTasks);
//		Issue dav = getWorkSubTask(dav1);
//		P.exit(dav.getId());
		String ISUUUE____ID = "ISUUUE____ID";
		String urlWithComment = orgUrl + "/browse/ " + ISUUUE____ID + "?focusedCommentId=12137758&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";

		JiraCli cli2 = new JiraCliCreator("dav").buildByUsername();
		Pare<String, Integer> stringIntegerPare = cli2._Attach().parseIssueIdAndCommentId(urlWithComment);

//		P.exit(UUrl.getHostAndPath(urlWithComment));
//		QueryUrl.of()
//		Pare<Issue, Integer> commnet = getCommentId(urlWithComment);

//		Issue issue = getIssue(myJiraClient.getJiraRestClient(), stringIntegerPare.key());
//		List<Comment> comments = Arr.toList(issue.getComments());
//		List<Attachment> attachments = ARR.toList(issue.getAttachments());
//		Attachment attachment = attachments.get(0);

//		downloadAttachs(user, issue, 12077852, Paths.get("/home/dav/pjbf_tasks/69/"));
		Sys.exit();

		//UArr.toList(cli.getTasks()).stream().forEach(StandartTypeTask.PauseSubTask::printShortInfo);
//		Issue last = getAllWorkSubTask(cli.user);
//		SubtaskControlEE.runPauseTransition(cli, last);
//		U.exit(last.getKey());
//		SubtaskControlEE.runStartTransition(cli, getIssue(cli.getJiraRestClient(), "ISUUUE____ID"));


	}

	public static JiraCli buildCli_ByUsr(String dav) {
		return new JiraCliCreator(dav).buildByUsername();
	}


	@SneakyThrows
	public static JiraCli initMode0_AP() {
		String user = AP.get("jira.user", null);
		if (user != null) {
			return new JiraCliCreator(user).buildByUsername();
		}
		String jiraurl = AP.get(EPK_JIRA_URL, null);
		String login = AP.get(EPK_JIRA_LOGIN, null);
		String pass = AP.get(EPK_JIRA_PASS, null);
		if (X.emptyAnyStr(jiraurl, login)) {
			throw new FIllegalStateException("set all keys to app.props 'jira.url' + 'jira.login' + 'jira.pass'");
		}
		return JiraCliCreator.getOrCreate(IT.isUrl0(jiraurl).toString(), new String[]{login, pass});
	}


	@SneakyThrows
	public static JiraCli ofAuth(Object auth) {
		auth = auth == null ? new String[0] : (auth instanceof String ? new String[]{(String) auth} : auth);
		JiraCli rsp;
		if (auth instanceof String) {
			rsp = new JiraCliCreator((String) auth).buildByUsername();
		} else if (auth instanceof Path) {
			throw new UnsupportedOperationException("need impl path auth");
		} else if (auth instanceof Integer) {
			Integer mode = (Integer) auth;
			switch (mode) {
				case 0:
					rsp = initMode0_AP();
					break;
				case 1:
					String userName = Env.getUserName();
					rsp = new JiraCliCreator(userName).buildByUsername();
					break;
				default:
					throw new WhatIsTypeException(mode);
			}
		} else if (String[].class.isAssignableFrom(auth.getClass())) {
			rsp = JiraCliCreator.createByHlp((String[]) auth).toJiraClient();
		} else {
			throw new WhatIsTypeException(auth.getClass() + "-->" + auth);
		}
		return rsp;
	}

	private final @Getter JiraUser user;

	private JiraRestClient restClient;

	public JiraCli(JiraUser user) {
		this.user = user;
	}


	public JiraRestClient getRestClient() {
		if (this.restClient != null) {
			return this.restClient;
		}
		if (L.isInfoEnabled()) {
			L.info("JiraRestClient to {} with user {}", user.getUrl2jira(), user.getLogin());
		}
		return this.restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(user.getUrl2jira()), user.getLogin(), user.getPass());
	}


	//
	//


	public ApiEdit _Edit() {
		return new ApiEdit(this);
	}

	public ApiCom _Com() {
		return new ApiCom(this);
	}

	public ApiTransition _Transition() {
		return new ApiTransition(this);
	}

	public ApiAttach _Attach() {
		return new ApiAttach(this);
	}

	public ApiJql _Jql() {
		return new ApiJql(this);
	}


	public ApiExp _Exp() {
		return new ApiExp(this);
	}


}
