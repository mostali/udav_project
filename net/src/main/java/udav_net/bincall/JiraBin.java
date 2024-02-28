package udav_net.bincall;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpe.core.P;
import mpc.env.Env;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpc.map.UMap;
import mpc.rfl.RFL;
import mpe.rt.core.ExecRq;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JiraBin {

	public static final String CLASS_GDBMOD = "mp.jira.JiraMod";

	public static final String JARNAME_GDBMOD = "jira-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";

	public static IssueContract loadTaskAsJOC(Object auth, String task) {
		return IssueContract.of(loadTaskAsJO(auth, task));
	}
//	final Path jar;

	public interface IssueContract {
		Long getId();

		String getSummary();
		String getStatus();

		String getDescription();

		List<Map> getComments();

		List<Map> getAttachments();

		static IssueContract of(JsonObject issueJsonObject) {
			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		}
	}

	public static void main(String[] args) throws ExecRq {

		if (true) {
			IssueContract c = IssueContract.of(JiraBin.loadTaskAsJO(1, "EB-92065"));
			P.exit(c.getId());
		}

//		Object issue = JiraBin.invokeContext("dav", UMap.of("task", "EB-92065"));
		P.exit(JiraBin.loadTaskAsJO(1, "EB-92065"));
	}

	public static JsonObject loadTaskAsJO(Object auth, String task) {
		return UGson.toJsonObjectFromAnyObject(loadTaskNativeObject(auth, task));
	}

	public static Object loadTaskNativeObject(Object auth, String task) {
		return invokeContext(auth, UMap.of("task", task));
	}

	public static Object invokeContext(Object auth, Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{auth, context});
	}

	private static Object invokeJar0(Class[] types, Object[] vls) {
		Object o = RFL.invokeJarSt(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, types, vls);
		return o;
	}

	private static Object invokeJarWith0(Object... kv) {
		Object o = RFL.invokeJarStWith(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, kv);
		return o;
	}

	@NotNull
	private static Path getAndCheckJarLocation() {
		return Env.getBinPath(JARNAME_GDBMOD, true);
	}

}
