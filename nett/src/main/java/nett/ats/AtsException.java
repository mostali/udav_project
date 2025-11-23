package nett.ats;

import mpc.exception.*;
import mpu.X;
import mpu.core.ARG;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Arrays;

public class AtsException extends ERxception {

	public static final String ERR_MSG_CANT_PARSE_ENTITY = "Bad Request: can't parse entities:";
	public static final String ERR_MSG_CONT_FIND_END = "Can't find end of the entity starting";
//	public static final String ERR_MSG_CONT_FIND_END = "Too Many Requests: retry after 8\n";

	//	public AtsException(Throwable cause) {
//		super(cause);
//	}
//
//	public AtsException(String message, Throwable cause) {
//		super(message, cause);
//	}
//

	public static AtsException of(Exception ex, boolean... checkSelf) {
		if (ARG.isDefEqTrue(checkSelf) && ex instanceof AtsException) {
			return (AtsException) ex;
		} else if (!(ex instanceof TelegramApiRequestException)) {
			return new AtsException(EE.NOSTATUS, ex);
		}
		TelegramApiRequestException tex = (TelegramApiRequestException) ex;
		Integer errorCode = tex.getErrorCode();
		if (errorCode == null) {
			return EE.NOCODE.I(ex);
		}
		switch (errorCode) {
			case 413:
				return EE.TOO_LARGE.I(ex);
			default:
				if (isMsgWrong400(ex, false)) {
					return EE.WRONGMSG.I(ex);
				}
				return EE.UNDEFINED.I(ex);
		}
	}

	public static boolean isMsgWrong400(Throwable ex, boolean checkCause) {
		return isMsgWrong400IfHasAnyMsg(ex, checkCause, ERR_MSG_CANT_PARSE_ENTITY, ERR_MSG_CONT_FIND_END);
	}

	public static boolean isMsgWrong400IfHasAnyMsg(Throwable ex, boolean checkCause, String... msgs) {
		switch (msgs.length) {
			case 0:
				throw new FIllegalStateException(ex, "Set messages for check error");
			case 1:
				boolean has = ex instanceof TelegramApiRequestException && ex.getMessage().contains(msgs[0]);
				return has || (checkCause && ex.getCause() != null && isMsgWrong400(ex.getCause(), false));
			default:
				return Arrays.stream(msgs).anyMatch(m -> isMsgWrong400IfHasAnyMsg(ex, checkCause, m));

		}

	}

	///
	//
	//

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */


	@Override
	public EE type() {
		return super.type(EE.class);
	}

	public enum EE {
		NOSTATUS, //
		NOCODE, //no http code
		UNDEFINED, //not found known case
		TOO_LARGE, WRONGMSG;

		public AtsException I() {
			return new AtsException(this);
		}

		public AtsException I(Throwable ex) {
			AtsException er = new AtsException(this, ex);
			return er;
		}

		public AtsException I(String message) {
			AtsException er = new AtsException(this, new SimpleMessageRuntimeException(message));
			return er;
		}

		public AtsException I(String message, Object... args) {
			AtsException er = new AtsException(this, new SimpleMessageRuntimeException(X.f(message, args)));
			return er;
		}

		public AtsException M(String message, Object... args) {
			AtsException er = new AtsException(this, new CleanMessageRuntimeException(X.f(message, args)));
			return er;
		}
	}

	public AtsException() {
		super(EE.NOSTATUS);
	}

	public AtsException(EE error) {
		super(error);
	}

	public AtsException(EE error, Throwable cause) {
		super(error, cause);
	}
}
