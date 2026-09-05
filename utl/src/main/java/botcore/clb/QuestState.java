package botcore.clb;

import botcore.msg.IBotMsg;
import lombok.Getter;
import mpu.core.ENUM;
import mpc.map.MAP;
import mpu.core.QDate;

import java.util.Map;

public class QuestState {
	public final Map data;
	private IBotMsg msg;

	public IBotMsg msg() {
		return msg;
	}

	public QuestState msg(IBotMsg msg) {
		this.msg = msg;
		return this;
	}

	@Getter
	private QDate dateExpired;

	public QuestState(Map data) {
		this.data = data;
		updateDateExpired();
	}

	public QuestState updateDateExpired() {
		dateExpired = QDate.now().addMinutes(15);
		return this;
	}

	public static QuestState ofMap(Object... pairKeyValues) {
		Map map = MAP.mapOf(pairKeyValues);
		return new QuestState(map);
	}

	public static QuestState of(Map data) {
		return new QuestState(data);
	}

	public static boolean isExpired(QuestState questState) {
		if (questState == null) {
			return true;
		}
		return QDate.now().isAfter(questState.dateExpired);
	}

	@Override
	public String toString() {
		return "QuestState{" +
				"ctx=" + data +
				'}';
	}

	public <T> T get(Object key, Class<T> type, T... defRq) {
		return MAP.getAs((Map<Object, ?>) data, key, type, defRq);
	}

	public Object get(Object key) {
		return data.get(key);
	}

	public String getString(Object key, String... defRq) {
		return get(key, String.class, defRq);
	}

	public Long getLong(Object key, Long... defRq) {
		return get(key, Long.class, defRq);
	}

	public Integer getInt(Object key, Integer... defRq) {
		return get(key, Integer.class, defRq);
	}

	public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass, E... defRq) {
		return ENUM.valueOf(getString(key), enumClass, defRq);
	}

	public boolean hasKey(Object quest_key) {
		return data.containsKey(quest_key);
	}
}
