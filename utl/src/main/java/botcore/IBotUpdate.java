package botcore;


import mpu.pare.Pare;

import java.util.List;

public interface IBotUpdate {

	boolean isDecode(boolean... state);

	String getCallbackData();

	String getMessageOrCallbackData();

	long getChatIdAny();

	default boolean getIsCallbackOrMessage() {
		return getCallbackData() != null;
	}

	String getMessageText();

	default List<Pare> getSpecialTypes() {
		return null;
	}

	void setMetaData(String key, Object value);

	Object getMetaData(String key, Object... defRq);

}
