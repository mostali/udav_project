package mpc.types.abstype;

import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.str.ObjTo;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.SPLIT;
import mpv.sql_morpheus.SQLPlatform;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;
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

	public AbsType(String name, T value, boolean nn) {
		this(name, value, (Class<T>) value.getClass());
	}

	public AbsType(String name, T value, Class<T> type) {
		this._name = name;
		if (type == null && value == null) {
			throw new NullPointerException("Type & Value is NULL. How to get type?");
		}
		this._type = type != null ? type : (Class<T>) value.getClass();
		this._value = value;
	}

	public static List<AbsType> ofSafeTypes(Object... class_i_arg) {
		IT.isEven2(class_i_arg.length);
		List<AbsType> args = new ArrayList<>();
		for (int i = 0; i < class_i_arg.length; i += 2) {
			args.add(ofSafeType((Class) class_i_arg[i], class_i_arg[i + 1]));
		}
		return args;
	}

	public static AbsType ofSafeType(Class keyType, Object valObj) {
		if (valObj != null) {
			IT.isType(valObj, keyType);
		} else {
			IT.state(!keyType.isPrimitive(), "except not null value for primitive class '%s'", keyType);
		}
		return AbsType.of(null, valObj, keyType);
	}


	//
	//wth
	private static SQLPlatform detectSqlPlatform(ResultSet rs) {
		String simpleName = rs.getClass().getSimpleName();
		switch (simpleName) {
			case "HikariProxyResultSet":
			case "PgResultSet":
				return SQLPlatform.POSTGRE;
			case "JDBC4ResultSet":
				return SQLPlatform.SQLITE;
			default:
				throw NI.stop("Need impl for define SqlPlatform. Who is call?" + simpleName);
		}
	}

	public static AbsType toAbsTypeSql(int colIndex, String name, int sqlType, ResultSet rs) throws SQLException {
		return AbsType.toAbsTypeSql(colIndex, name, sqlType, rs, detectSqlPlatform(rs));
	}

	public static AbsType toAbsTypeSql(int colIndex, String name, int sqlType, ResultSet rs, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlType) {
			case Types.VARCHAR://12
			{
				String val = rs.getString(colIndex);
				AbsType typ = new AbsType(name, val, String.class);
				return typ;
			}
			case Types.INTEGER://4
			case Types.BIGINT://-5
			{
				long valL = rs.getLong(colIndex);
				AbsType typ = new AbsType(name, valL, Long.class);
				return typ;
			}
			case Types.NUMERIC://2
			{
				BigDecimal val = rs.getBigDecimal(colIndex);
				AbsType typ = new AbsType(name, val, BigDecimal.class);
				return typ;
			}
			case Types.DATE://91
			{
				Date val = rs.getDate(colIndex);
				AbsType typ = new AbsType(name, val, Date.class);
				return typ;
			}
			case Types.TIMESTAMP://93
			{
				switch (sqlPlatform) {
					case POSTGRE:
						Timestamp timestamp = rs.getTimestamp(colIndex);
						if (timestamp == null) {
							return new AbsType(name, timestamp, Timestamp.class);
						}
						return new AbsType(name, timestamp, true);
					default:
						return new AbsType(name, rs.getLong(colIndex), Long.class);
				}

			}
			case Types.BINARY://-2
			{
				byte[] val = rs.getBytes(colIndex);
				AbsType typ = new AbsType(name, val, byte[].class);
				return typ;
			}
			case Types.CHAR://1
			{
				byte[] val = rs.getBytes(colIndex);
				AbsType typ = new AbsType(name, val, byte[].class);
				return typ;
			}
			case Types.BIT://-7
			{
				boolean val = rs.getBoolean(colIndex);
				AbsType typ = new AbsType(name, val, boolean.class);
				return typ;
			}
			case Types.NULL://0
			{
				return new AbsType(name, null, Object.class);
			}

			case Types.OTHER: {
				Object object = rs.getObject(colIndex);
				if (sqlType == 1111) {
					if (object == null) {
						return new AbsType(name, object, UUID.class);
					}
					return new AbsType(name, (UUID) object, true);
				}
				return new AbsType(name, null, Object.class);
			}

		}
		Object object = rs.getObject(colIndex);
		throw new WhatIsTypeException("What is SqlType of column [%s], object [%s]", sqlType, object == null ? null : object.getClass());
	}

	public static Pare toPareTypeSql(int colIndex, String name, int sqlType, ResultSet rs, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlType) {
			case Types.VARCHAR://12
			{
				String val = rs.getString(colIndex);
				Pare typ = new Pare3(name, val, String.class);
				return typ;
			}
			case Types.INTEGER://4
			case Types.BIGINT://-5
			{
				long valL = rs.getLong(colIndex);
				Pare typ = new Pare3(name, valL, Long.class);
				return typ;
			}
			case Types.NUMERIC://2
			{
				BigDecimal val = rs.getBigDecimal(colIndex);
				Pare typ = new Pare3(name, val, BigDecimal.class);
				return typ;
			}
			case Types.DATE://91
			{
				Date val = rs.getDate(colIndex);
				Pare typ = new Pare3(name, val, Date.class);
				return typ;
			}
			case Types.TIMESTAMP://93
			{
				switch (sqlPlatform) {
					case POSTGRE:
						return new Pare3(name, rs.getTimestamp(colIndex), true);
					default:
						return new Pare3(name, rs.getLong(colIndex), Long.class);
				}

			}
			case Types.BINARY://-2
			{
				byte[] val = rs.getBytes(colIndex);
				Pare typ = new Pare3(name, val, byte[].class);
				return typ;
			}
			case Types.CHAR://1
			{
				byte[] val = rs.getBytes(colIndex);
				Pare typ = new Pare3(name, val, byte[].class);
				return typ;
			}
			case Types.BIT://-7
			{
				boolean val = rs.getBoolean(colIndex);
				Pare typ = new Pare3(name, val, boolean.class);
				return typ;
			}
			case Types.NULL://0
			{
				return new Pare3(name, null, Object.class);
			}

		}
		Object object = rs.getObject(colIndex);
		throw new WhatIsTypeException("What is SqlType of column [%s], object [%s]", sqlType, object == null ? null : object.getClass());
	}

	public static boolean hasValue(AbsType absType) {
		return absType != null && absType.getValue() != null;
	}

	public boolean isValidTypeValue(boolean... RETURN) {
		return isValidTypeValue(_value, type(), RETURN);
	}

	public boolean isValidTypeValue(Object value, boolean... RETURN) {
		return isValidTypeValue(value, type(), RETURN);
	}

	public static boolean isValidTypeValue(Object value, Class type, boolean... RETURN) {
		try {
			IT.isType(value, type);
			return true;
		} catch (Exception ex) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw ex;
		}
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

	public static <T> AbsType<T> of(String name, T value) {
		return new AbsType(name, value, value.getClass());
	}


	public static <T> AbsType<T> of(String name, T value, Class<T> type, AbsType<T>... defRq) {
		try {
			return new AbsType(name, value, type);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static Map<String, String> asMapWithStringValues(List<AbsType> list) {
		return list.stream().collect(Collectors.toMap(e -> e.name(), v -> v.getValueAsString()));
	}

	public static Map<String, ?> asMapWithObject(List<AbsType> list) {
		return list.stream().collect(HashMap::new, (m, v) -> m.put(v.name(), v.val()), HashMap::putAll);
	}

	public static Map<String, ?> asLinkedMapWithObject(List<AbsType> list) {
		return list.stream().collect(LinkedHashMap::new, (m, v) -> m.put(v.name(), v.val()), HashMap::putAll);
	}

	public static Map<String, AbsType> asMap(List<AbsType> list) {
		return list.stream().collect(Collectors.toMap(e -> e.name(), v -> v));
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

	public T typeObj() {
		return type().cast(_value);
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

	public T getValueOr(T valIfNull) {
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

	public T initDefValue() {
		T defaultValue = ObjTo.toDefaultValue(type());
		setValue(defaultValue);
		return defaultValue;
	}

	public <T> String[] getValueSplited(String del) {
		return SPLIT.argsBy(getValueAsString(), del);
	}
}
