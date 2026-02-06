package mpc.types.abstype;

public class ExtStringType<T> extends AbsTypeExt<T, String> {

	public ExtStringType(String name, T value) {
		this(name, value, null, null);
	}

	public ExtStringType(String name, T value, String ext, Class<T> type) {
		super(name, value, ext, type);
	}

	public static <T> ExtStringType of(String key, T val, String resp, Class<T>... type) {
		return type.length == 0 ? new ExtStringType(key, val, resp, null) : new ExtStringType(key, val, resp, type[0]);
	}


}
