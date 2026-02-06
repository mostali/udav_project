package mpu.core;

import java.util.List;

public interface IEnum {
	default int i() {
		return ENUM.indexOf((Enum) this);
	}

//	default List<String> getValuesList() {
//		return ENUM.getValuesAsString((Class) getClass());
//	}
}
