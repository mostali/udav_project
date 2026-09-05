package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mp.jira.api.ApiAttach;
import mp.jira.api.ApiJql;
import mp.jira.api.Issue0;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpc.types.opts.SeqOptions;
import mpc.types.tks.SO1;
import mpf.zcall.ZJar;
import mpf.zcall.ZType;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.SPLIT;
import mpu.str.TKN;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ZType.ZTypeAno(app = "jira", version = "2")
public class JiraModOld {

	public static final Logger L = LoggerFactory.getLogger(JiraModOld.class);

	public static final String MK_TASK = "task";
	public static final String MK_PROJECTS = "projects";
	public static final String MK_ALL = "all";
	public static final String MK_CREATE_PROJECT = "create.task.project";
	public static final String MK_CREATE_TASKTYPE = "create.task.type";
	public static final String MK_CREATE_TASKSUMMARY = "create.task.summary";

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

//        Issue0 issue0 = Issue0.loadIssue("NSI-3483");
//        X.exit(issue0.getComments());

		JiraCli jiraCli = JiraCli.buildCli_ByUsr("dav");

		Issue0 iss0 = jiraCli.issue("NSI-3483");

		String json = Issue0.toJson(new Issue0.IssueQk(iss0.getIssueOrLoad()));
		X.p(json);
		X.p("-----------");
		String json1 = Issue0.toJson(iss0.getIssueOrLoad());
		X.p(json1);
		X.p("-----------");
		if (true) {
			return;
		}
		;
		ApiAttach attachApi = jiraCli._Attach();

		List<Comment> allComments = attachApi.getAllComments("NSI-3483");
		X.exit(allComments);

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

//		X.exit(invokeMsg(jqlMsg) + "");
		;

		ApiJql dav = JiraCli.buildCli_ByUsr("dav")._Jql();

		Iterable<Issue> allTasksByJql = dav.getAllTasksByJql("project in (NSI, BSK) AND issuetype = Sub-task AND status in (\"In Progress\", Paused) AND assignee in (currentUser())");
//		X.nothing();
		int i = X.sizeOf(allTasksByJql);
		X.p("Count:" + i);
		X.exit(i);


		String[] packages = {JiraModOld.class.getPackage().getName()};
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
		return (T) Issue0.toJson(issue);
//		return Issue00.of(issue).toStringJsonObj();
//		return (T) GsonMap.toMapFromObj(issue);
	}


	//	@RequiredArgsConstructor
	static interface CreateContract {
		static boolean isValid(JiraModOld.CreateContract createContract) {
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
