package mp.jira;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import mp.jira.api.Issue0;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.trees.lifecache.UTreeLifeCacheAbstract;
import mp.utl_odb.tree.trees.lifecache.UTreeTtl;
import mpc.arr.STREAM;
import mpc.exception.FIllegalStateException;
import mpc.fs.Ns;
import mpc.time.EDayTime;
import mpc.types.tks.FIDT;
import mpc.url.UUrl;
import mpe.cmsg.core.ErrorCollector;
import mpe.cmsg.std.JqlCallMsg;
import mpf.zcall.ZType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.Hu;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@ZType.ZTypeAno(app = "jira", version = "2")
public class JiraMod extends JiraModOld {

	public static final Logger L = LoggerFactory.getLogger(JiraMod.class);


	public static void main(String[] args) throws IOException {

		String jqlFileOne = "/opt/appVol/bea/.planes/.index/jql/.forms/jql-one/AppNotes.props";
		Path path = Paths.get(jqlFileOne);
		JqlCallMsg jqlCallMsg = JqlCallMsg.of(path);
		Object objsOne = invokeMsg(jqlCallMsg);
		X.exit(objsOne);
	}

	//
	//

	@ZType.ZMethodAno
	public static <T> T invokeMsg(String callMsg) {
		return invokeMsg(JqlCallMsg.of(callMsg));
	}

	@ZType.ZMethodAno
	public static <T> T invokeMsg(Object jqlCallMsg) {

		JqlCallMsg callMsg = JqlCallMsg.ofAny(jqlCallMsg);

		InvokerMsg invokerMsg = new InvokerMsg(callMsg);

		L.info("Check URL with task in key..");
		if (invokerMsg.hasUrlInKey()) {
			return invokerMsg.invokeMsg_OneTask();
		}

		L.info("Check JQL in key..");
		if (invokerMsg.hasJqlInKey()) {
			return invokerMsg.invokeMsg_asJql();
		}

		L.info("Check CreateContract in key..");
		if (invokerMsg.hasContract_CREATE_ISSUE()) {
			return invokerMsg.invokeMsg_createContract();
		}
		Sb sb = new Sb();
		sb.NL("Set link to task or jql or create");
		throw new FIllegalStateException(sb.toString());

	}

	//
	//

	@RequiredArgsConstructor
	public static class InvokerMsg {

		public static final long CACHE_INE_TASK_TTL_SEC = 3000;
		public static final long CACHE_INE_TASK_TTL_SEC_NIGHT = 12000;

		public final JqlCallMsg callMsg;

		//
		//

		public JiraCli jiraCli() {
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

		final static UTreeTtl DB_CACHE = (UTreeTtl) UTreeTtl.tree(Ns.of("jiraview"), "tasks").withAutoCleanCfg(new Integer[]{0, 1000, -1, 5000, 1000, 0});

		static {
			DB_CACHE.createDbIfNotExists();
		}

		public <T> T invokeMsg_OneTask() {
			String url = callMsg.getKeyAsTaskUrl();
			TaskId taskId = TaskId.of(url);

			Long cacheTtlSec = callMsg.getCacheTtlSec(EDayTime.isWorkTimeMsk() ? CACHE_INE_TASK_TTL_SEC : CACHE_INE_TASK_TTL_SEC_NIGHT);

			String issueId = taskId.getIssueKey();

			L.info("invokeMsg [BINCALL START] by OneTask: " + " , issueId:" + issueId + ", callMsg : " + callMsg);

			String jsonRsp;

			String secHu = Hu.SEC(cacheTtlSec);

			Function<String, String> loader = (issId) -> {
				L.info("invokeMsg [CACHE LOADING .. ], TTL[{}] [{}]", secHu, issueId);
				Issue issue = jiraCli()._Com().getIssue(issueId.toString());
				String json = Issue0.toJson(issue);
				DB_CACHE.put(issueId, json);
//				String key = issue.getKey();
				return json;
			};

			long millis = TimeUnit.SECONDS.toMillis(cacheTtlSec);

			try {
				Ctx3Db.CtxModelCtr modelWithMaxTtl = DB_CACHE.getModel_WithMaxTtl(issueId, millis);
				if (modelWithMaxTtl == null) {
					L.info("invokeMsg [CACHE NOT FOUND] [" + issueId + "]");
					jsonRsp = loader.apply(issueId);

				} else {
					jsonRsp = modelWithMaxTtl.getValue();
					L.info("invokeMsg [CACHE HOT] [" + issueId + "][" + modelWithMaxTtl.getDiffHuPeriod() + "/" + secHu + "]");
				}
			} catch (UTreeLifeCacheAbstract.ModelLifeMsException e) {
				L.info("invokeMsg [CACHE IS EXPIRED] [" + issueId + "][" + e.timeModel.getDiffHuPeriod()+ "/" + secHu + "]");
				//List<Comment> many = ARRi.firstMany(issue.getComments(), 5,);
				jsonRsp = loader.apply(issueId);
			}

			L.info("invokeMsg [BINCALL LOADED] by OneTask:\n" + jsonRsp);

			return (T) jsonRsp;

		}

		//
		//

		public boolean hasJqlInKey() {
			return X.notBlank(callMsg.getKeyAsJql(null));
		}

		public <T> T invokeMsg_asJql() {
			String jql = callMsg.getKeyAsJql();
			Iterable<Issue0> allTasks = jiraCli()._Jql().getAllTasksByJql(IT.NE(jql, "set jql, obj " + callMsg.toObjMsgId(null)), Issue0.class);
			L.info("invokeMsg by JQL:\n" + allTasks);
			Collection issue0s1 = STREAM.mapToAll(allTasks.iterator(), Issue0::getIssueOrLoad);
			return (T) issue0s1;
		}


		public boolean hasContract_CREATE_ISSUE() {
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
			BasicIssue issue = jiraCli()._Com().createIssue(projectKey, taskType, taskSummary, taskAssignee, taskDesc);
			return (T) issue;

		}
	}

	public static class TaskId extends ErrorCollector {

		final String url;

//		public String getIssueKey() {
//			return fidt.toString();
//		}

		public TaskId(String url, TaskId... defRq) {
			this.url = url;
//			this.fidt = of(url, null);
//			if (fidt == null) {
//				String msg = X.f("Except string [%s] as url", url);
//				ARG.throwMsg(() -> msg, defRq);
//				addError(msg);
//			}
		}

		public static TaskId of(String url, TaskId... defRq) {
			try {
				TaskId taskId = new TaskId(IT.isUrl(url));
				return taskId;
			} catch (Exception ex) {
				return ARG.throwErr(ex, defRq);
			}
		}

		public static FIDT ofUrl(String url, FIDT... defRq) {
			try {
				TaskId taskId = new TaskId(IT.isUrl(url));
				return taskId.fidt();
			} catch (Exception ex) {
				return ARG.throwErr(ex, defRq);
			}
		}

		private FIDT fidt() {
			FIDT fidt = FIDT.of(UUrl.getPathLastItemWoQuery(url));
			IT.NE(fidt.first(), "except project key in url : %s", url);
			IT.isLong0(fidt.second(), "except num project in url : %s", url);
			return fidt;
		}


		public String getIssueKey() {
			return UUrl.getPathLastItemWoQuery(url);
		}

		@Override
		public String toString() {
			return getIssueKey();
		}
	}
}
