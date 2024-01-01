package mpc.types.abstype;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbsType<T> {
	private final String _name;//имя поля
	private final Class<T> _type;//тип поля
	private T _value;//значение поля

	public AbsType(String name) {
		this(name, null, (Class<T>) String.class);
	}

	public AbsType(String name, Number value) {
		this(name, (T) value, null);
	}

	public AbsType(String name, String value) {
		this(name, (T) value, (Class<T>) String.class);
	}

	public AbsType(T value, Class<T> type) {
		this(null, value, type);
	}

	public AbsType(String name, T value, Class<T> type) {
		this._name = name;
		if (type == null && value == null) {
			throw new NullPointerException("Type & Value is NULL. How to get type?");
		}
		this._type = type != null ? type : (Class<T>) value.getClass();
		this._value = value;
	}

	public static String toLogString(AbsType... fields) {
		if (fields.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (AbsType field : fields) {
			sb.append("Поле '").append(field.name()).append("' значение '").append(field.getValue()).append("'. ");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();//Удалим последний пробел
	}

	public static <T> AbsType<T> of(String name, T value, Class<T> type) {
		return new AbsType(name, value, type);
	}

	public static Map asMap(List<AbsType> list) {
		return list.stream().collect(Collectors.toMap(e -> e.name(), v -> v.getValueAsString()));
	}

	/**
	 * Имя поля.
	 */
	public String name() {
		return _name;
	}

	/**
	 * Тип поля
	 */
	public Class<T> type() {
		return _type;
	}

	/**
	 * Получаем текущее значение поля как объекта (без приведения типов)
	 */
	public Object getValueAsObject() {
		return _value;
	}

	public String getValueAsString() {
		return _value == null ? "null" : _value instanceof String ? (String) _value : _value.toString();
	}

	public <T> T getValue(Class<T> type) {
		return type.cast(getValue());
	}

	public T getValue() {
		return val();
	}

	public T getValueOrDef(T valIfNull) {
		T v = val();
		return v == null ? valIfNull : v;
	}

	public T val() {
		return _value;
	}

	public AbsType<T> setValue(T value) {
		this._value = value;
		return this;
	}

	/**
	 * Клонируем поле
	 */
	public AbsType<T> clone() {
		return new AbsType(name(), getValue(), type());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"name='" + name() + '\'' +
				", value=" + (getValueAsObject() instanceof CharSequence ? "'" + getValueAsObject() + "'" : getValueAsObject()) +
				", type=" + type().getSimpleName() +
				'}';
	}

	public boolean isNullValue() {
		return val() == null;
	}
}
