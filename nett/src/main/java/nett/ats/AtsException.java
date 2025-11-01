package nett.ats;

import mpc.exception.FIllegalStateException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Arrays;

public class AtsException extends RuntimeException {

	public static final String ERR_MSG_CANT_PARSE_ENTITY = "Bad Request: can't parse entities:";
	public static final String ERR_MSG_CONT_FIND_END = "Can't find end of the entity starting";
//	public static final String ERR_MSG_CONT_FIND_END = "Too Many Requests: retry after 8\n";

	public AtsException(Throwable cause) {
		super(cause);
	}

	public AtsException(String message, Throwable cause) {
		super(message, cause);
	}

	public static AtsException of(TelegramApiException ex) {
		if (ex instanceof TelegramApiRequestException) {
			return new AtsException(((TelegramApiRequestException) ex).getApiResponse(), ex);
		}
		return new AtsException(ex);
	}

	public static boolean isMsgWrong400(Throwable ex, boolean checkCause) {
		return isMsgWrong400IfHasMsg(ex, checkCause, ERR_MSG_CANT_PARSE_ENTITY, ERR_MSG_CONT_FIND_END);
	}

	public static boolean isMsgWrong400IfHasMsg(Throwable ex, boolean checkCause, String... msgs) {
		switch (msgs.length) {
			case 0:
				throw new FIllegalStateException(ex, "Set messages for check error");
			case 1:
				boolean has = ex instanceof TelegramApiRequestException && ex.getMessage().contains(msgs[0]);
				return has || (checkCause && ex.getCause() != null && isMsgWrong400(ex.getCause(), false));
			default:
				return Arrays.stream(msgs).anyMatch(m -> isMsgWrong400IfHasMsg(ex, checkCause, m));

		}

	}
}
