package udav_net.bincall;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.json.GsonMap;
import mpc.json.UGson;
import mpc.rfl.RFL;
import mpe.NT;
import mpe.cmsg.std.JqlCallMsg;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udav_net.bincall.jira.IssueContract;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JiraBin {

	public static final Logger L = LoggerFactory.getLogger(JiraBin.class);


	public static final String CLASS_GDBMOD = "mp.jira.JiraMod";
	public static final String JARNAME_GDBMOD = "jira-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";

	public static final String[] CALL_CTX = {JARNAME_GDBMOD, CLASS_GDBMOD, CALL_METHODNAME};
	public static final String[] CALL_CTX_NOMETHOD = {JARNAME_GDBMOD, CLASS_GDBMOD, null};

	public static JiraBinV3 apiV3() {
		return new JiraBinV3();
	}

	public static class JiraBinV3 {

		public Object invokeMsg(String jqlMsg) {
			return ZBin.JIRA.newJarCall().invokeMethod_By_PareClassValue("invokeMsg", String.class, jqlMsg);
		}

		public List<IssueContract> getIssuesByJql(String jql) {
			throw NI.stop();
//			return getIssues();
		}

		@SneakyThrows

		public List<IssueContract> getIssues(String jqlMsg) {
			List rspObjs = (List) invokeMsg(jqlMsg);

			List<IssueContract> objsAsModels = (List) rspObjs.stream().map(t -> {

				JsonObject jo = UGson.toJsonObjectFromAnyObject(t);
				JsonObject joIs = jo;
				if (jo.has("issue")) {
					joIs = jo.getAsJsonObject("issue");
				}
				IT.state(joIs.has("key"), "What is issue json", joIs);

				return IssueContract.of(joIs);

			}).collect(Collectors.toList());

			return objsAsModels;
		}

		public Map<String, IssueContract> getIssuesAsMap(@NotNull String jqlExpression, String[] hlp) {
			List<IssueContract> issues = getIssues(JqlCallMsg.buildMsgByHlp(hlp, jqlExpression));
			Map<String, IssueContract> map = issues.stream().collect(Collectors.toMap(k -> k.getKey(), v -> v));
			return map;
		}
	}

	public static void main(String[] args) {

		NT.BEA.set();

		{
			String jqlMsg = "jql:https://job-jira.otr.ru/browse/NSI-3733\n" + //
					"--auth.usr:dav\n" + //
					"--app.org:otr\n";


//			Object o1 = apiV3().invokeMsg(jqlMsg);
		}
		{
			String jqlMsg = "jql:project in (NSI, BSK) AND issuetype = Sub-task AND status in (\"In Progress\",Paused) AND assignee in (currentUser())\n" + //
					"--auth.usr:dav\n" + //
					"--app.org:otr\n";

			List<IssueContract> issues = apiV3().getIssues(jqlMsg);
//			Object o1 = issues;

//			Object o1 = apiV3().invokeMsg(jqlMsg);
			X.exit(issues);
		}
		{
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

			Object o1 = apiV3().invokeMsg(jqlMsg);
			GsonMap gm = GsonMap.ofObj(o1);
			Sys.open_Chrome("https://ias-tst-job-jira.otr.ru/browse/" + gm.get("key"));

			X.exit(o1);
		}
//		Object o = JiraBin.of(jqlMsg).invokeMsg();

	}

	public static class JqlLoader implements Function<JqlLoader.IJqlFilter, List<IssueContract>> {

		public final String[] hlp;

		public final IJqlFilter jqlFilter;

		public JqlLoader(JqlCallMsg jqlMsg) {
			this(null, new JqlFilter(jqlMsg));
		}

		public JqlLoader(String[] hlp, IJqlFilter jqlFilter) {
			this.hlp = hlp;
			this.jqlFilter = jqlFilter;
		}


		public String jqlExpression(String... defRq) {
			if (jqlFilter instanceof JqlFilter) {
				return ((JqlFilter) jqlFilter).jql();
			}
			return ARG.throwMsg(() -> X.f("Only direct filter support jql. Now: " + RFL.scn(jqlFilter, null)), defRq);
		}

		public interface IJqlFilter {

		}

		public static class JqlFilter implements IJqlFilter {

			public final JqlCallMsg jqlMsg;
			public final String _jql;

			public String jql() {
				return _jql;
			}

			public JqlFilter(String hlp[], String jqlExpression) {
				this._jql = jqlExpression;
				this.jqlMsg = JqlCallMsg.of(JqlCallMsg.buildMsgByHlp(hlp, IT.NB(jqlExpression, "set jqlExpression")));
			}

			public JqlFilter(JqlCallMsg jqlMsg) {
				this.jqlMsg = jqlMsg;
				this._jql = jqlMsg.getKeyAsJql(null);
			}
		}

		public static class JqlFilterCustom implements IJqlFilter {
			public List<String> project = null, status = null, issuetype = null;
		}

		public List<IssueContract> loadIssues() {
			return apply(IT.NN(jqlFilter, "set jql filter"));
		}

		@Override
		public List<IssueContract> apply(IJqlFilter jiraCmdFilter) {
			if (jiraCmdFilter instanceof JqlFilterCustom) {
				return loadByCustom((JqlFilterCustom) jiraCmdFilter);
			} else if (jiraCmdFilter instanceof JqlFilter) {
				return loadByJql((JqlFilter) jiraCmdFilter);
			} else {
				throw new FIllegalStateException("Set JqlFilter");
			}

		}

		public List<IssueContract> loadByJql(JqlFilter jiraCmdFilter) {

			String msg = X.fl("Call jira from [{}] with jql [{}]", jiraCmdFilter.jqlMsg.toObjMsgId(null), jiraCmdFilter.jql());
			L.info(msg);

			List<IssueContract> issueContracts = apiV3().getIssues(jiraCmdFilter.jqlMsg.getMsg());

			msg = "Jira call JQL successfully:" + X.sizeOf(issueContracts);
			L.info(msg);

			return issueContracts;
		}

		public List<IssueContract> loadByCustom(JqlFilterCustom jiraCmdFilter) {

			String msg = X.fl("Call jira from [{}->{}] with pojects [{}]", hlp[0], "ПАРОЛЬ", jiraCmdFilter.project);
			L.info(msg);
//			ZKI.log(msg);

			List<String> projects = IT.notEmpty(jiraCmdFilter.project, "set projects");
			String projectsArg = JOIN.allByComma(projects);
			List<IssueContract> issueContracts = JiraBinExt.loadAllTasks_Models(hlp, projectsArg);

			msg = "Jira call CUSTOM successfully:" + X.sizeOf(issueContracts);
			L.info(msg);
//			ZKI.log(msg);

			return issueContracts;
		}
	}

}
