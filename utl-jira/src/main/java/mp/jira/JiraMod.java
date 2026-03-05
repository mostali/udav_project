package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mp.jira.api.ApiJql;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpc.types.opts.SeqOptions;
import mpc.types.tks.FIDT;
import mpc.types.tks.SO1;
import mpc.url.UUrl;
import mpe.cmsg.std.JqlCallMsg;
import mpf.zcall.ZJar;
import mpf.zcall.ZType;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.SPLIT;
import mpu.str.Sb;
import mpu.str.TKN;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ZType.ZTypeAno(app = "jira", version = "2")
public class JiraMod {

	public static final Logger L = LoggerFactory.getLogger(JiraMod.class);

	public static final String MK_TASK = "task";
	public static final String MK_PROJECTS = "projects";
	public static final String MK_ALL = "all";
	public static final String MK_CREATE_PROJECT = "create.task.project";
	public static final String MK_CREATE_TASKTYPE = "create.task.type";
	public static final String MK_CREATE_TASKSUMMARY = "create.task.summary";
//	public static final String DEF_EB_PROJECTS = "EXP,SUP,TSE,BU,ARP,ESD,ACT,INC,EB,NSI,REF,BSK";

//	public static void main0(String[] args) {
//		String[] packages = {"mp.jira"};
//		ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), packages);
//
//				List<ZJar> zJars = ZJar.ls();
//		Object taskOne = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "SUP-1495556"});
//		X.exit(taskOne);
//
//		//		Object taskAll = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "*", "-projects", DEF_EB_PROJECTS});
//		//		X.exit(taskAll);
//
//		Object taskCreate = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "create", "-task.type", "3", "-task.project", "SUP", "-task.summary", "summarryss", "-task.desc", "descc", "-task.assignee", "ditts.aleksandr"});
//		X.exit(taskCreate);
//	}

	public static void main(String[] args) throws IOException {
		String jqlMsg = "jql:\n" + //
				"--auth.usr:dav\n" + //
				"--app.org:otr\n" + //
				"--task.project:SUP\n" +//
				"--task.type:3\n" + //
				"--task.desc:test\n" + //
				"--task.assignee:ditts.aleksandr\n" + //
				"--task.summary:summarysummary\n" + //
				"--wth\n" + //
				"\n";

		X.exit(invokeMsg(jqlMsg)+"");
		;

		ApiJql dav = JiraCli.buildCli_ByUsr("dav")._Jql();

		Iterable<Issue> allTasksByJql = dav.getAllTasksByJql("project in (NSI, BSK) AND issuetype = Sub-task AND status in (\"In Progress\", Paused) AND assignee in (currentUser())");
//		X.nothing();
		int i = X.sizeOf(allTasksByJql);
		X.p("Count:" + i);
		X.exit(i);


		String[] packages = {JiraMod.class.getPackage().getName()};
		ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), packages);

//		List<ZJar> zJars = ZJar.ls();
		Object taskOne = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "SUP-1495556"});
		X.exit(taskOne);

//		Object taskAll = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "*", "-projects", DEF_EB_PROJECTS});
//		X.exit(taskAll);

		Object taskCreate = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "create", "-task.type", "3", "-task.project", "SUP", "-task.summary", "summarryss", "-task.desc", "descc", "-task.assignee", "ditts.aleksandr"});
		GsonMap gm = GsonMap.ofObj(taskCreate);
		Sys.open_Chrome("https://ias-tst-job-jira.otr.ru/browse/" + gm.get("key"));

		X.exit();
		//
		//
		Map<ZType, List<ZType.ZMethod>> mapZTypes = zJar.getMapZTypes();
		X.exit(mapZTypes);
		List<ZType> allZTypes = zJar.getAllZTypes();
		X.exit(allZTypes);
//		List<ZType> all = ZJar.findAll(Paths.get("/opt/appVol/.bin/jira-mod.jar"), "mp.jira");
//		X.exit(all);
//		Function<Object, Object> getAll = (name) -> {
//		Supplier getAllEnv = () -> {
//			List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
//			ZType zType = new ZType(allPackageClassViaClassgraph.get(0));
//			List<ZType.ZMethod> allCalls = zType.getAllZMethods();
//			System.setProperty("task", "*");
//			Optional<ZType.ZMethod> first = allCalls.stream().filter(i -> "invokeLinesEnv".equals(i.name())).findFirst();
////		X.exit(calls.get(0).call(new String[]{"-task", "SUP-1495556"}));
////			ZType.ZMethod zMethod = allCalls.get(0);
////			return first.get().invokeWithArgs(new String[]{"-task", "*"});
//			return first.get().invokeWithArgs0();
//		};
//		Supplier getAll = () -> {
//			List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
//			ZType zType = new ZType(allPackageClassViaClassgraph.get(0));
//			ZType.ZMethod zMethod = zType.getZMethod_ByName_FirstAny("invokeLines");
//			return zMethod.invokeWithArgs1(new String[]{"-task", "*", "-projects", DEF_EB_PROJECTS});
////			return first.get().invokeWithArgs();
//		};
//		X.exit(getAll.get());
//		EnvTlp envTlp = EnvTlp.ofHlpOrg("", "");
//		String[] auth = envTlp.readAsHLP3();
//		auth[2] = "http://asd.asd";
//		X.exit(invokeContext0(null, MAP.of("task", "SUP-1495556")));
//		X.exit(invokeContext0(null, MAP.of("task", "all")));
		Object objs = invokeLines(new String[]{"-task", "SUP-1495556"});
//		Object objs = invokeLines(new String[]{"-task", "NSI-1050"});
//		Object objs = invokeLines(new String[]{"-task", "create", "task.type", "3", "-task.project", "SUP", "-task.summary", "summarryss", "-task.assignee", "ditts.aleksandr"});
//		Object objs = invokeLines(args);
		X.p("Founded:" + objs);
		X.p("" + objs);
	}

//	@SneakyThrows
//	public static <T> T get_task_args(String[] args) {
//		return invokeLines(args);
//	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_task(@ZType.ZArgAno("task") String task) {
		String[] objs = ARR.of("-task", IT.NE(task, "set task key"));
		return invokeLines(objs);
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_all_task_by_projects(@ZType.ZArgAno("projects") String projects) {
		return invokeLines(ARR.of("-task", "*", SO1.wrap(MK_PROJECTS), IT.NE(projects, "set projects")));
	}

//	@ZType.ZMethodAno
//	@SneakyThrows
//	public static <T> T get_all_task_default_eb() {
//		return invokeLines(ARR.of("-task", "*", SO1.wrap(MK_PROJECTS), DEF_EB_PROJECTS));
//	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T create_task(
			@ZType.ZArgAno("task.type") String type, //
			@ZType.ZArgAno("task.project") String project, //
			@ZType.ZArgAno("task.summary") String summary, //
			@ZType.ZArgAno("task.desc") String desc, //
			@ZType.ZArgAno("task.assignee") String assignee //
	) {
		return invokeLines(ARR.of("-task", "create", "-task.project", project, "-task.type", type, "-task.summary", summary, "-task.desc", desc, "-task.assignee", assignee));
	}


	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T invokeLines(String... args) {

		SeqOptions seqOpts = SeqOptions.of(args);

		String task = seqOpts.getSingle(MK_TASK, null);
		if (task == null) {
			throw new RequiredRuntimeException("Set ket task with allowed options [-task *] || [-task all] || || [-task TASK_ID]");
		}

		JiraCliCreator creator = new JiraCliCreator(args, null);

		JiraCli client = creator.buildByOpts();

		switch (task) {
			case "*":
				String projects = seqOpts.getSingle(MK_PROJECTS);
				Iterable<Issue> allTasks = client._Jql().getAllDefaultTasksByProjects(SPLIT.allByComma(projects));
				L.info("InvokeLines:All\n" + allTasks);
				return (T) allTasks;
			case "create":
				String projectKey = seqOpts.getSingle("task.project");
				Long taskType = seqOpts.getSingleAs("task.type", Long.class);//, 3L
				String taskSummary = seqOpts.getSingleOrFile("task.summary");
				String taskDesc = seqOpts.getSingleOrFile("task.desc");
				String taskAssignee = seqOpts.getSingle("task.assignee");
				L.info("InvokeLines:Create:{}({}) - {}", projectKey, taskType, taskSummary);
				BasicIssue issue = client._Com().createIssue(projectKey, taskType, taskSummary, taskAssignee, taskDesc);
				return (T) issue;
			default:
				IT.state(UST.INT(TKN.two(task, "-")[1], null) != null, "except issue key ( not '%s' ), e.g. PROJECT-123", task);
//				throw new FUnsupportedOperationException("What is task %s?", task);
		}
		Issue issue = client._Com().getIssue(task);
		L.info("InvokeLines:Issue:{}\n{}", task, issue);
		return Issue0.of(issue).toStringJsonObj();
//		return (T) GsonMap.toMapFromObj(issue);
	}


	//	@RequiredArgsConstructor
	static interface CreateContract {
		static boolean isValid(mp.jira.JiraMod.CreateContract createContract) {
			Long taskType = createContract.getTaskType(null);
			String projectKey = createContract.getTaskProject(null);
			String taskSummary = createContract.getTaskSummary(null);
			String taskDesc = createContract.getTaskDesc(null);
			String taskAssignee = createContract.getTaskAssignee(null);
			return X.notEmptyAllObj_Str_Cll_Num(taskType, projectKey, taskSummary, taskDesc, taskAssignee);
		}

		//		final JqlCallMsg callMsg;
		String getTaskProject(String... defRq);

		Long getTaskType(Long... defRq);

		String getTaskSummary(String... defRq);

		String getTaskDesc(String... defRq);

		String getTaskAssignee(String... defRq);

		public static CreateContract fromDirtyMap(Map<String, String> props) {
			return new CreateContract() {
				@Override
				public String getTaskProject(String... defRq) {
					return ARG.throwNE(props.get("task.project"), "task.project", defRq);
				}

				@Override
				public Long getTaskType(Long... defRq) {
					return ARG.throwNN(UST.LONG(props.get("task.type"), null), "task.type", defRq);
				}

				@Override
				public String getTaskSummary(String... defRq) {
					return ARG.throwNE(props.get("task.summary"), "task.summary", defRq);
				}

				@Override
				public String getTaskDesc(String... defRq) {
					return ARG.throwNE(props.get("task.desc"), "task.desc", defRq);
				}

				@Override
				public String getTaskAssignee(String... defRq) {
					return ARG.throwNE(props.get("task.assignee"), "task.assignee", defRq);
				}


			};
		}
	}
	//
	//

	@RequiredArgsConstructor
	public static class InvokerMsg {
		public final JqlCallMsg callMsg;

		//
		//

		public JiraCli jc() {
			JiraCliCreator creator = new JiraCliCreator(callMsg);
			JiraCli jc = creator.buildByMsg();
			return jc;
		}

		//
		//

		public boolean hasUrlInKey() {
			String url = callMsg.getKeyAsTaskUrl(null);
			return url != null;
		}

		public <T> T invokeMsg_OneTask() {
			String url = callMsg.getKeyAsTaskUrl();
			FIDT issueId = TaskId.ofUrl(url);
			L.info("invokeMsg [...] by OneTask: " + " , issueId:" + issueId + ", callMsg : " + callMsg);
			Issue issue = jc()._Com().getIssue(issueId.toString());
			Issue0 issue0 = Issue0.of(issue);
			L.info("invokeMsg by OneTask:\n" + issue0);
			return issue0.toStringJsonObj();

		}

		//
		//

		public boolean hasJqlInKey() {
			return X.notBlank(callMsg.getKeyAsJql(null));
		}

		public <T> T invokeMsg_asJql() {
			String jql = callMsg.getKeyAsJql();
			Iterable<Issue0> allTasks = jc()._Jql().getAllTasksByJql(IT.NE(jql, "set jql, obj " + callMsg.toObjMsgId(null)), Issue0.class);
			L.info("invokeMsg by JQL:\n" + allTasks);
			return (T) allTasks;
		}


		public boolean hasCreateConract() {
			CreateContract createContract = CreateContract.fromDirtyMap(callMsg.getHeaders_MAP());
			return CreateContract.isValid(createContract);
		}

		public <T> T invokeMsg_createContract() {
			CreateContract createContract = CreateContract.fromDirtyMap(callMsg.getHeaders_MAP());
			Long taskType = createContract.getTaskType();
			String projectKey = createContract.getTaskProject();
			String taskSummary = createContract.getTaskSummary();
			String taskDesc = createContract.getTaskDesc();
			String taskAssignee = createContract.getTaskAssignee();
			L.info("invokeMsg_createContract:{}({}) - {}", projectKey, taskType, taskSummary);
			BasicIssue issue = jc()._Com().createIssue(projectKey, taskType, taskSummary, taskAssignee, taskDesc);
			return (T) issue;

		}
	}

	static class TaskId {
		final String url;

		public static FIDT ofUrl(String url, FIDT... defRq) {
			try {
				TaskId taskId = new TaskId(IT.isUrl(url));
				return taskId.fidt();
			} catch (Exception ex) {
				return ARG.throwMsg(ex, defRq);
			}
		}

		private FIDT fidt() {
			FIDT fidt = FIDT.of(UUrl.getPathLastItemWoQuery(url));
			IT.NE(fidt.first(), "except project key in url : %s", url);
			IT.isLong0(fidt.second(), "except num project in url : %s", url);
			return fidt;
//			String[] two = TKN.two(getTaskKey(), "-");
//			return FIDT.ofArgs(IT.NE(two[0], "except project key in url"), IT.isInt(two[0], "except num project in url"));
		}

		public TaskId(String url) {
			this.url = url;
		}

		public String getTaskKey() {
//			String first = TKN.last(url, "?", url);
//			String last = TKN.last(first, "/");
//			String pathLastItemWoQuery = ;
			return UUrl.getPathLastItemWoQuery(url);
		}

		@Override
		public String toString() {
			return getTaskKey();
		}
	}

	@ZType.ZMethodAno
	public static <T> T invokeMsg(String callMsg) {
		JqlCallMsg jqlCallMsg = JqlCallMsg.of(callMsg);
		InvokerMsg invokerMsg = new InvokerMsg(jqlCallMsg);


		L.info("Check URL with task in key..");
		if (invokerMsg.hasUrlInKey()) {
			return invokerMsg.invokeMsg_OneTask();
		}

		L.info("Check JQL in key..");
		if (invokerMsg.hasJqlInKey()) {
			return invokerMsg.invokeMsg_asJql();
		}

		L.info("Check CreateContract in key..");
		if (invokerMsg.hasCreateConract()) {
			return invokerMsg.invokeMsg_createContract();
		}
		Sb sb = new Sb();
		sb.NL("Set link to task or jql or create");
		throw new FIllegalStateException(sb.toString());

	}

	//
	//

	@SneakyThrows
	public static <T> T invokeContext0(Object auth, Map context) {
		{
			String task = (String) MAP.get(context, MK_TASK, null);
			if (task != null) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				Issue issue = jiraCli._Com().getIssue(task);
				//		return (T) UGson.toStringJsonFromObject(issue);
				return (T) issue;
			}
		}
		{
			String all = (String) MAP.get(context, MK_ALL, null);
			if (all != null) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				String projects = (String) MAP.get(context, MK_PROJECTS);
				Iterable<Issue> issues = jiraCli._Jql().getAllDefaultTasksByProjects(SPLIT.allByComma(projects));
				return (T) ARR.toList(issues);
			}
		}
		{
			String projectName = (String) MAP.get(context, MK_CREATE_PROJECT);
			Long taskType = MAP.getAsLong(context, MK_CREATE_TASKTYPE);
			String taskSummary = MAP.getAsString(context, MK_CREATE_TASKSUMMARY);
			if (X.notNullAll(projectName, taskType, taskSummary)) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				BasicIssue issue = jiraCli._Com().createIssue(projectName, taskType, taskSummary);
				return (T) issue;
			} else if (projectName == null || taskType == null || taskSummary == null) {
				if (X.blank(projectName)) {
					throw new RequiredRuntimeException("set projectName");
				} else if (taskType == null) {
					throw new RequiredRuntimeException("set taskType");
				} else if (X.blank(taskSummary)) {
					throw new RequiredRuntimeException("set taskSummary");
				}
			}
		}
		//
		//

		L.info("Invoke Context not found:" + context);
		throw new FIllegalStateException("Invoke Context not found");

	}

	//
	//

}
