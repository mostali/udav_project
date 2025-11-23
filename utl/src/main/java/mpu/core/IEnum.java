package mpu.core;

public interface IEnum {
	default int i() {
		return ENUM.indexOf((Enum) this);
	}
}
