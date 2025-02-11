package mpc.types.abstype;

import java.util.Optional;

public class AbsTypeExt<T, E> extends AbsType<T> {

	private final E _ext;

	public AbsTypeExt(String name, T value) {
		this(name, value, null, null);
	}

	public AbsTypeExt(String name, T value, E ext, Class<T> type) {
		super(name, value, type);
		this._ext = ext;
	}

	public static <T> AbsTypeExt of(String key, T val, String resp, Class<T>... type) {
		return type.length == 0 ? new AbsTypeExt(key, val, resp, null) : new AbsTypeExt(key, val, resp, type[0]);
	}

	public E cachedValue() {
		return _ext;
	}

	private void setExt(Optional<T> vl) {
	}

	@Override
	public String toString() {
		return super.toString() +
				"\next:>>>\n" + _ext + '\'' +
				"\n<<<:ext";
	}
}
