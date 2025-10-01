package mpc.map;

import com.google.gson.JsonObject;
import mpu.X;
import mpc.exception.FIllegalStateException;
import mpc.json.UGson;
import mpc.str.ObjTo;
import mpf.contract.CMethodException;
import mpf.contract.UContract;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import mpu.core.ARG;
import mpu.IT;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;

@RequiredArgsConstructor
public class MapTableContract<T> {

//	interface IMapTestContract {
//		String getK1();
//
//		Long getK2();
//
//		String getK3();
//
//		String getK4(String... markNotRq);
//
//	}

//	public static void main(String[] args) throws SQLException {
	//test();
//	}

//	private static void test() {
//		String patternMap = "k1=v1;k2=8;";
//		IMapTestContract c = MapTableContract.buildContract(UMap.mapOf(patternMap), IMapTestContract.class);
//		String patternMapTotal = "k1=%s;k2=%s;";
//		patternMapTotal = U.f(patternMapTotal, c.getK1(), c.getK2());
//		try {
//			String k3 = c.getK3();
//			throw new IllegalStateException("error getting");
//		} catch (MapTableContractException e) {
//			P.w(e);
//		}
//
//		String v4 = c.getK4();//OK
//		P.p(v4);//null
//
//		String v4_2 = c.getK4("");//OK
//		P.p(v4_2);//null
//		//
//		//
//		UC.state(patternMap.equals(patternMapTotal), "not equals", patternMapTotal);
//	}


	public static <T> T buildContract_MarkNotRq(Map data, Class<T> contractClass) {
		return (T) Proxy.newProxyInstance(MapTableContract.class.getClassLoader(), new Class[]{contractClass}, new GetAndDefMapInvocationHandler(data, contractClass, false));
	}

	public static <T> T buildContract_DefRq(Map data, Class<T> contractClass) {
		return buildContract_DefRq(data, contractClass, false);
	}

	public static <T> T buildContract_DefRq(Map data, Class<T> contractClass, boolean keyAsIs) {
		return (T) Proxy.newProxyInstance(MapTableContract.class.getClassLoader(), new Class[]{contractClass}, new GetAndDefMapInvocationHandler(data, contractClass, true, keyAsIs));
	}

	public static <T> T buildContract_DefRq(JsonObject data, Class<T> contractClass, boolean keyAsIs) {
		return (T) Proxy.newProxyInstance(MapTableContract.class.getClassLoader(), new Class[]{contractClass}, new GetAndDefMapInvocationHandler(data, contractClass, true, keyAsIs));
	}

	public static class GetAndDefMapInvocationHandler implements InvocationHandler {

		public static final String METHOD_CLEAN_MAP = "mapc";
		public static final String METHOD_ALL_MAP = "mapdb";
		@Getter
		private final Map<String, Object> data;
		private JsonObject jsonData;
		private final Class contractClass;
		private final boolean isDefRqSignMode;
		private final boolean keyAsIs;

		public JsonObject json() {
			return jsonData;
		}

		public GetAndDefMapInvocationHandler(JsonObject data, Class contractClass, boolean isDefRqSignMode, boolean keyAsIs) {
			this((Map) UGson.toMapFromJO(data), contractClass, isDefRqSignMode, keyAsIs);
			this.jsonData = data;
		}

		public GetAndDefMapInvocationHandler(Map<String, Object> data, Class contractClass, boolean isDefRqSignMode) {
			this(data, contractClass, isDefRqSignMode, false);
		}

		public GetAndDefMapInvocationHandler(Map<String, Object> data, Class contractClass, boolean isDefRqSignMode, boolean keyAsIs) {
			this.data = IT.notEmpty(data, "Data contract is empty");
			this.contractClass = IT.notNull(contractClass);
			this.isDefRqSignMode = isDefRqSignMode;
			this.keyAsIs = keyAsIs;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
			String methodName = method.getName();
			if (METHOD_ALL_MAP.equals(methodName)) {
				return data;
			} else if (METHOD_CLEAN_MAP.equals(methodName)) {
				return getCleanObjectMap(new Object[0]);
			}
			if (methodName.length() < 3) {
				throw new MapTableContractException("Method name [%s] less 3 symbols", methodArgs);
			}
			Class<?> returnType = method.getReturnType();
			if (method.isDefault()) {
				//https://stackoverflow.com/questions/37812393/how-to-explicitly-invoke-default-method-from-a-dynamic-proxy
				MethodHandle methodHandle = MethodHandles.lookup().findSpecial(contractClass, method.getName(), MethodType.methodType(returnType, method.getParameterTypes()), contractClass).bindTo(proxy);
				Object rslt = methodHandle.invokeWithArguments(methodArgs);
				return rslt;
			} else if (methodName.startsWith("get")) {
				Object val = X.empty(_cleanMap) ? getObjectValue(method, methodArgs) : _cleanMap.get(UContract.getKeyNameFromGetMethodName(methodName, keyAsIs));
				if (val == null) {
					return returnType.isPrimitive() ? ObjTo.getInitValueForPrimitive(returnType) : null;
				} else if (val instanceof CharSequence && ((CharSequence) val).length() == 0 && Number.class.isAssignableFrom(returnType)) {
					return 0;
				}
				return ObjTo.objTo(val, returnType);
			} else if ("toString".equals(methodName)) {
				Map build = getCleanObjectMap(new Object[0]);
//				Map build = data;
				return build.toString();
			} else if ("hashCode".equals(methodName)) {
				Map build = getCleanObjectMap(new Object[0]);
				return Objects.hashCode(build);
			} else if ("equals".equals(methodName)) {
				if (methodArgs.length != 1) {
					throw new MapTableContractException("Call equals method with args!=1 [%s]", Arrays.asList(methodArgs));
				}
				if (methodArgs[0] == null || !contractClass.isAssignableFrom(methodArgs[0].getClass())) {
					return false;
				}
				return methodArgs[0].toString().equals(proxy.toString());//getCleanMap(data)
			} else {
				throw new CMethodException(method, "unsupported");
			}
		}

		private Object getObjectValue(Method method, Object[] methodArgs) {
			return isDefRqSignMode ? UContract.getObjectValue_DefRq(data, method, methodArgs, keyAsIs) : UContract.getObjectValue_MarkNotRq(data, method, methodArgs, keyAsIs);
		}

		private Map _cleanMap = null;

		@NotNull
		private Map getCleanObjectMap(Object[] methodArgs, boolean... reinit) {
			if (_cleanMap == null || ARG.isDefEqTrue(reinit)) {
				_cleanMap = new HashMap();
				for (Method method : contractClass.getMethods()) {
					if (Modifier.isStatic(method.getModifiers()) || method.isDefault()) {
						continue;
					}
					if (method.getName().equals(METHOD_CLEAN_MAP) || method.getName().equals(METHOD_ALL_MAP)) {
						continue;
					}
					if (method.getName().startsWith("get")) {
						String key = UContract.getKeyNameFromGetMethodName(method.getName(), keyAsIs);
						Object val = getObjectValue(method, methodArgs);
						_cleanMap.put(key, val);
					}
				}
			}
			return _cleanMap;
		}


	}


	public static class MapTableContractException extends FIllegalStateException {
		public MapTableContractException() {
			super();
		}

		public MapTableContractException(String message) {
			super(message);
		}

		public MapTableContractException(String message, Object... args) {
			this(String.format(message, args));
		}

		public MapTableContractException(Throwable throwable, String message) {
			super(message, throwable);
		}

		public MapTableContractException(Throwable throwable, String message, Object... args) {
			this(throwable, String.format(message, args));
		}
	}


}