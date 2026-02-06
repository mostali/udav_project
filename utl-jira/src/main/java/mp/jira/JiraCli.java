package mp.jira;


import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.env.EnvTlp;
import mpc.exception.NI;
import mpc.types.opts.SeqOptions;
import mpe.core.P;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpc.arr.STREAM;
import mpc.env.AP;
import mpc.env.Env;
import mpc.exception.EException;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.url.Url0;
import mpc.net.CON;
import mpc.net.DLD;
import mpu.core.ENUM;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.pare.Pare;
import mpu.str.ToString;
import mpe.str.URx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraCli {

	public static final Logger L = LoggerFactory.getLogger(JiraCli.class);

	public static final String EPK_JIRA_URL = "JIRA_URL";
	public static final String EPK_JIRA_LOGIN = "JIRA_LOGIN";
	public static final String EPK_JIRA_PASS = "JIRA_PASS";

	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, SubtaskControlEE {


		JiraUser dav1 = createUsr_DefaultORG("dav");
//		Iterable<Issue> allTasks1 = myJiraClient.getAllTasks();
		Iterable<Issue> allTasks = getAllDefaultTasksByProjects(dav1, SPLIT.allByComma("EXP, SUP, TSE, BU, ARP, ESD, ACT, INC, EB"));
//		Issue issue = dav1.createClient().getIssue("SUP-1495556");

		P.exit(allTasks);


		String orgUrl = APP.getAppOrg("org");

		NI.stop(orgUrl);

		Env.setAppName("jm");
		if (false) {
//			String urlWithComment = orgUrl+"/browse/ISUUUEEEE______ID?focusedCommentId=12137758&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";
			String urlWithComment = orgUrl + "/browse/ISUUUEEEE______ID?focusedCommentId=12177597&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-12177597";
			downloadFilesFromComment("dav", urlWithComment, "/home/dav/pjbf_tasks/73/", true);
			return;
		}


//		List<Issue> issues = ARR.toList(allTasks);
//		Issue dav = getWorkSubTask(dav1);
//		P.exit(dav.getId());
		String ISUUUE____ID = "ISUUUE____ID";
		String urlWithComment = orgUrl + "/browse/ " + ISUUUE____ID + "?focusedCommentId=12137758&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";

		Pare<String, Integer> stringIntegerPare = Issue0.parseIssueIdAdnCommentId(urlWithComment);

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
//		U.exit();

//		List<Issue> worked = toList(getAllWorkSubTasks(cli.user));
//		worked = Arrays.asList(getIssue(cli.getJiraRestClient(), "ISUUUE____ID"));
//		ERR.isLength(worked, 1);
//		Issue myWork = worked.get(0);
//		SubtaskControlEE.stop(cli, myWork);
//		U.exit(myWork.getStatus());
//		List<Issue> tasks = toList(cli.getAllTasks());
//		List<Issue> subtasks = tasks.stream().filter(t -> t.getIssueType().isSubtask()).collect(Collectors.toList());
//		subtasks = subtasks.stream().filter(t -> t.getStatus().getId() == 3L).collect(Collectors.toList());
//		U.exit(subtasks);


	}


	@SneakyThrows
	public static JiraUser createUsr_DefaultORG(String usrName) {
		String org = APP.getAppOrg();
		EnvTlp envTlp = EnvTlp.ofHlpOrg(org, usrName);
		return JiraUser.create(envTlp.readHost(), new String[]{envTlp.readLogin(), envTlp.readPass()});
	}

	@SneakyThrows
	public static JiraCli initMode0_AP() {
		String user = AP.get("jira.user", null);
		if (user != null) {
			return JiraCli.getOrCreateByUsr(user);
		}
		String jiraurl = AP.get(EPK_JIRA_URL, null);
		String login = AP.get(EPK_JIRA_LOGIN, null);
		String pass = AP.get(EPK_JIRA_PASS, null);
		if (X.emptyAnyStr(jiraurl, login)) {
			throw new FIllegalStateException("set all keys to app.props 'jira.url' + 'jira.login' + 'jira.pass'");
		}
		return JiraCli.getOrCreate(IT.isUrl0(jiraurl).toString(), new String[]{login, pass});
	}


	@SneakyThrows
	public static JiraCli ofAuth(Object auth) {
		auth = auth == null ? new String[0] : (auth instanceof String ? new String[]{(String) auth} : auth);
		JiraCli rsp;
		if (auth instanceof String) {
			rsp = JiraCli.getOrCreateByUsr((String) auth);
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
					rsp = JiraCli.getOrCreateByUsr(userName);
					break;
				default:
					throw new WhatIsTypeException(mode);
			}
		} else if (String[].class.isAssignableFrom(auth.getClass())) {
			rsp = JiraUser.createByHlp((String[]) auth).createClient();
		} else {
			throw new WhatIsTypeException(auth.getClass() + "-->" + auth);
		}
		return rsp;
	}

	public static List<String> downloadFilesFromComment(String usrOrgAlias, String urlWithComment, String dir, boolean skipExisted) throws IOException, ExecutionException, InterruptedException, SubtaskControlEE {
		JiraUser user = createUsr_DefaultORG(usrOrgAlias);
		JiraCli myJiraClient = getOrCreateByUsr(user);
		Pare<String, Integer> stringIntegerPare = Issue0.parseIssueIdAdnCommentId(urlWithComment);
		Issue issue = getIssue(myJiraClient.getJiraRestClient(), stringIntegerPare.key());
		return downloadAttachs(user, issue, stringIntegerPare.val(), Paths.get(dir), skipExisted);
	}


	@Getter
	private JiraUser user;
	private JiraRestClient restClient;
//	private static Map<String, JiraCli> store = (Map<String, JiraCli>) QUEUE.cache_map_FILO(10);

	private JiraCli(JiraUser user) {
		this.user = user;
	}

	public static Issue getWorkSubTask(String usr) throws IOException, ExecutionException, InterruptedException {
		return getWorkSubTask(createUsr_DefaultORG(usr));
	}

	public enum HLP {
		l, p, h, z;

		public static String[] getHLP4(SeqOptions args) {
			return Arrays.stream(values()).map(String::valueOf).toArray(String[]::new);
		}

		public static String[] getHLP_withNulls(SeqOptions args) {
			String[] hlp4 = new String[4];
			for (HLP hlp_ : values()) {
				String single = args.getSingle(hlp_.name(), null);
				if (single == null) {
					return Arrays.stream(hlp4).filter(X::NN).toArray(String[]::new);
				}
				hlp4[ENUM.indexOf(hlp_)] = single;
			}
			return hlp4;
		}
	}

	public interface IStandartTransitionId {
		long id();

		String nameru();

		static Issue getTaskById(long id, Iterable<Issue> tasks) {
			for (Issue issue : tasks) {
				if (isSameIssueId(id, issue)) {
					return issue;
				}
			}
			return null;
		}

		static Transition getTransitionById(long id, Iterable<Transition> transitions) {
			for (Transition transition : transitions) {
				if (isSameTransitionId(id, transition)) {
					return transition;
				}
			}
			return null;
		}

		static boolean isSameIssueId(long id, Issue issue) {
			return Objects.equals(id, issue.getStatus().getId());
		}

		static boolean isSameTransitionId(long id, Transition transition) {
			return Objects.equals(id, transition.getId());
		}
	}

	enum OrgSubtaskStandartTransitionId implements IStandartTransitionId {
		PAUSE(711, "Pause"), INWORK(3, "В работе"), CLOSE(2, "Close");

		final long id;
		final String nameru;

		OrgSubtaskStandartTransitionId(long id, String nameru) {
			this.id = id;
			this.nameru = nameru;
		}


		@Override
		public long id() {
			return id;
		}

		@Override
		public String nameru() {
			return nameru;
		}
	}

	public static Iterable<Transition> getAllTransitionOfIssue(JiraCli jiraCli, Issue issue) {
		return jiraCli.getJiraRestClient().getIssueClient().getTransitions(issue).claim();
	}

	public static class SubtaskControlEE extends EException {

		//		public static final int PAUSE_STATUS = 10009;
		public static final int PAUSE_TRANSITION = 711;

		//		public static final int START_STATUS = 3;
		public static final int START_TRANSITION = 4;

		//		public static final int CLOSE_STATUS = 6;
		public static final int CLOSE_TRANSITION = 2;

		//		public static final int OPEN_STATUS = 3;
		public static final int OPEN_REOPEN_TRANSITION = 3;

		public static void runPauseTransition(JiraCli cli, Issue issue) throws SubtaskControlEE {
			runTransition(cli, issue, PAUSE_TRANSITION);
		}

		public static void runStartTransition(JiraCli cli, Issue issue) throws SubtaskControlEE {
			runTransition(cli, issue, START_TRANSITION);
		}

		public static void runCloseTransition(JiraCli cli, Issue issue) throws SubtaskControlEE {
			runTransition(cli, issue, CLOSE_TRANSITION);
		}

		public static void runReopenTransition(JiraCli cli, Issue issue) throws SubtaskControlEE {
			runTransition(cli, issue, OPEN_REOPEN_TRANSITION);
		}

		public static void runTransition(JiraCli cli, Issue issue, int transitionId) throws SubtaskControlEE {
//			final long staus = issue.getStatus().getId();
//			if (allowedStatuses.length == 0) {
//				throw EErrors.ALLOWED_STATUS_IS_EMPTY.I();
//			} else if (!Arrays.asList(allowedStatuses).contains(staus)) {
//				throw EErrors.STATUS_IS_NOT_IN_ALLOWED_STATUSES.I("Issue [%s] with status[%s] is not in allowed [%s]", issue.getKey(), staus, Arrays.asList(allowedStatuses));
//			}
			List<Transition> ts = ARR.toList(getAllTransitionOfIssue(cli, issue));
			if (!isInTransition(transitionId, ts)) {
				throw EErrors.TRANSITION_IS_NOT_IN_ALLOWED.I("Issue [%s] with status[%s] is not in allowed [%s]", issue.getKey(), issue.getStatus(), ToString.toNiceString(ts));
			}
			TransitionInput transitionInput = new TransitionInput(transitionId);
			cli.getJiraRestClient().getIssueClient().transition(issue, transitionInput).claim();
		}

		private static boolean isInTransition(int transitionId, List<Transition> transitions) {
			return transitions.stream().anyMatch(t -> t.getId() == transitionId);
		}

//
//		public static void start(JiraCli cli, Issue issue) throws SubtaskControlEE {
//			final long id = issue.getStatus().getId();
//			switch ((int) id) {
//				case PAUSE_STATUS: {
//					TransitionInput transitionInput = new TransitionInput(START_TRANSITION);
//					cli.getJiraRestClient().getIssueClient().transition(issue, transitionInput).claim();
//					break;
//				}
//				case CLOSE_STATUS:
//					throw EErrors.ISSUE_IS_CLOSED.I();
//
//				case START_STATUS:
//					throw EErrors.ALREADY_IN_START.I();
//				default:
//					throw new WhatIsTypeException("What is status ID? " + issue);
//			}
//		}
//
//		public static void stop(JiraCli cli, Issue issue) throws SubtaskControlEE {
//			final long id = issue.getStatus().getId();
//			switch ((int) id) {
//				case PAUSE_STATUS: {
//					throw EErrors.ALREADY_ON_PAUSE.I();
//				}
//				case CLOSE_STATUS:
//					throw EErrors.ISSUE_IS_CLOSED.I();
//
//				case START_STATUS:
//					TransitionInput transitionInput = new TransitionInput(PAUSE_TRANSITION);
//					cli.getJiraRestClient().getIssueClient().transition(issue, transitionInput).claim();
//					break;
//				default:
//					throw new WhatIsTypeException("What is status ID? " + issue);
//			}
//		}

		public enum EErrors {
			NOSTATUS, TRANSITION_IS_NOT_IN_ALLOWED;

			public SubtaskControlEE I() {
				return new SubtaskControlEE(this);
			}

			public SubtaskControlEE I(Throwable ex) {
				SubtaskControlEE er = new SubtaskControlEE(this, ex);
				return er;
			}

			public SubtaskControlEE I(String message) {
				SubtaskControlEE er = new SubtaskControlEE(this, new RuntimeException(message));
				return er;
			}

			public SubtaskControlEE I(String message, Object... args) {
				SubtaskControlEE er = new SubtaskControlEE(this, new RuntimeException(X.f(message, args)));
				return er;
			}
		}

		public SubtaskControlEE(EErrors error) {
			super(error);
		}

		public SubtaskControlEE(EErrors error, Throwable cause) {
			super(error, cause);
		}
	}

	static class Issue0 {
//		Issue issue;
//		Integer commnetId;
//
//		JiraCli myJiraClient;
//
//		public static Issue0 ofUrl(String url) {
//		}

		public static Pare<String, Integer> parseIssueIdAdnCommentId(String url) {
			Url0 urlInfo = Url0.ofQk(url);
			return Pare.of(urlInfo.pagenameLast(), urlInfo.queryUrl().getFirstAs("focusedCommentId", Integer.class, null));

		}

	}

	//https://developer.atlassian.com/cloud/jira/platform/basic-auth-for-rest-apis/
	@SneakyThrows
	private static List<String> downloadAttachs(JiraUser user, Issue issue, long commentId, Path toDir, boolean skipExisted) {

		Comment comment = STREAM.findFirst(ARR.toList(issue.getComments()), c -> c.getId().equals(commentId));

		Path toDirComment = toDir.resolve(commentId + "");

		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(toDirComment, true);

		List<Attachment> attachments = ARR.toList(issue.getAttachments());

		List<String> markAttahs = pickAttach(comment.getBody());

		List<Attachment> commentAttachs = STREAM.filterToAll(attachments, (Attachment i) -> markAttahs.contains(i.getFilename()));
		IT.state(markAttahs.size() == commentAttachs.size());
		if (L.isInfoEnabled()) {
			L.info("Found attach '{}' files for comment '{}'", X.sizeOf(commentAttachs), commentId);
		}
		String[][] headers = CON.HEADERS(user.getAuthBasic());

		List<String> downlaoded = new ArrayList<>();
		for (Attachment attachment : commentAttachs) {
			if (L.isInfoEnabled()) {
				L.info("Start download file '{}' by uri '{}' to '{}'", attachment.getContentUri().toURL(), attachment.getFilename(), toDirComment);
			}

			String file = toDirComment.resolve(attachment.getFilename()).toString();
			if (skipExisted && UFS.existFile(file)) {
				if (L.isInfoEnabled()) {
					L.info("SKIP download file '{}' by uri '{}' to '{}'", attachment.getContentUri().toURL(), attachment.getFilename(), toDirComment);
				}
				continue;
			}
			DLD.url2file0(attachment.getContentUri().toURL(), file, headers);
			downlaoded.add(file);
		}
		return downlaoded;
	}


	private static List<String> pickAttach(String commentContent) {
		List<String> all = STREAM.mapToAll(URx.findAllGroup(commentContent, "(\\[\\^.+?\\])"), (String s) -> STR.substrCount(s, 2, 1));
		return all;
	}

	enum IssueTypeId {
		TASK(3), SUBTASK(5);
		Long status;

		IssueTypeId(long status) {
			this.status = status;
		}

		boolean is(Issue issue) {
			return status.equals(issue.getIssueType().getId());
		}
	}

	enum IssyeStatusTypeId {
		OPEN(3), PAUSE(10009), CLOSE(6);
		Long status;

		IssyeStatusTypeId(long status) {
			this.status = status;
		}

		boolean is(Issue issue) {
			return status.equals(issue.getStatus().getId());
		}

	}

	public JiraRestClient getJiraRestClient() {
		if (this.restClient != null) {
			return this.restClient;
		}
		if (L.isInfoEnabled()) {
			L.info("JiraRestClient to {} with user {}", user.getUrl2jira(), user.getLogin());
		}
		return this.restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(user.getUrl2jira()), user.getLogin(), user.getPass());
	}


	@RequiredArgsConstructor
	@Getter
	public static class JiraUser {
		private final String url2jira;
		private final String login;
		private final String pass;

		public static JiraUser create(String urlToJira, String[] loginPass) {
			IT.isLength(loginPass, 2, "For create Jira Client need 3 arguments (login, password, url2jira).");
			return new JiraUser(urlToJira, loginPass[0], loginPass[1]);
		}

		public static JiraUser createBy(SeqOptions opts) {
			if (opts.hasDouble("elp", false)) {
				return JiraUser.create(Sys.PKS.getValueFirst(EPK_JIRA_URL).val(), ARR.of(Sys.PKS.getValueFirst(EPK_JIRA_LOGIN).val(), Sys.PKS.getValueFirst(EPK_JIRA_PASS).val()));
			}
			Path tlp = opts.getSingleAs("tlp", Path.class, null);
			String[] hlp;
			if (tlp != null) {
				hlp = EnvTlp.ofFile(tlp).readAsHLP4_loginRq();
			} else {
				hlp = HLP.getHLP_withNulls(opts);
			}
			return createByHlp(hlp);
		}

		public static JiraUser createByHlp(String[] hlp) {
			switch (hlp.length) {
				case 0:
				case 1:
					String usr;
					if (hlp.length == 0) {
						usr = Env.getUserName();
						if (L.isInfoEnabled()) {
							L.info("Use default os user name '{}' for env app 'jira'", usr);
						}
					} else {
						usr = hlp[1];
						if (L.isInfoEnabled()) {
							L.info("Use in user name '%s' for env app 'jira'", usr);
						}
					}
					EnvTlp enTlp = EnvTlp.ofSysAcc("jira", usr);
					return JiraUser.create(enTlp.readHostWithPort(), ARR.of(enTlp.readLogin(), enTlp.readPass()));
				case 2:
					return JiraUser.create(Sys.PKS.getValueFirst(EPK_JIRA_URL).val(), ARR.of(hlp[0], hlp[1]));
				case 3:
					return JiraUser.create(hlp[2], ARR.of(hlp[0], hlp[1]));
				case 4:
					return JiraUser.create(hlp[2] + ":" + IT.isInt0(hlp[3]), ARR.of(hlp[0], hlp[1]));
				default:
					throw new IllegalArgumentException("Support only 2(login/pass) or 3|4(with url2jira|port) arguments");
			}
		}

		@Override
		public String toString() {
			return "JiraUser{" + "login='" + login + '\'' + ", pass='" + "*****" + '\'' + ", url2jira='" + url2jira + '\'' + '}';
		}

		public String[] getAuthBasic() {
			return CON.HEADER_AUTH_BASIC(new String[]{getLogin(), getPass()});
		}

		public JiraCli createClient() {
			JiraCli myJiraClient = getOrCreateByUsr(this);
			return myJiraClient;
		}
	}

	public static JiraCli getOrCreate(String jiraUrl, String[] userLP) {
		JiraUser cli = JiraUser.create(jiraUrl, new String[]{userLP[0], userLP[1]});
		JiraCli myJiraClient = JiraCli.getOrCreateByUsr(cli);
		return myJiraClient;
	}

	//
	//

	public static JiraCli getOrCreateByUsr(String usrName) throws IOException {
		return getOrCreateByUsr(createUsr_DefaultORG(usrName));
	}

	private static JiraCli getOrCreateByUsr(JiraUser user) {
//		JiraCli client = store.get(user.getLogin());
//		if (client != null) {
//			return client;
//		}
		JiraCli client = new JiraCli(user);
//		store.put(user.getLogin(), client);
		return client;
	}


	/**
	 * *************************************************************
	 * ---------------------------- GET ISSUE ----------------------
	 * *************************************************************
	 */

	public static Issue getIssue(String jiraUrl, String[] userLP, String issueKey) {
		JiraUser usr = JiraUser.create(jiraUrl, new String[]{userLP[0], userLP[1]});
		JiraCli myJiraClient = JiraCli.getOrCreateByUsr(usr);
		Issue task = myJiraClient.getIssue(issueKey);
		return task;
	}

	/**
	 * *************************************************************
	 * ------------------------- CREATE ISSUE ----------------------
	 * *************************************************************
	 */

	public static String createIssue(String jiraUrl, String[] userLP, String projectCodeName, long taskTypeId, String taskTitle, String taskDescription) {
		JiraUser usr = JiraUser.create(jiraUrl, new String[]{userLP[0], userLP[1]});
		JiraCli myJiraClient = JiraCli.getOrCreateByUsr(usr);
		BasicIssue task = myJiraClient.createIssue(projectCodeName, taskTypeId, taskTitle);
		String issueKey = task.getKey();
		myJiraClient.updateIssueDescription(issueKey, taskDescription);
		return issueKey;
	}

	public BasicIssue createIssue(String projectKey, Long issueType, String issueSummary) {
		return createIssue(projectKey, issueType, issueSummary, null, null);
	}

	public BasicIssue createIssue(String projectKey, Long issueType, String issueSummary, String assignee, String desc) {
		IssueRestClient issueClient = getJiraRestClient().getIssueClient();
		IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey, issueType, issueSummary);
		issueInputBuilder.setAssigneeName(assignee);

		issueInputBuilder.setDescription(desc);

		IssueInput newIssue = issueInputBuilder.build();
//		newIssue.getFields().set
//		newIssue.
		if (L.isInfoEnabled()) {
			L.info("Run project issue create PK={} ISSUE={} SUMMARY={} - {}", projectKey, issueType, issueSummary, newIssue);
		}
		return issueClient.createIssue(newIssue).claim();
	}

	/**
	 * *************************************************************
	 * --------------------------- ADD LABEL -----------------------
	 * *************************************************************
	 */

	public void addLabel(Issue issue, String label) {
		addLabel(getJiraRestClient(), label);
	}

	public static void addLabel(String jiraUrl, String[] userLP, String issueKey, String... label) {
		JiraCli cli = JiraCli.getOrCreate(jiraUrl, userLP);
		addLabel(cli.getJiraRestClient(), issueKey, label);
	}


//	public static void addLabel(JiraRestClient jiraRestClient, String taskKey, String label) {
//		Issue issue = getIssue(jiraRestClient, taskKey);
//		Set existLabels = issue.getLabels();
//		List existLableList = Arrays.asList(existLabels);
//		if (existLableList.contains(label)) {
//			return;
//		}
//		IssueInputBuilder ib = new IssueInputBuilder();
//		existLabels.addAll(label);
//		ib.setFieldValue(IssueFieldId.LABELS_FIELD.id, existLabels);
//		jiraRestClient.getIssueClient().updateIssue(issue.getKey(), ib.build()).claim();
//	}

	public static void addLabel(JiraRestClient jiraRestClient, String issueKey, String... labels) {
		IT.notEmpty(labels, "labels is empty");
		Issue issue = getIssue(jiraRestClient, issueKey);
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
		jiraRestClient.getIssueClient().updateIssue(issue.getKey(), ib.build()).claim();
	}

	public static Issue changeAssignee(String jiraUrl, String[] userLP, String issueKey, String assignee) {
		return changeAssignee(JiraCli.getOrCreate(jiraUrl, userLP).getJiraRestClient(), issueKey, assignee);
	}

	public static Issue changeAssignee(JiraRestClient jiraRestClient, String issueKey, String assignee) {
		IT.notEmpty(issueKey, "issueKey is empty");
		IT.notEmpty(assignee, "assignee is empty");
		Issue issue = getIssue(jiraRestClient, issueKey);
		IssueInputBuilder ib = new IssueInputBuilder();
		ib.setAssigneeName(assignee);
		jiraRestClient.getIssueClient().updateIssue(issue.getKey(), ib.build()).claim();
		return issue;
	}

	/**
	 * *************************************************************
	 * ------------------------- GET TASKS -------------------------
	 * *************************************************************
	 */

	public Iterable<Issue> getAllDefaultTasksByProjects(List<String> projects) throws ExecutionException, InterruptedException {
		return getAllDefaultTasksByProjects(user, projects);
	}

	//	public static Iterable<Issue> getAllTasks0(JiraUser user) throws ExecutionException, InterruptedException {
//		String project = "project in (EXP, SUP, TSE, BU, ARP, ESD, ACT, INC, EB)";
//		String status = "status in (Open, \"In Progress\", Resolved, Design, \"In Development\", \"In Build\", Tested, Analysis, Testing, Formed, \"Information Required\", Realization, Correction, \"Запрос информации\", Delay, \"В ожидании\", Пауза, CodeReview, \"Ready in build\", Blocked)";
//		String issuetype = "issuetype in (Improvement, Task, \"Задача ДТА\", \"Request management\", \"Change request\", Request, \"Component Improvement\", Bug, Patch)";
//		return getAllTasks(user, "assignee=currentuser() AND status in (Open, \"In Progress\") AND " + status + " AND " + project + " AND " + issuetype);
//	}
	public static Iterable<Issue> getAllDefaultTasksByProjects(JiraUser user, List<String> projects) throws ExecutionException, InterruptedException {
		String project = "project in (" + IT.NE(JOIN.allByComma(projects), "set projects") + ")";
		String status = "status in (Open, \"In Progress\", Resolved, Design, \"In Development\", \"In Build\", Tested, Analysis, Testing, Formed, \"Information Required\", Realization, Correction, \"Запрос информации\", Delay, \"В ожидании\", Пауза, CodeReview, \"Ready in build\", Blocked)";
		String issuetype = "issuetype in (Improvement, Task, \"Задача ДТА\", \"Request management\", \"Change request\", Request, \"Component Improvement\", Bug, Patch)";
		return getAllTasks(user, project, issuetype, status);
	}

	public static Iterable<Issue> getAllTasks(JiraUser user, String project, String issuetype, String status) throws ExecutionException, InterruptedException {
		return getAllTasksByJql(user, "assignee=currentuser() AND " + status + " AND " + project + " AND " + issuetype);
	}

	public static Issue getWorkSubTask(JiraUser user) throws ExecutionException, InterruptedException {
		return getAllWorkSubTasks(user).iterator().next();
	}

	public static Iterable<Issue> getAllWorkSubTasks(JiraUser user) throws ExecutionException, InterruptedException {
		return getAllTasksByJql(user, "assignee=currentuser() AND issuetype = Sub-task AND Status=3");
	}

	public static Iterable<Issue> getAllTasksByJql(JiraUser user, String jql) throws ExecutionException, InterruptedException {
		JiraCli myJiraClient = getOrCreateByUsr(user);
		Promise<SearchResult> filters = myJiraClient.getJiraRestClient().getSearchClient().searchJql(jql);
		SearchResult result = filters.get();
		if (result.getTotal() == 0) {
			return Collections.EMPTY_LIST;
		} else {
			return result.getIssues();
		}
	}

	/**
	 * *************************************************************
	 * -------------------------- EXAMPLES -------------------------
	 * *************************************************************
	 */


	public Issue getIssue(String issueKey) {
		return getIssue(getJiraRestClient(), issueKey);
	}

	public static Issue getIssue(JiraRestClient jiraRestClient, String issueKey) {
		return jiraRestClient.getIssueClient().getIssue(issueKey).claim();
	}

	public void voteForAnIssue(Issue issue) {
		getJiraRestClient().getIssueClient().vote(issue.getVotesUri()).claim();
	}

	public int getTotalVotesCount(String issueKey) {
		BasicVotes votes = getIssue(issueKey).getVotes();
		return votes == null ? 0 : votes.getVotes();
	}

	public void addComment(Issue issue, String commentBody) {
		getJiraRestClient().getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
	}


	public List<Comment> getAllComments(String issueKey) {
		return StreamSupport.stream(getIssue(issueKey).getComments().spliterator(), false).collect(Collectors.toList());
	}

	public void updateIssueDescription(String issueKey, String newDescription) {
		IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
		getJiraRestClient().getIssueClient().updateIssue(issueKey, input).claim();
	}

	public void deleteIssue(String issueKey, boolean deleteSubtasks) {
		getJiraRestClient().getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
	}


}
