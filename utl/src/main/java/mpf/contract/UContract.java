package mpf.contract;

import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.RequiredRuntimeException;
import mpc.map.MapTableContract;
import mpc.map.MAP;
import mpc.str.ObjTo;
import mpu.str.STR;
import mpu.pare.Pare;

import java.lang.reflect.Method;
import java.util.Map;

public class UContract {

	@Deprecated//NU
	public static <T> T getRequiredContractValue_LAST_DEFRQ(Class<T> returnType, Object[] methodArgs) {
		if (methodArgs.length == 0) {
			throw new FIllegalArgumentException("Impossible get defRq arg from empty method signature.");
		}
		Object last = ARRi.last(methodArgs);
		if (last == null) {
			if (returnType.isPrimitive()) {
				throw new FIllegalArgumentException("Error declare defRq type. Return Type is primitive class [%s] and MethodArgs=null", returnType.getName());
			}
			return null;
		}
		Class class0 = last.getClass();
		if (class0.isArray() && class0.getComponentType() == returnType) {
			Object[] lastArr = ((Object[]) last);
			if (lastArr.length == 0) {
				throw new FIllegalArgumentException("Impossible get defRq arg from empty array.");
			}
			Object arg = ARRi.first(lastArr);
			return cast(arg, returnType);
		}
		throw new FIllegalArgumentException("Is not defRq arg, type [%s]", class0.getName());
	}

	public static boolean hasLastDefRq(Method method, Object[] methodArgs) {
		return getRequiredContractValue_DefRq(method, methodArgs) != null;
	}

	public static <T> Pare<Boolean, T> getRequiredContractValue_DefRq(Method method, Object[] methodArgs) {
		Class returnType = method.getReturnType();
		Class[] params = method.getParameterTypes();
		if (params.length == 0) {
			return null;
		}
		Class lastParamType = ARRi.last(params);
		if (!lastParamType.isArray()) {
			return null;
		}
		IT.state(returnType.isAssignableFrom(lastParamType.getComponentType()), "Method [%s] has last PARAM type [%s], but except type assignable from defRq type as [%s]", method.getName(), returnType, lastParamType.getClass().getComponentType());
		Object lastArg = ARRi.last(methodArgs, null);
		if (lastArg == null) {
			return (Pare<Boolean, T>) Pare.of(false, null);
		}
		IT.state(lastArg.getClass().isArray(), "last method params except array type");
		IT.state(lastArg.getClass().getComponentType().isAssignableFrom(lastParamType.getComponentType()), "Method [%s] has last ARG type [%s], but except type assignable from defRq type as [%s]", returnType, lastParamType.getClass().getComponentType());
		Object[] lastDefRq = (Object[]) lastArg;
		if (lastDefRq.length == 0) {
			return Pare.of(true, null);
		}
		return (Pare<Boolean, T>) Pare.of(false, lastDefRq[0]);
	}

	public static boolean isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(Method method) {
		return isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(method.getName(), method.getReturnType(), method.getParameterTypes());
	}

	public static boolean isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(String methodname, Class returnType, Class[] params) {
		boolean isRequired = true;
		if (params.length == 0) {
			isRequired = true;
		} else if (params.length > 1) {
			throw new FIllegalArgumentException("Check parameter's of method ::: %s(args=%s) ::: method need empty args or single [...arg] type (if need set the default value", methodname, params.length);
		} else if (params[0].isArray() && params[0].getComponentType() == returnType) {
			isRequired = false;
		} else {
			throw new FIllegalArgumentException("Check parameter's of method ::: %s(args=%s) ::: need class [%s] vs specific [%s] ", methodname, params.length, returnType, params[0]);
		}
		return isRequired;
	}

	public static Object returnNull(Method method, boolean checkIsRequiredContractArg) {
		if (checkIsRequiredContractArg) {
			throwIfRequiredContractValue(method);
		}
		return checkAssignableValue(method, null);
	}

	public static Object checkAssignableValue(Method method, Object val) {
		Class returnType = method.getReturnType();
		if (val == null) {
			if (returnType.isPrimitive()) {
				throw new CMethodException(method, "Return Value is NULL, not return type is primitive");
			}
			return val;
		}
		if (!returnType.isAssignableFrom(val.getClass())) {
			throw buildNewErrorNotAssignableReturnTypes(method, val);
		}
		return val;
	}

	public static CMethodException buildNewErrorNotAssignableReturnTypes(Method method, Object returnVal) {
		return new CMethodException(method, "Returned Value type [%s] not assignable for method", returnVal.getClass().getName());
	}

	public static Object throwIfRequiredContractValue(Method method) {
		if (isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(method)) {
			throw new CMethodException(method, "Return value [%s] is required.", method.getReturnType().getName());
		}
		return null;
	}

	public static <T> T cast(Object obj, Class<T> type, boolean... wrapPrimitive) {
		if (obj == null) {
			if (type.isPrimitive()) {
				if (ARG.isDefEqTrue(wrapPrimitive)) {
					return (T) ObjTo.getInitValueForPrimitive(type);
				}
				throw new FIllegalArgumentException("Value is NULL could't be primitive class [%s]", type.getName());
			}
			return null;
		}
		if (!type.isAssignableFrom(obj.getClass())) {
			throw new FIllegalArgumentException("Object Class [%s] not assignable for Required Class [%s]", obj.getClass().getName(), type.getName());
		}
		return type.cast(obj);
	}

	public static Object getObjectValue_DefRq(Map<String, Object> data, Method method, Object[] methodArgs, boolean keyAsIs) {
		String methodName = method.getName();

		String key = getKeyNameFromGetMethodName(methodName, keyAsIs);
		Object val = data.get(key);
		if (val != null) {
			return val;
		}

		boolean hasKey = data.containsKey(key);
		if (hasKey) {
			return val;
		}

		Pare<Boolean, Object> defRqObj = getRequiredContractValue_DefRq(method, methodArgs);
		boolean allowNoKey = defRqObj == null;

		if (allowNoKey) {
			return val;
		} else if (defRqObj.key()) {
			throw new RequiredRuntimeException("Contract data is required key [%s] (key not found)", key);
		} else {
			return defRqObj.val();
		}
//			boolean isRequired = defRqObj != null;
//			val = data.get(key);
//			if (X.emptyObjOrStr(val)) {
//				if (isRequired) {
//					throw new MapTableContractException("Data contract has value is NULL by key [%s] from method [%s], data[%s]. May be need mark method by ..notRq?", key, methodName, data);
//				}
//				if (Number.class.isAssignableFrom(method.getReturnType()) || Boolean.class.isAssignableFrom(method.getReturnType())) {
//					return null;
//				}
//
//			} else {
//				val = UMap.getAs(data, key, method.getReturnType(), methodArgs);
////				if (methodArgs != null && methodArgs.length > 0) {
////					val = UMap.getAs(data, key, method.getReturnType(), methodArgs[0]);
////				} else {
////					val = UMap.getAs(data, key, method.getReturnType());
////				}
//			}
//			return val;
	}

	public static Object getObjectValue_MarkNotRq(Map<String, Object> data, Method method, Object[] methodArgs, boolean keyAsIs) {
		String methodName = method.getName();
		Object val;
		boolean isRequired = isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(method);
		String key = getKeyNameFromGetMethodName(methodName, keyAsIs);
		val = data.get(key);
		if (X.emptyObj_Str(val)) {
			if ("".equals(val)) {
				return "";
			} else if (isRequired) {
				throw new MapTableContract.MapTableContractException("Data contract has value is NULL by key [%s] from method [%s], data[%s]. May be need mark method by ..notRq?", key, methodName, data);
			}
			if (Number.class.isAssignableFrom(method.getReturnType()) || Boolean.class.isAssignableFrom(method.getReturnType())) {
				return null;
			}
		} else {
			if (methodArgs != null && methodArgs.length > 0) {
				val = MAP.getAs(data, key, method.getReturnType(), methodArgs[0]);
			} else {
				val = MAP.getAs(data, key, method.getReturnType());
			}
		}
		return val;
	}

	public static String getKeyNameFromGetMethodName(String methodName, boolean keyAsIs) {
		String key = methodName.substring(3);
		if (X.empty(key)) {
			throw new MapTableContract.MapTableContractException("Methodname [%s] must have key after prefix 'get'", methodName);
		}
		if (!Character.isLetter(key.charAt(0))) {
			throw new MapTableContract.MapTableContractException("Var name must start with letter method name [%s] / var [%s] ", methodName, key);
		}
		if (!Character.isUpperCase(key.charAt(0))) {
			throw new MapTableContract.MapTableContractException("Var name must start with upper case letter method name [%s] / var [%s] ", methodName, key);
		}
		key = keyAsIs ? key : STR.decapitalize(key);
		return key;
	}
}
