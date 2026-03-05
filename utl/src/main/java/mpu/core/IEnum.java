package mpu.core;

public interface IEnum {

	default int eIndex() {
		return ENUM.indexOf((Enum) this);
	}

//	default <T extends Enum> T valueOf(String name, T... defRq) {
//		return ENUM.valueOf(name, getClass(), this);
//	}

//	default List<String> getValuesList() {
//		return ENUM.getValuesAsString((Class) getClass());
//	}
}
