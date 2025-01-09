package mp.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.log.L;
import mpe.core.P;
import mpc.map.UMap;
import mpu.core.ARR;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JiraMod {


	public static final String MK_TASK = "task";
	public static final String MK_ALL = "all";

	public static void main(String[] args) throws IOException {
		Object issue = invokeContext0(1, UMap.of("task", "ISUUUE___ID"));
		P.p(issue);
	}

	@SneakyThrows
	public static <T> T invokeContext0(Object auth, Map context) {
		{
			String task = (String) UMap.get(context, MK_TASK, null);
			if (task != null) {
				JiraCli jiraCli = JiraCli.ofAuth(auth);
				Issue issue = jiraCli.getIssue(task);
				//		return (T) UGson.toStringJsonFromObject(issue);
				return (T) issue;
			}
		}
		{
			String all = (String) UMap.get(context, MK_ALL, null);
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
