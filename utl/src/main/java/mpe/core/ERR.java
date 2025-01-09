package mpe.core;

import mpc.env.APP;
import mpc.exception.MultiCauseExceptionExt;
import mpc.exception.MultipleMessageException;
import mpe.str.StringWalkBuilder;
import mpu.X;
import mpu.IT;
import mpu.core.ARG;
import mpc.exception.ICleanMessage;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpu.str.STR;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//Error Utility
public class ERR {

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void _throwException(Throwable exception) throws T {
		throw (T) exception;
	}

	public static void throwException(Throwable exception) {
		ERR.<RuntimeException>_throwException(exception);
	}

	public static String getStackTraceWithoutPrefixClass(Throwable rootException, Class... removePrefixClasses) {
		String message = ExceptionUtils.getStackTrace(rootException);
		if (!X.empty(removePrefixClasses)) {
			String messageAfter = null;
			do {
				messageAfter = message;
				for (Class prefix : removePrefixClasses) {
					String name = prefix.getName() + ":";
					message = STR.removeStartString(message, name, false).trim();
				}
			} while (!message.equals(messageAfter));//recursive
		}
		return message;
	}

	public static <E extends Throwable> E getErrorCauseInstance(Throwable error, Class<E> type, boolean... required) {
		if (error != null) {
			E e = getInstanceAsType(error.getCause(), type, required);
			if (e != null) {
				return e;
			}
		}
		if (error == null) {
			throw new RequiredRuntimeException("Error need cause type [%s]. But instance of error is NULL.", type);
		}
		if (ARG.isDefEqTrue(required)) {
			throw new RequiredRuntimeException("Error need cause type [%s]. But instance has cause type [%s].", type, error.getCause());
		}
		return null;
	}

	private static <T> T getInstanceAsType(Object instance, Class<T> type, boolean... required) {
		if (instance != null && type.isAssignableFrom(instance.getClass())) {
			return type.cast(instance);
		} else if (ARG.isDefEqTrue(required)) {
			if (instance == null) {
				throw new RequiredRuntimeException("Object need type [%s]. But instance is NULL.", type);
			}
			throw new RequiredRuntimeException("Object need type [%s]. But instance class is [%s].", type, instance.getClass());
		}
		return null;
	}

	public static <E extends Throwable> E getErrorInstanceRekursive(Throwable error, Class<E> type, boolean... required) {
		Throwable copy = error;
		do {
			Throwable err = getErrorInstance(error, false, type);
			if (err != null) {
				return type.cast(err);
			}
			error = error.getCause();
			if (error == null) {
				break;
			}
		} while (true);
		if (ARG.isDefEqTrue(required)) {
			throw new RequiredRuntimeException("Error|Cause need type [%s]. Recursive analyze [%s] useless", type, copy.getClass());
		}
		return null;
	}

	public static <E extends Throwable> E getErrorInstance(Throwable error, boolean checkCause, Class<E> type, boolean... required) {
		if (error != null) {
			if (type.isAssignableFrom(error.getClass())) {
				return type.cast(error);
			} else if (checkCause) {
				return getErrorCauseInstance(error, type, required);
			}
		}
		if (ARG.isDefEqTrue(required)) {
			if (error == null) {
				throw new RequiredRuntimeException("Error|Cause need type [%s]. But instance of error is NULL.", type);
			}
			throw new RequiredRuntimeException("Error need type [%s]. But instance has type [%s].", type, error.getClass());
		}
		return null;
	}

	public static String getMessageWithoutPrefixClass(Throwable rootException, Class... removePrefixClasses) {
		String message = rootException.getMessage();
		if (!X.empty(removePrefixClasses)) {
			String messageAfter = null;
			do {
				messageAfter = message;
				for (Class prefix : removePrefixClasses) {
					String name = prefix.getName() + ":";
					message = STR.removeStartString(message, name, false).trim();
				}
			} while (!message.equals(messageAfter));//recursive
		}
		return message;
	}

	public static String getCauseMessageOrMessage(Throwable error) {
		if (error.getCause() != null) {
			return error.getCause().getMessage();
		} else {
			return error.getMessage();
		}
	}

	public static ICleanMessage getCleanMessageType(Throwable error) {
		if (error == null) {
			return null;
		} else if (error instanceof ICleanMessage) {
			return ((ICleanMessage) error);
		} else if (error.getCause() != null && error.getCause() instanceof ICleanMessage) {
			return ((ICleanMessage) error.getCause());
		}
		return null;
	}

	public static boolean endsWith(SQLException ex, boolean checkCase, String sfx) {
		return ex != null && (ex.getMessage().endsWith(sfx) || (checkCase && ex.getCause() != null && ex.getCause().getMessage().endsWith(sfx)));
	}

	public static String getMessageWithType(Throwable ex) {
		return ex.getClass().getSimpleName() + ":" + ex.getMessage();
	}

//	public static String getMessageWithType(Throwable ex, String del, boolean... includeCause) {
//		StringBuilder sb = new StringBuilder(RFL.scn(ex) + ":" + ex.getMessage());
//		Throwable t = null;
//		if (ARG.isDefEqTrue(includeCause)) {
//			t = ex;
//			while (((t = t.getCause()) != null)) {
//				sb.append(del);
//				sb.append(">").append(getMessageWithType(t, del, false));
//			}
//		}
//		return sb.toString();
//	}

	public static String getMessagesAsStringWithHead(Collection<Throwable> errors, String head, boolean... ol) {
		StringWalkBuilder<Throwable> msgBuilder = StringWalkBuilder.of(Throwable::getMessage);
		if (head != null) {
			msgBuilder = msgBuilder.pfxMsg(head + STR.NL);
		}
		if (ARG.isDefEqTrue(ol)) {
			msgBuilder = msgBuilder.ol();
		}
		return msgBuilder.buildSB(errors).toString();
	}

	public static String getMessagesAsString(Collection<Throwable> errors, String del) {
		IT.notEmpty(errors);
		StringBuilder sb = new StringBuilder();
		for (Throwable t : errors) {
			sb.append(t.getMessage()).append(del);
		}
		return sb.substring(0, sb.length() - del.length());
	}

	public static List<String> getStackTraceLines(Throwable error) {
		List<String> collect = Arrays.stream(error.getStackTrace()).map(e -> e.toString()).collect(Collectors.toList());
		return collect;
		//return US.SPLIT.splitFastLines(getStackTrace(error));
	}

	public static String getStackTrace(Throwable error) {
		return ExceptionUtils.getStackTrace(error);
	}

	public static List<String> getAllMessages(Throwable cause) {
		Throwable t = cause;
		List<String> msgs = new ArrayList<>();
		while (t != null) {
			msgs.add(t.getMessage());
			t = t.getCause();
		}
		return msgs;
	}

	public static String getCauseMessageOr(Throwable error, String returnIfCauseNull) {
		if (error.getCause() == null) {
			return returnIfCauseNull;
		}
		return error.getCause().getMessage();
	}

	public static String getCauseMessageOr(Throwable error, String returnIfCauseNull, String returnIfCauseMsgEmpty) {
		if (error.getCause() == null) {
			return returnIfCauseNull;
		}
		String message = error.getCause().getMessage();
		return X.notEmpty(message) ? message : returnIfCauseMsgEmpty;
	}

	public static List<String> getAllMessagesFromError(Throwable throwable) {
		List<String> result = new ArrayList<String>();
		while (throwable != null) {
			result.add(getMessageOr(throwable, "NULL"));
			throwable = throwable.getCause();
		}
		return result; //["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
	}

	public static String getMessageOr(Throwable ex, String causeNull) {
		return ex == null || ex.getMessage() == null ? causeNull : ex.getMessage();
	}

	public static String toStringRoleMode(Throwable ex, int... length) {
		if (APP.IS_PROM_ENABLE) {
			return "Error";
		} else if (APP.IS_DEBUG_ENABLE) {
			String str = ex.getMessage() + STR.NL + ERR.getStackTrace(ex);
			return STR.substrIf(str, length);
		}
		return ex.getMessage();
	}

//	public static Exception multiCause(List<Throwable> errs) {
//		return new MultiCauseExceptionExt(errs);
//	}

	public static Exception multiCause(List<Throwable> errs) {
		return new Exception(errs.stream().map(ERR::getStackTrace).collect(Collectors.joining(STR.NL)));
	}
}

