package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.map.MAP;
import mpc.rfl.UReflScanner;
import mpc.types.opts.SeqOptions;
import mpc.types.tks.SOID;
import mpf.zcall.ZJar;
import mpf.zcall.ZType;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.str.SPLIT;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ZType.ZTypeAno
public class JiraMod {

	public static final String MK_TASK = "task";
	public static final String MK_PROJECTS = "projects";
	public static final String MK_ALL = "all";
	public static final String MK_CREATE_PROJECT = "create.task.project";
	public static final String MK_CREATE_TASKTYPE = "create.task.type";
	public static final String MK_CREATE_TASKSUMMARY = "create.task.summary";

	public static void main(String[] args) throws IOException {
		List<ZType> all = ZJar.findAll(Paths.get("/opt/appVol/.bin/jira-mod.jar"), "mp.jira");
		X.exit(all);
		String[] strings = {JiraMod.class.getPackage().getName()};
		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(strings, ZType.ZTypeAno.class);
		ZType zType = new ZType(allPackageClassViaClassgraph.get(0));
		List<ZType.ZMethod> calls = zType.getAllZMethods();
//		X.exit(calls.get(0).call(new String[]{"-task", "SUP-1495556"}));
		X.exit(calls.get(0).invokeWithArgs(new String[]{"-task", "*"}));
//		EnvTlp envTlp = EnvTlp.ofHlpOrg("", "");
//		String[] auth = envTlp.readAsHLP3();
//		auth[2] = "http://asd.asd";
//		X.exit(invokeContext0(null, MAP.of("task", "SUP-1495556")));
//		X.exit(invokeContext0(null, MAP.of("task", "all")));
		Object objs = invokeLines(new String[]{"-task", "SUP-1495556"});
//		Object objs = invokeLines(new String[]{"-task", "NSI-1050"});
//		Object objs = invokeLines(new String[]{"-task", "create", "task.type", "10505", "-task.project", "SUP", "-task.summary", "summarryss", "-task.assignee", "ditts.aleksandr"});
//		Object objs = invokeLines(args);
		X.p("Founded:" + objs);
		X.p("" + objs);
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_task_args(String[] args) {
		return invokeLines(args);
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_task(@ZType.ZArgAno("task") String task) {
		String[] objs = ARR.of("-task", IT.NE(task, "set task key"));
		return invokeLines(objs);
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_all_task_by_projects(@ZType.ZArgAno("projects") String projects) {
		return invokeLines(ARR.of("-task", "*", SOID.toKey(MK_PROJECTS), IT.NE(projects, "set projects")));
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T get_all_task_default_eb() {
		return invokeLines(ARR.of("-task", "*", SOID.toKey(MK_PROJECTS), "EXP,SUP,TSE,BU,ARP,ESD,ACT,INC,EB,NSI"));
	}


	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T create_task(@ZType.ZArgAno("task.project") String project, @ZType.ZArgAno("task.type") String type, @ZType.ZArgAno("task.summary") String summary) {
		return invokeLines(ARR.of("-task", "create", "-task.project", project, "-task.type", type, "-task.summary", summary));
	}

	@ZType.ZMethodAno
	@SneakyThrows
	public static <T> T invokeLines(String[] args) {

		SeqOptions seqOpts = SeqOptions.of(args);

		String task = seqOpts.getSingle(MK_TASK, null);
		if (task != null) {
			String[] hlpIq = seqOpts.getHLP_IQ();
			JiraCli client = JiraCli.JiraUser.createByArgs(hlpIq).createClient();
			if ("*".equals(task)) {
				String projects = seqOpts.getSingle(MK_PROJECTS);
				Iterable<Issue> allTasks = client.getAllDefaultTasksByProjects(SPLIT.allByComma(projects));
				L.info("InvokeLines:All\n" + allTasks);
				return (T) allTasks;
			} else if ("create".equals(task)) {
				String projectKey = seqOpts.getSingle("task.project");
				Long taskType = seqOpts.getSingleAs("task.type", Long.class, 3L);
				String taskSummary = seqOpts.getSingle("task.summary");
//				String taskAssignee = seqOpts.getSingle("task.assignee");
				L.info("InvokeLines:Create:{}({}) - {}", projectKey, taskType, taskSummary);
				BasicIssue issue = client.createIssue(projectKey, taskType, taskSummary);
				return (T) issue;
			}
			Issue issue = client.getIssue(task);
			L.info("InvokeLines:Issue:{}\n{}", task, issue);
			return (T) GsonMap.toMapFromObj(issue);
		}
		throw new RequiredRuntimeException("Set ket task with allowed options [-task *] || [-task all] || || [-task TASK_ID]");
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
