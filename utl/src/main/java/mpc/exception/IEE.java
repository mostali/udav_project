package mpc.exception;

import mpu.core.ARG;
import mpu.core.ENUM;
import mpe.core.ERR;
import mpu.str.JOIN;
import mpu.X;

import java.util.List;

public interface IEE<E> {

	static <T> T convertTo(Throwable t, Class<T> type, T... defRq) {
		return type.isAssignableFrom(t.getClass()) ? type.cast(t) : ARG.toDefThrow(new RequiredRuntimeException("Throwable '%s' is not compatible type '%s'", t.getClass().getSimpleName(), type.getSimpleName()), defRq);
	}

	static String getPfxMessageOr(Exception exception, String defMsg) {
		return isEE(exception) ? ((IEE) exception).type().name() + ": " : defMsg;
	}

	static boolean isEE(Exception exception) {
		return exception instanceof IEE;
	}

	<E extends Enum> E type();

	default <E extends Exception> E eerr() {
		return (E) this;
	}

	default <E extends Enum> boolean is(E error) {
		return type() == error;
	}

	default boolean is(Throwable err, boolean... ignoreCase) {
		return err instanceof EException && ENUM.eq(((EException) err).type(), type(), ignoreCase);
	}

	public default String getMessageAny() {
		Exception err = eerr();
		String msg = ERR.getCauseMessageOr(err.getCause(), null, null);
		return msg == null ? err.getMessage() : msg;
	}

	static <E extends Enum> boolean is(Throwable err, Enum type, boolean... ignoreCase_or_typeEq) {
		return err instanceof EException && ENUM.eq(((EException) err).type(), type, ignoreCase_or_typeEq);
	}

	static String getCleanMessage(IEE error, boolean... any) {
		ICleanMessage clnMsg = ERR.getCleanMessageType((Throwable) error);
		if (clnMsg != null) {
			return clnMsg.getCleanMessage();
		}
		if (ARG.isDefNotEqTrue(any)) {
			return null;
		}
		String msg = ERR.getCauseMessageOrMessage((Throwable) error);
		return msg != null ? msg : error.type().name();
	}

	static String getMessage(IEE iee, String messageFromSuper) {
		String clnMsg = getCleanMessage(null);
		if (clnMsg != null) {
			return clnMsg;
		}
		Throwable ee = (Throwable) iee;
		Throwable cause = ee.getCause();
		return cause == null ? messageFromSuper : cause.getMessage();
	}

	static String getMessage_OLD(IEE iee, String parentMessage) {
		String clnMsg = getCleanMessage(null);
		if (clnMsg != null) {
			return clnMsg;
		}
		Throwable ee = (Throwable) iee;
		String mmsg = iee.type().name().equals(parentMessage) ? parentMessage : iee.type() + "/" + parentMessage;
		return ee.getCause() == null ? mmsg : X.empty(ee.getCause().getMessage()) ? mmsg : iee.type() + "/" + ee.getCause().getMessage();
	}

	static String toString(IEE iee, String parentMessage) {
		Throwable ee = (Throwable) iee;
		String pfx = iee instanceof EException ? "EE" : "EER";
		String msgt = String.valueOf(iee.type());
		String part1 = msgt.equals(parentMessage) ? "" : ":" + parentMessage;
		String part2 = "";
		if (ee.getCause() != null && X.notEmpty(ee.getCause().getMessage()) && !msgt.equals(ee.getCause().getMessage())) {
			List<String> errors = ERR.getAllMessages(ee.getCause());
			part2 = "/" + JOIN.allBy("/", errors);
		}
		return pfx + ":" + msgt + part1 + part2;
	}

	default <T> T getCause(Class<T> typeOf, T... defRq) {
		return getCauseOfType((Throwable) this, typeOf, defRq);
	}

	static <T> T getCauseOfType(Throwable err, Class<T> typeOf, T... defRq) {
		Throwable cause = err.getCause();
		if (typeOf.isAssignableFrom(cause.getClass())) {
			return typeOf.cast(cause);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		return ARG.toDef(defRq);
	}
}
