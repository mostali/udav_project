package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
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
public class JiraMod {


	public static final Logger L = LoggerFactory.getLogger(JiraMod.class);

	public static final String MK_TASK = "task";
	public static final String MK_PROJECTS = "projects";
	public static final String MK_ALL = "all";
	public static final String MK_CREATE_PROJECT = "create.task.project";
	public static final String MK_CREATE_TASKTYPE = "create.task.type";
	public static final String MK_CREATE_TASKSUMMARY = "create.task.summary";
//	public static final String DEF_EB_PROJECTS = "EXP,SUP,TSE,BU,ARP,ESD,ACT,INC,EB,NSI,REF,BSK";

	public static void main0(String[] args) {
		String[] packages = {"mp.jira"};
		ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), packages);

		//		List<ZJar> zJars = ZJar.ls();
		//		Object taskOne = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "SUP-1495556"});
		//		X.exit(taskOne);

		//		Object taskAll = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "*", "-projects", DEF_EB_PROJECTS});
		//		X.exit(taskAll);

		Object taskCreate = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "create", "-task.type", "3", "-task.project", "SUP", "-task.summary", "summarryss", "-task.desc", "descc", "-task.assignee", "ditts.aleksandr"});
		X.exit(taskCreate);
	}

	public static void main(String[] args) throws IOException {
		String[] packages = {JiraMod.class.getPackage().getName()};
		ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), packages);

//		List<ZJar> zJars = ZJar.ls();
//		Object taskOne = zJar.invokeWithArgs1("invokeLines", new String[]{"-task", "SUP-1495556"});
//		X.exit(taskOne);

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

		JiraCli client = JiraCli.JiraUser.createBy(seqOpts).createClient();

		switch (task) {
			case "*":
				String projects = seqOpts.getSingle(MK_PROJECTS);
				Iterable<Issue> allTasks = client.getAllDefaultTasksByProjects(SPLIT.allByComma(projects));
				L.info("InvokeLines:All\n" + allTasks);
				return (T) allTasks;
			case "create":
				String projectKey = seqOpts.getSingle("task.project");
				Long taskType = seqOpts.getSingleAs("task.type", Long.class);//, 3L
				String taskSummary = seqOpts.getSingleOrFile("task.summary");
				String taskDesc = seqOpts.getSingleOrFile("task.desc");
				String taskAssignee = seqOpts.getSingle("task.assignee");
				L.info("InvokeLines:Create:{}({}) - {}", projectKey, taskType, taskSummary);
				BasicIssue issue = client.createIssue(projectKey, taskType, taskSummary, taskAssignee, taskDesc);
				return (T) issue;
			default:
				IT.state(UST.INT(TKN.two(task, "-")[1], null) != null, "except issue key ( not '%s' ), e.g. PROJECT-123", task);
//				throw new FUnsupportedOperationException("What is task %s?", task);
		}

		Issue issue = client.getIssue(task);
		L.info("InvokeLines:Issue:{}\n{}", task, issue);
		return (T) GsonMap.toMapFromObj(issue);
	}


	@SneakyThrows
	public static <T> T invokeContext0(Object auth, Map context) {
		{
			String task = (String) MAP.get(context, MK_TASK, null);
			if (task != null) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				Issue issue = jiraCli.getIssue(task);
				//		return (T) UGson.toStringJsonFromObject(issue);
				return (T) issue;
			}
		}
		{
			String all = (String) MAP.get(context, MK_ALL, null);
			if (all != null) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				String projects = (String) MAP.get(context, MK_PROJECTS);
				Iterable<Issue> issues = jiraCli.getAllDefaultTasksByProjects(SPLIT.allByComma(projects));
				return (T) ARR.toList(issues);
			}
		}
		{
			String projectName = (String) MAP.get(context, MK_CREATE_PROJECT);
			Long taskType = MAP.getAsLong(context, MK_CREATE_TASKTYPE);
			String taskSummary = MAP.getAsString(context, MK_CREATE_TASKSUMMARY);
			if (X.notNullAll(projectName, taskType, taskSummary)) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				BasicIssue issue = jiraCli.createIssue(projectName, taskType, taskSummary);
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
}
