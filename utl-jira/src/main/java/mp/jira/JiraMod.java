package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.map.MAP;
import mpc.types.opts.SeqOptions;
import mpu.X;
import mpu.core.ARR;

import java.io.IOException;
import java.util.Map;

public class JiraMod {

	public static final String MK_TASK = "task";
	public static final String MK_ALL = "all";
	public static final String MK_CREATE = "create";
	public static final String MK_CREATE_PROJECT = "create.task.project";
	public static final String MK_CREATE_TASKTYPE = "create.task.type";
	public static final String MK_CREATE_TASKSUMMARY = "create.task.summary";

	public static void main(String[] args) throws IOException {
//		EnvTlp envTlp = EnvTlp.ofHlpOrg("", "");
//		String[] auth = envTlp.readAsHLP3();
//		auth[2] = "http://asd.asd";
//		X.exit(invokeContext0(null, MAP.of("task", "SUP-1495556")));
//		X.exit(invokeContext0(null, MAP.of("task", "all")));
//		Object objs = invokeLines(new String[]{"-task", "SUP-1495556"});
//		Object objs = invokeLines(new String[]{"-task", "NSI-1050"});
		Object objs = invokeLines(new String[]{"-task", "create", "task.type", "10505", "-task.project", "SUP", "-task.summary", "summarryss", "-task.assignee", "ditts.aleksandr"});
//		Object objs = invokeLines(args);
		X.p("Founded:" + objs);
		X.p("" + objs);
	}

//	enum Mode {
//		TASK, ALL, CREATE
//	}

	@SneakyThrows
	public static <T> T invokeLines(String[] args) {

		SeqOptions seqOpts = SeqOptions.of(args);

		String task = seqOpts.getSingle(MK_TASK, null);
		if (task != null) {
			String[] hlpIq = seqOpts.getHLP_IQ();
			JiraCli client = JiraCli.JiraUser.createByArgs(hlpIq).createClient();
			if ("*".equals(task)) {
				Iterable<Issue> allTasks = client.getAllTasks();
				L.info("InvokeLines:All\n" + allTasks);
				return (T) allTasks;
			} else if ("create".equals(task)) {
				String projectKey = seqOpts.getSingle("task.project");
				Long taskType = seqOpts.getSingleAs("task.type", Long.class, 3L);
				String taskSummary = seqOpts.getSingle("task.summary");
				String taskAssignee = seqOpts.getSingle("task.assignee");
				L.info("InvokeLines:Create:{}({}) - {}", projectKey, taskType, taskSummary);
				return (T) client.createIssue(projectKey, taskType, taskSummary);
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
				Iterable<Issue> issues = jiraCli.getAllTasks();
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
