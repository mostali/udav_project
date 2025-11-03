package mpz_deprecated.contractregex;

import lombok.Getter;
import mpc.exception.FIllegalStateException;
import mpc.exception.ICleanMessage;
import mpu.X;
import mpu.str.UST;
import mpu.IT;
import mpf.contract.DefObjMethod;
import mpc.rfl.R;
import mpf.contract.UContract;
import mpc.rfl.RFL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
public interface ContractRx {

//	String _ = "\\s";
	String S1N = "\\s+";
	String __ = S1N;
	String S01 = "\\s?";
	String _1 = S01;
	String S0N = "\\s*";
	String _N = S0N;

	String W01 = "(\\s*(?<w0>\\w+)\\s+)?(?<w1>\\w+)";
	String W0 = "(?<w0>\\w+)";
	String W1 = "(?<w1>\\w+)";
	String W2 = "(?<w2>\\w+)";

	Map __map();

	public static class CrxException extends FIllegalStateException implements ICleanMessage {
		public CrxException() {
		}

		private String cleanMessage;

		@Override
		public String getCleanMessage() {
			return cleanMessage == null ? getMessage() : cleanMessage;
		}

		public CrxException(Boolean isClean, String message) {
			super(message);

		}

		public CrxException(String message) {
			super(message);
		}

		public CrxException(String cleanMessage, String message, Object... args) {
			super(message, args);
			this.cleanMessage = cleanMessage;
		}

		public CrxException(String message, Object... args) {
			super(message, args);
		}

		public CrxException(Throwable throwable) {
			super(throwable);
		}

		public CrxException(Throwable throwable, String message) {
			super(throwable, message);
		}

		public CrxException(Throwable throwable, String message, Object... args) {
			super(throwable, message, args);
		}
	}

	public static String rx(Object... args) {
		return Stream.of(args).map(X::toString).collect(Collectors.joining("\\s+"));
	}

	String METHOD_MAP = "__map";

	static <T> T buildContract(String data, Pattern rx, Class<T> contractClass) {
		return (T) Proxy.newProxyInstance(
				ContractRx.class.getClassLoader(),
				new Class[]{contractClass}, new GetAndDefMapInvocationHandler(data, rx, contractClass)
		);
	}

	 static Map toMap(ContractRx crx, Class contractClass) {
//		if (methodName.equals(METHOD_MAP)) {
		Map map = new HashMap<>();
//		Map map = new LinkedHashMap<>();
//		Map map = new TreeMap<>();
		Method[] methods = contractClass.getMethods();
		for (Method m : methods) {
			if (RFL.isStatic(m)) {
				continue;
			}
			switch (m.getName()) {
				case METHOD_MAP:
//				case METHOD_BUILD_CONTRACT:
//				case METHOD_MAIN:
					continue;
			}
			String key = m.getName();
			Object val = null;
			if (UContract.isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(m)) {
				val = RFL.invokeSimple(crx, key);
			} else {
				Class[] params = m.getParameterTypes();
				val = RFL.invoke(crx, key, new Class[]{params[0]}, new Object[]{null});
			}

			map.put(key, val);
		}
//		}
		return map;
	}

	public static class GetAndDefMapInvocationHandler implements InvocationHandler {

		@Getter
		private final Pattern regex;
		private final Matcher matcher;

		private final Class contractClass;

//		public GetAndDefMapInvocationHandler(String data, Pattern regex) {
//			this(data, regex, null);
//		}

		public GetAndDefMapInvocationHandler(String data, Pattern regex, Class contractClass) {
			this.regex = regex;
			matcher = regex.matcher(data);
			if (!matcher.matches()) {
				throw new CrxException("Ошибка запроса", "Mathes't [%s] / RX [%s]", data,regex);
			}
			this.contractClass = IT.notNull(contractClass);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] methodArgs) {
			DefObjMethod nativeObject = DefObjMethod.of(method, null);

			if (nativeObject != null) {
				switch (nativeObject) {
					case toString:
						return toMap((ContractRx) proxy, contractClass).toString();
					default:
//						throw new NI(nativeObject);
				}
			}

			String methodName = method.getName();

			if (METHOD_MAP.equals(methodName)) {
				return toMap((ContractRx) proxy, contractClass);
			}
			boolean isRequired = UContract.isRequiredContractValue_IF_PRESENT_SINGLY_SAMETYPE_ARRAY(method);
			try {
				String vl = matcher.group(methodName);
				if (vl == null) {
					if (isRequired) {
						throw new CrxException("Ошибка запроса", "Contract name '%s' value is NULL (required)", methodName);
					}
					return vl;
				}
				return UST.strTo(vl, method.getReturnType());
			} catch (IllegalArgumentException ex) {
				String no_group_with_name = "No group with name";
				if (ex.getMessage().startsWith(no_group_with_name)) {
					if (!isRequired) {
						return null;
					}
				}
				String replace = ex.getMessage().substring(no_group_with_name.length()).replace("<", "'").replace(">", "'");
				throw new CrxException(ex, no_group_with_name + replace);
			} catch (Exception ex) {
				throw new CrxException(ex, "Proxy Class [%s] method [%s] throws error", R.cn(contractClass), methodName);
			}
		}

	}


}
