package udav_net.bincall;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.arr.S_;
import mpe.core.P;
import mpc.env.Env;
import mpc.json.UGson;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpu.X;
import org.jetbrains.annotations.NotNull;
import udav_net.bincall.jira.IssueContract;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JiraBinExt extends JiraBin {


	public static void main(String[] args) {

		if (true) {

//			X.exit(loadTaskAsJO(null,"SUP-1593991"));
			X.exit(loadTaskAsJOC(null, "SUP-1593991"));
			X.exit(loadAllTasks_Models(null, "SUP"));

			Object issue = JiraBinExt.invokeContext("", MAP.of("task", "EE-92065"));
			X.exit(issue);
		}
		if (true) {

			try {
				P.exit(JiraBinExt.loadTaskAsJO(1, "EE-92065"));
//				IssueContract c = IssueContract.of(JiraBin.loadTaskAsJO(1, "EE-92065"));
				IssueContract c = IssueContract.of(JiraBinExt.loadTaskAsJO(new String[]{"", "", "http://asd.sd"}, "EE-92065"));
				P.exit(c.getId());
			} catch (Exception ex) {
				X.p(ex.getMessage());
			}

		}

	}

	public static IssueContract loadTaskAsJOC(Object auth, String task) {
		JsonObject issueJsonObject = loadTaskAsJO(auth, task);
		return IssueContract.of(issueJsonObject);
	}

	public static List<IssueContract> loadAllTasks_Models(Object auth, String projectsByComma) {
		List<JsonObject> jsonObjects = loadAllTasks_JsonObects(auth, projectsByComma);
		return jsonObjects.stream().map(IssueContract::of).collect(Collectors.toList());
	}

	public static JsonObject loadTaskAsJO(Object auth, String task) {
		return UGson.toJsonObjectFromAnyObject(loadTaskNativeTask(auth, task));
	}

	public static List<JsonObject> loadAllTasks_JsonObects(Object auth, String projectsByComma) {
		List<Object> objects = loadAllTasks_RspObject(auth, projectsByComma);
		return S_.mapToList(objects, UGson::toJsonObjectFromAnyObject);
	}

	//
	//
	public static List<Object> loadAllTasks_RspObject(Object auth, String projectsByComma) {
		return (List<Object>) invokeContext(auth, MAP.of("all", "*", "projects", projectsByComma));
	}

	public static Object loadTaskNativeTask(Object auth, String task) {
		return invokeContext(auth, MAP.of("task", task));
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
		return Env.getNativeBinLibsPath(JARNAME_GDBMOD, true);
	}

}
