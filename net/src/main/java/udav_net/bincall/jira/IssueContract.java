package udav_net.bincall.jira;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.json.UGson;
import mpc.map.MapTableContract;
import mpc.rfl.RFL;
import mpc.str.sym.SYMJ;
import mpe.img.EColor;
import mpe.str.CN;
import mpf.contract.IContract;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.IEnum;
import mpu.core.QDate;
import mpu.pare.Tuple;
import mpu.str.STR;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
//			return StatusType.valueOfName(((String) map.get("name")), defRq);
			return StatusType.valueOfIdOrName((map.get("name")), defRq);
		}
		return ARG.throwMsg(() -> X.f("Except StatusType from %s", getKey(null)), defRq);
	}

	default IssueType getIssueTypeType(IssueType... defRq) {
		Map map = getIssueType(ARR.EMPTY_MAP);// mb return null
		if (map != null) {
			return IssueType.valueOfIdOrName((map.get("name")), defRq);
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

	default List<CommentTuple> getCommentsAsText() {
		List<Map> comments = getComments(ARR.EMPTY_LIST);
		if (X.empty(comments)) {
			return ARR.EMPTY_LIST;
		}
		return comments.stream().map(m -> {
			long id = ((Number) m.get("id")).longValue();
			Object body = m.get("body");
			Object author = ((Map) m.get("author")).get("name");
			long date = ((Number) ((Map) m.get("updateDate")).get("iMillis")).longValue();
			Object[] objs = new Object[]{id, author, QDate.of(date).f(QDate.F.MONO17NF), body};
			return new CommentTuple(objs);
		}).collect(Collectors.toList());
	}

	public static class CommentTuple extends Tuple {
		@Override
		public String toString() {
			return toStringSimple();
		}

		public String toStringSimple() {
//			return JOIN.argsBy(STR.DELR, getAuthor(), getDate(), getMsg());
			String msg = SYMJ.THINK + "" + getAuthor() + "[" + getDate() + "]" + STR.DELR + getMsg();
//			String msg =  getAuthor() + "[" + getDate() + "]" + SYMJ.THINK + getMsg();
			return X.toStringLine(msg);
		}

		public CommentTuple(Object[] obs) {
			super(obs);
		}

		public long getId() {
			return (long) objs[0];
		}

		public String getMsg() {
			return (String) objs[3];
		}

		public String getAuthor() {
			return (String) objs[1];
		}

		public String getDate() {
			return (String) objs[2];
		}
	}

	;

	List<Map> getIssueFields(List<Map>... defRq);

	List<Map> getAttachments(List<Map>... defRq);

	static IssueContract ofAny(Object issueObject) {
		return of(UGson.toJsonObjectFromAnyObject(issueObject));
	}

	static IssueContract of(String issueJsonObject) {
//			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		return MapTableContract.buildContract_DefRq(UGson.toMapFromString(issueJsonObject), IssueContract.class);
	}

	static IssueContract of(JsonObject issueJsonObject) {
//			return MapTableContract.buildContract_MarkNotRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		return MapTableContract.buildContract_DefRq(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
	}

	default String assignee() {
		Map<String, String> assignee = (Map<String, String>) mapdb().get("assignee");
		String userLogin = assignee.get("name");
		return userLogin;
	}

	default String reporter() {
		Map<String, String> assignee = (Map<String, String>) mapdb().get("reporter");
		String userLogin = assignee.get("name");
		return userLogin;
	}

	default String dateFinishDev() {
		List<Map> fileds = getIssueFields();
//		String kvDate="customfield_28553";
		String kvDate = "customfield_28555";
		Map map = fileds.stream().filter(m -> kvDate.equals(m.get(CN.ID))).findAny().orElse(null);
		if (map == null) {
			return null;
		}
		Object o = map.get("value");
		return (String) o;
	}


	public enum IssueType {
		UNDEFINED(0, "Неизвестно", EColor.YELLOW),//
		ERROR(1, "Ошибка", EColor.GRAY),
		IN_JOB(5, "Подзадача", EColor.WHITE),
		TASK(null, "Задача", EColor.WHITE),
		KD(10101, "Компонентная доработка", "КД", EColor.WHITE),
		DORABOTKA(4, "Доработка", EColor.WHITE),
		TASK_DTA(15400, "Задача ДТА", EColor.WHITE),
		REQ(null, "Заявка", EColor.WHITE),
		REQ_IZM(null, "Запрос на изменение", "Зап.Изм", EColor.WHITE),
		REQ_ADM(null, "Запрос на администрирование", "Зап.Админ", EColor.WHITE),
//		CHECK_CODE(16706, "Проверка кода", "Пров.Кода", EColor.WHITE),
		;
		public final Integer id;
		public final String nameRu;
		public final String nameRuShort;
		public final EColor zkColor;

		IssueType(Integer id, String nameRu, EColor zkColor) {
			this(id, nameRu, nameRu, zkColor);
		}

		IssueType(Integer id, String nameRu, String nameRuShort, EColor zkColor) {
			this.id = id;
			this.nameRu = nameRu;
			this.nameRuShort = nameRuShort;
			this.zkColor = zkColor;
		}

		public static IssueType valueOfIdOrName(Object id, IssueType... defRq) {
			IssueType issueType = null;
			if (id instanceof Number) {
				issueType = valueOfId((Number) id, null);
				if (issueType != null) {
					return issueType;
				}
			} else if (id instanceof String) {
				issueType = valueOfName((String) id, null);
				if (issueType != null) {
					return issueType;
				}
			}
			return ARG.throwMsg(() -> X.f("IssueType except item type Number|String [ vs %s ] by id '%s'", RFL.cn(id, null), id), defRq);
		}

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
		OK(1, "Решен", EColor.GRAY),
		OPEN(1, "Открыто", EColor.YELLOW),
		CLOSE(6, "Закрыто", EColor.BLACK),
		ISPRAWLENIE(null, "Исправление", EColor.LBLUE),
		PROTESTIROWANO(null, "Протестировано", EColor.WHITE),
		DEV(null, "В разработке", EColor.WHITE),
		ANALIZ(null, "Анализ", EColor.WHITE),
		IN_JOB(null, "В работе", EColor.WHITE),
		RECIVE(null, "Приемка", EColor.WHITE),
		REJECTED(null, "Отклонено", EColor.WHITE),
		IN_WAIT(null, "В ожидании", EColor.WHITE),
		IN_BUILD(null, "В сборке", EColor.WHITE),

		PROEKTIROWANIE(10000, "Проектирование", EColor.WHITE),

		CHECK_CODE(16706, "Проверка кода", EColor.WHITE),
		OZENKA_TRZ(16900, "Оценка ТРЗ", EColor.WHITE),

		;
		public final Integer id;
		public final String nameRu;
		public final EColor zkColor;


		public static StatusType valueOfIdOrName(Object id, StatusType... defRq) {
			StatusType statusType = null;
			if (id instanceof Number) {
				statusType = valueOfId((Number) id, null);
				if (statusType != null) {
					return statusType;
				}
			} else if (id instanceof String) {
				statusType = valueOfName((String) id, null);
				if (statusType != null) {
					return statusType;
				}
			}
			return ARG.throwMsg(() -> X.f("IssueType except item type Number|String [ vs %s ] by id '%s'", RFL.cn(id, null), id), defRq);
		}

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
