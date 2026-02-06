package udav_net.bincall;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.exception.RequiredRuntimeException;
import mpc.ui.ColorTheme;
import mpe.core.P;
import mpc.env.Env;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpe.rt.core.ExecRq;
import mpu.X;
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


	public static void main(String[] args) throws ExecRq {

		if (true) {

//			X.exit(loadTaskAsJO(null,"SUP-1593991"));
			X.exit(loadTaskAsJOC(null, "SUP-1593991"));
			X.exit(loadAllTasksByAuth(null,"SUP"));

			Object issue = JiraBin.invokeContext("", MAP.of("task", "EE-92065"));
			X.exit(issue);
		}
		if (true) {

			try {
				P.exit(JiraBin.loadTaskAsJO(1, "EE-92065"));
//				IssueContract c = IssueContract.of(JiraBin.loadTaskAsJO(1, "EE-92065"));
				IssueContract c = IssueContract.of(JiraBin.loadTaskAsJO(new String[]{"", "", "http://asd.sd"}, "EE-92065"));
				P.exit(c.getId());
			} catch (Exception ex) {
				X.p(ex.getMessage());
			}

		}

//		Object issue = JiraBin.invokeContext("", UMap.of("task", "EE-92065"));
	}

	//	public static IssueContract loadAllTasks(Object auth) {
//		return IssueContract.of(loadTaskAsJO(auth));
//	}
	public static IssueContract loadTaskAsJOC(Object auth, String task) {
		return IssueContract.of(loadTaskAsJO(auth, task));
	}

	public static List<IssueContract> loadAllTasksByAuth(Object auth, String projectsByComma) {
		return loadAllTasksAsJO(auth, projectsByComma).stream().map(IssueContract::of).collect(Collectors.toList());
	}
//	final Path jar;

	public interface IssueContract {
		Long getId();

		String getKey();

		String getLabels();

		String getSummary();

		String getStatus();

		String getDescription(String... desc);

		Map getPriority();

		default Prio getPriorityType(Prio... defRq) {
			Prio prio = Prio.valueOfRu((String) getPriority().get("name"), defRq);
			return prio;
		}

		List<Map> getComments();

		List<Map> getAttachments(List<Map>... attachments);

		static IssueContract of(JsonObject issueJsonObject) {
			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		}

		@RequiredArgsConstructor
		enum Prio {
			BLOCK("Блокирующий", ColorTheme.RED),//
			CRYTICAL("Критический", ColorTheme.ORANGE),//
			HIGH("Высокий", ColorTheme.GREEN),//
			MIDDLE("Средний", ColorTheme.LBLUE),//
			LOW("Низкий", ColorTheme.GRAY),//
			UNDEFINED("Неизвестно", ColorTheme.WHITE);
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

	public static JsonObject loadTaskAsJO(Object auth, String task) {
		return UGson.toJsonObjectFromAnyObject(loadTaskNativeTask(auth, task));
	}

	public static List<JsonObject> loadAllTasksAsJO(Object auth, String projectsByComma) {
		List<Object> objects = loadTaskNativeAllTask(auth, projectsByComma);
		return STREAM.mapToList(objects, UGson::toJsonObjectFromAnyObject);
	}

	//
	//
	public static List<Object> loadTaskNativeAllTask(Object auth, String projectsByComma) {
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
