package nett.ats;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class AtsException extends RuntimeException {
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

	public static boolean isMsgHasWrongSymbols(Exception ex) {
		return ex.getMessage().contains("Can't find end of the entity starting");
	}
}
