package udav_net.bincall;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.ui.UColorTheme;
import mpe.core.P;
import mpc.env.Env;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpc.map.UMap;
import mpc.rfl.RFL;
import mpe.rt.core.ExecRq;
import mpu.core.ARG;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JiraBin {

	public static final String CLASS_GDBMOD = "mp.jira.JiraMod";

	public static final String JARNAME_GDBMOD = "jira-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";

	//	public static IssueContract loadAllTasks(Object auth) {
//		return IssueContract.of(loadTaskAsJO(auth));
//	}
	public static IssueContract loadTaskAsJOC(Object auth, String task) {
		return IssueContract.of(loadTaskAsJO(auth, task));
	}

	public static List<IssueContract> loadAllTasksAsJOC(Object auth) {
		return loadAllTasksAsJO(auth).stream().map(IssueContract::of).collect(Collectors.toList());
	}
//	final Path jar;

	public interface IssueContract {
		Long getId();

		String getKey();

		String getLabels();

		String getSummary();

		String getStatus();

		String getDescription();

		Map getPriority();

		default Prio getPriorityType(Prio... defRq) {
			Prio prio = Prio.valueOfRu((String) getPriority().get("name"), defRq);
			return prio;
		}

		List<Map> getComments();

		List<Map> getAttachments();

		static IssueContract of(JsonObject issueJsonObject) {
			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		}

		@RequiredArgsConstructor
		enum Prio {
			BLOCK("Блокирующий", UColorTheme.RED),//
			CRYTICAL("Критический", UColorTheme.ORANGE),//
			HIGH("Высокий", UColorTheme.GREEN),//
			LOW("Низкий", UColorTheme.GRAY),//
			UNDEFINED("Неизвестно", UColorTheme.WHITE);
			public final String nameRu;
			public final String[] colorTheme;

			public static Prio valueOfRu(String name, Prio... defRq) {
				for (Prio value : values()) {
					if (value.nameRu.equalsIgnoreCase(name)) {
						return value;
					}
				}
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Not found item '%s'", name), defRq);
			}
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
		return UGson.toJsonObjectFromAnyObject(loadTaskNativeTask(auth, task));
	}

	public static List<JsonObject> loadAllTasksAsJO(Object auth) {
		return UGson.toJsonObjectFromListAnyObject(loadTaskNativeAllTask(auth));
	}

	//
	//
	public static List<Object> loadTaskNativeAllTask(Object auth) {
		return (List<Object>) invokeContext(auth, UMap.of("all", "*"));
	}

	public static Object loadTaskNativeTask(Object auth, String task) {
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
