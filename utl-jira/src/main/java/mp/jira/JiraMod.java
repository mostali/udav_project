package mp.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mpc.env.EnvTlp;
import mpc.exception.FIllegalStateException;
import mpc.log.L;
import mpc.map.MAP;
import mpe.core.P;
import mpu.X;
import mpu.core.ARR;

import java.io.IOException;
import java.util.Map;

public class JiraMod {

	public static final String MK_TASK = "task";
	public static final String MK_ALL = "all";

	public static void main(String[] args) throws IOException {
		EnvTlp envTlp = EnvTlp.ofHlpOrg("", "");
		String[] auth = envTlp.readAsHLP3();
		auth[2] = "http://asd.asd";
		try{
			Object issue = invokeContext0(auth, MAP.of("task", "ISSUE_ID"));
//		Object issue = invokeContext0(1, UMap.of("task", "ISUUUE___ID"));
			P.p(issue);
		}catch (Exception ex){
			X.p(ex.getMessage());
		}
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

		//
		//

		L.info("Invoke Context not found:" + context);
		throw new FIllegalStateException("Invoke Context not found");

	}
}
