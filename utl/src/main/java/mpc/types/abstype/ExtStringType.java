package mpc.types.abstype;

public class ExtStringType<T> extends AbsType<T> {

	private final String _ext;

	public ExtStringType(String name, T value) {
		this(name, value, null, null);
	}

	public ExtStringType(String name, T value, String ext, Class<T> type) {
		super(name, value, type);
		this._ext = ext;
	}

	public static <T> ExtStringType of(String key, T val, String resp, Class<T>... type) {
		return type.length == 0 ? new ExtStringType(key, val, resp, null) : new ExtStringType(key, val, resp, type[0]);
	}

	public String ext() {
		return _ext;
	}

	@Override
	public String toString() {
		return super.toString() +
			   "\next:>>>\n" + _ext + '\'' +
			   "\n<<<:ext";
	}
}
