package mpe;

public class UFlags {

	public static boolean isSet(int value, int flag) {
		return (value & flag) == flag;
	}

	public static int set(int value, int flag) {
		return (value | flag);
	}

	public static int unset(int value, int flag) {
		return (value & ~flag);
	}

}