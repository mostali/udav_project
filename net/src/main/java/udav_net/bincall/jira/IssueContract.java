package udav_net.bincall.jira;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpe.img.EColor;
import mpf.contract.IContract;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.IEnum;

import java.util.List;
import java.util.Map;

public interface IssueContract extends IContract {
	Long getId(Long... defRq);

	String getProject(Long... defRq);

	String getKey(String... defRq);

	String getLabels(String... defRq);

	String getSummary(String... defRq);

//		String getStatus(String... defRq);

	String getDescription(String... defRq);

	Map getPriority(Map... defRq);

	Map getStatus(Map... defRq);

	Map getIssueType(Map... defRq);

	default StatusType getStatusType(StatusType... defRq) {
		Map map = getStatus(ARR.EMPTY_MAP);// mb return null
		if (map != null) {
//			return StatusType.valueOfId(((Number) map.get("id")).longValue(), defRq);
			return StatusType.valueOfName(((String) map.get("name")), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except StatusType from %s", getKey(null)), defRq);
	}

	default IssueType getIssueTypeType(IssueType... defRq) {
		Map map = getIssueType(ARR.EMPTY_MAP);// mb return null
		if (map != null) {
			return IssueType.valueOfName(((String) map.get("name")), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except IssueType from %s", getKey(null)), defRq);
	}

	default PrioType getPriorityType(PrioType... defRq) {
		Map priority = getPriority(ARR.EMPTY_MAP);// mb return null
		if (priority != null) {
			return PrioType.valueOfRu((String) priority.get("name"), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except prio from %s", getKey(null)), defRq);
	}

	List<Map> getComments(List<Map>... defRq);

	List<Map> getAttachments(List<Map>... defRq);

	static IssueContract of(Object issueObject) {
		return of(UGson.toJsonObjectFromAnyObject(issueObject));
	}

	static IssueContract of(JsonObject issueJsonObject) {
//			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		return MapTableContract.buildContract_DefRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
	}

	@RequiredArgsConstructor
	public enum IssueType {
		UNDEFINED(0, "Неизвестно", EColor.YELLOW),//
		ERROR(1, "Ошибка", EColor.GRAY),
		TASK(null, "Задача", EColor.WHITE),
		KD(10101, "Компонентная доработка", EColor.WHITE),
		DORABOTKA(4, "Доработка", EColor.WHITE),
		TASK_DTA(15400, "Задача ДТА", EColor.WHITE),
		REQ(null, "Заявка", EColor.WHITE),
		REQ_IZM(null, "Запрос на изменение", EColor.WHITE),
		REQ_ADM(null, "Запрос на администрирование", EColor.WHITE),
		;
		public final Integer id;
		public final String nameRu;
		//			public final String[] colorTheme;
		public final EColor zkColor;

		public static IssueType valueOfId(Number id, IssueType... defRq) {
			if (id != null) {
				for (IssueType value : values()) {
					if (value.id.intValue() == id.intValue()) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("IssueType except item by id '%s'", id), defRq);
		}

		public static IssueType valueOfName(String name, IssueType... defRq) {
			if (X.notEmpty(name)) {
				for (IssueType value : values()) {
					if (name.equalsIgnoreCase(value.nameRu)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("IssueType except item by name '%s'", name), defRq);
		}
		public static IssueType valueOf(String name, IssueType... defRq) {
			if (X.notEmpty(name)) {
				for (IssueType value : values()) {
					if (value.name().equalsIgnoreCase(name)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("IssueType except item by name '%s'", name), defRq);
		}
	}

	@RequiredArgsConstructor
	public enum StatusType {
		UNDEFINED(0, "Неизвестно", EColor.YELLOW),//
		OPEN(1, "Открыто", EColor.YELLOW),
		CLOSE(6, "Закрыто", EColor.BLACK),
		OZENKA_TRZ(null, "Оценка ТРЗ", EColor.WHITE),
		PROTESTIROWANO(null, "Протестировано", EColor.WHITE),
		DEV(null, "В разработке", EColor.WHITE),
		ANALIZ(null, "Анализ", EColor.WHITE),
		IN_JOB(null, "В работе", EColor.WHITE),
		RECIVE(null, "Приемка", EColor.WHITE),
		REJECTED(null, "Отклонено", EColor.WHITE),
		IN_WAIT(null, "В ожидании", EColor.WHITE),
		IN_BUILD(null, "В сборке", EColor.WHITE),
		;
		public final Integer id;
		public final String nameRu;
		public final EColor zkColor;

		public static StatusType valueOfId(Number id, StatusType... defRq) {
			if (id != null) {
				for (StatusType value : values()) {
					if (value.id.intValue() == id.intValue()) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("StatusType except item by id '%s'", id), defRq);
		}

		public static StatusType valueOfName(String name, StatusType... defRq) {
			if (X.notEmpty(name)) {
				for (StatusType value : values()) {
					if (name.equalsIgnoreCase(value.nameRu)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("StatusType except item by name '%s'", name), defRq);
		}
		public static StatusType valueOf(String name, StatusType... defRq) {
			if (X.notEmpty(name)) {
				for (StatusType value : values()) {
					if (value.name().equalsIgnoreCase(name)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("StatusType except item by name '%s'", name), defRq);
		}
	}


	@RequiredArgsConstructor
	public enum PrioType implements IEnum {
		UNDEFINED("Неизвестно", EColor.YELLOW),//
		NULL("null", EColor.WHITE),//
		BLOCK("Блокирующий", EColor.RED),//
		CRYTICAL("Критический", EColor.ORANGE),//
		HIGH("Высокий", EColor.GREEN),//
		MIDDLE("Средний", EColor.LBLUE),//
		LOW("Низкий", EColor.GRAY),//
		;
		public final String nameRu;
		//			public final String[] colorTheme;
		public final EColor zkColor;

		public static PrioType valueOfRu(String nameRu, PrioType... defRq) {
			if (X.notEmpty(nameRu)) {
				for (PrioType value : values()) {
					if (value.nameRu.equalsIgnoreCase(nameRu)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("PrioType except item by name '%s'", nameRu), defRq);
		}

		public static PrioType valueOf(String name, PrioType... defRq) {
			if (X.notEmpty(name)) {
				for (PrioType value : values()) {
					if (value.name().equalsIgnoreCase(name)) {
						return value;
					}
				}
			}
			return ARG.throwMsg(() -> X.f("PrioType except item by name '%s'", name), defRq);
		}
	}
}
