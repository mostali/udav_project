package mpc.str;

import com.google.gson.JsonPrimitive;
import lombok.SneakyThrows;
import mpc.exception.NI;
import mpc.json.UGson;
import mpc.num.UNum;
import mpu.X;
import mpe.core.P;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import mpu.IT;
import mpc.json.GsonMap;
import mpu.str.UST;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ObjTo {

	public static void main(String[] args) {
		P.p(objTo("123567890abcdfфывфыв", InputStream.class));
	}

	@Deprecated //why not objTo ?
	public static <T> T anyTo(Object objectOrCharSequence, Class<T> clazz) {
		IT.notNull(objectOrCharSequence);
		if (clazz.isAssignableFrom(objectOrCharSequence.getClass())) {
			return clazz.cast(objectOrCharSequence);
		}
		if (clazz.isAssignableFrom(CharSequence.class)) {
			return UST.strTo(((CharSequence) objectOrCharSequence), clazz);
		}
		return objTo(objectOrCharSequence, clazz);
	}

	public static <T> T objTo(Object obj, Class<T> clazz, T... defRq) {
		try {
			return objToRq(obj, clazz);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	@SneakyThrows
	public static <T> T objToRq(Object obj, Class<T> clazz) {
		IT.notNull(obj);
		if (clazz.isAssignableFrom(obj.getClass())) {
			return clazz.cast(obj);
		} else if (CharSequence.class.isAssignableFrom(clazz)) {
			return obj instanceof String ? (T) obj : (T) obj.toString();
		} else if (obj instanceof CharSequence) {
			return UST.strTo(((CharSequence) obj), clazz);
		} else if (obj instanceof JsonPrimitive) {
			return UGson.toObject((JsonPrimitive) obj, clazz);
		}
//		else if (obj instanceof Number) {
//
//		}
		if (clazz.isPrimitive()) {
			return obj instanceof Number ? (T) toPrimitiveNumber((Number) obj, clazz) : (T) toPrimitiveNon(obj, clazz);
		} else if (clazz == Boolean.class) {
			if (obj instanceof Boolean) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) (Boolean) (((Number) obj).intValue() == 0 ? false : true);
			} else if (obj instanceof CharSequence) {
				return (T) UST.BOOL(obj.toString());
			}
		} else if (clazz == Integer.class) {
			if (obj instanceof Integer) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) (Integer) ((Number) obj).intValue();
			} else if (obj instanceof CharSequence) {
				return (T) UST.INT((String) obj);
			}
		} else if (clazz == Long.class) {
			if (obj instanceof Long) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) (Long) ((Number) obj).longValue();
			} else if (obj instanceof CharSequence) {
				return (T) UST.LONG((String) obj);
			}
		} else if (clazz == Double.class) {
			if (obj instanceof Double) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) (Double) ((Number) obj).doubleValue();
			} else if (obj instanceof CharSequence) {
				return (T) UST.DBL((String) obj);
			}
		} else if (clazz == BigDecimal.class) {
			if (obj instanceof BigDecimal) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) new BigDecimal(((Number) obj).doubleValue());
			} else if (obj instanceof CharSequence) {
				return (T) UST.BD((String) obj);
			}
		} else if (clazz == Float.class) {
			if (obj instanceof Float) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) (Float) ((Number) obj).floatValue();
			} else if (obj instanceof CharSequence) {
				return (T) UST.FLOAT((String) obj);
			}
		} else if (clazz == Map.class) {
			if (obj instanceof Map) {
				return (T) obj;
			}
		} else if (clazz == GsonMap.class) {
			if (obj instanceof Map) {
				return (T) GsonMap.of((Map) obj);
			}
		} else if (clazz == BigInteger.class) {
			if (obj instanceof BigInteger) {
				return (T) obj;
			} else if (obj instanceof Number) {
				return (T) BigInteger.valueOf(((Number) obj).longValue());
			} else if (obj instanceof CharSequence) {
				return (T) UST.BI(obj.toString());
			}
		} else if (clazz == Path.class) {
			if (obj instanceof Path) {
				return (T) obj;
			} else if (obj instanceof File) {
				return (T) ((File) obj).toPath();
			} else if (obj instanceof CharSequence) {
				return (T) Paths.get(obj.toString());
			}
		} else if (clazz == InputStream.class) {
			if (obj instanceof InputStream) {
				return (T) obj;
			} else if (obj instanceof File || obj instanceof Path) {
				return (T) new FileInputStream((File) obj);
			} else if (obj instanceof CharSequence) {
				return (T) new ByteArrayInputStream(obj.toString().getBytes(Charset.defaultCharset()));
			}
		}
		throw new RequiredRuntimeException("Wrong Value [" + obj + "] for type [" + clazz + "]");
	}

	private static Object toPrimitiveNumber(Number obj, Class primitiveClass) {
		if (obj == null) {
			return getInitValueForPrimitive(primitiveClass);
		}
		if (Integer.TYPE == primitiveClass) {
			return obj.intValue();
		} else if (Long.TYPE == primitiveClass) {
			return obj.longValue();
		} else if (Short.TYPE == primitiveClass) {
			return obj.shortValue();
		} else if (Byte.TYPE == primitiveClass) {
			return obj.byteValue();
		} else if (Double.TYPE == primitiveClass) {
			return obj.doubleValue();
		} else if (Float.TYPE == primitiveClass) {
			return obj.floatValue();
		}
		throw new WhatIsTypeException("Except primitive class for Number type '%s'", primitiveClass.getName());
	}

	private static Object toPrimitiveNon(Object obj, Class primitiveClass) {
		if (obj == null) {
			return getInitValueForPrimitive(primitiveClass);
		}
		if (primitiveClass == Boolean.TYPE) {
			if (obj instanceof Boolean) {
				return (boolean) obj;
			}
		} else if (primitiveClass == Character.TYPE) {
			if (obj instanceof Character) {
				return (char) obj;
			}
		}
		throw new WhatIsTypeException("Except primitive class for NoNumber type '%s'", primitiveClass.getName());
	}


	public static Class convertObjectClassToPrimitiveClass(Class wrapClass, Class... defRq) {
		if (wrapClass == Integer.class) {
			return Integer.TYPE;
		} else if (wrapClass == Long.class) {
			return Long.TYPE;
		} else if (wrapClass == Boolean.class) {
			return Boolean.TYPE;
		} else if (wrapClass == Double.class) {
			return Double.TYPE;
		} else if (wrapClass == Character.class) {
			return Character.TYPE;
		} else if (wrapClass == Float.class) {
			return Float.TYPE;
		} else if (wrapClass == Byte.class) {
			return Byte.TYPE;
		} else if (wrapClass == Short.class) {
			return Short.TYPE;
		} else if (wrapClass == Void.class) {
			return Void.TYPE;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new WhatIsTypeException("Class '%s' is not wrapper primitive class", wrapClass.getName());
	}

	public static Object convertObjectToObject(Class srcType, Object srcObj, Class dstClass) {
		if (srcType == Long.class) {
			if (dstClass == Long.class || dstClass == Long.TYPE) {
				return srcObj;
			} else if (dstClass == Integer.class || dstClass == Integer.TYPE) {
				return (int) (long) (Long) srcObj;
			} else if (dstClass == Short.class || dstClass == Short.TYPE) {
				return (short) (long) (Long) srcObj;
			} else if (dstClass == Byte.class || dstClass == Byte.TYPE) {
				return (byte) (long) (Long) srcObj;
			}
		}
		throw new WhatIsTypeException("Src Object typeof '%s' could't convert to Dst Object typeof '%s':", srcType.getName(), dstClass.getName(), srcObj);
	}

	public static Object convertObjectToPrimitive(Class objWrapperType, Object obj, boolean allowNull, Object... defRq) {
		try {
			return convertObjectToPrimitiveRq(objWrapperType, obj, allowNull);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(ex);
		}
	}

	public static Object convertObjectToPrimitiveRq(Class objWrapperType, Object obj, boolean allowNull) {
		Class primitiveClass = ObjTo.convertObjectClassToPrimitiveClass(objWrapperType);
		if (obj == null) {
			if (!allowNull) {
				throw new FIllegalStateException("Object '%s' is null. But allowNull=false.", objWrapperType);
			}
			return getInitValueForPrimitive(objWrapperType);
		}
		if (objWrapperType == Integer.class) {
			int obj1 = (Integer) obj;
			return obj1;
		} else if (objWrapperType == Long.class) {
			long obj1 = (Long) obj;
			return obj1;
		}
		throw new WhatIsTypeException("Object '%s'/'%s' without wrapper primitive class", primitiveClass.getName());
	}

	public static Object getInitValueForPrimitive(Class primitiveClass) {
		IT.state(primitiveClass.isPrimitive(), "except primitive class [%s]", primitiveClass);
		if (primitiveClass == Integer.TYPE) {
			return (int) 0;
		} else if (primitiveClass == Long.TYPE) {
			return (long) 0;
		} else if (primitiveClass == Boolean.TYPE) {
			return false;
		} else if (primitiveClass == Double.TYPE) {
			return (double) 0;
		} else if (primitiveClass == Character.TYPE) {
			return (char) '\u0000';
		} else if (primitiveClass == Float.TYPE) {
			return (float) 0;
		} else if (primitiveClass == Byte.TYPE) {
			return (byte) 0;
		} else if (primitiveClass == Short.TYPE) {
			return (short) 0;
		}
//		else if (primitiveClass == Void.TYPE) {
//			return Void.class;
//		}
		throw new WhatIsTypeException("Class '%s' without wrapper primitive class", primitiveClass.getName());
	}

	public static <T> T toDefaultValue(Class<T> type) {
		if (type == String.class) {
			return (T) "";
		} else if (type == Boolean.class) {
			return (T) (Boolean) false;
		} else if (Number.class.isAssignableFrom(type)) {
			return UNum.toNumberDefaultValue(type);
		}
		throw NI.stop(type);
	}
}
