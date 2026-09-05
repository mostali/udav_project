package nett;


import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import mpc.exception.EException;

public class TgException extends EException {
	public enum EError {
		SUCCESS, ERROR_OLD_WEB_HOOK, PROXYEND, PROXYENDLONG;

		public void ON() throws TgException {
			throw I();
		}

		public TgException I() {
			return new TgException(this);
		}

		public TgException I(Exception ex) {
			return new TgException(this, ex);
		}
	}

	private static final long serialVersionUID = 1L;

	public TgException(EError error) {
		super(error);
	}

	public TgException(EError error, Throwable cause) {
		super(error, cause);
	}

	public static boolean isOldWebHook(TelegramApiException e) {
		if (e instanceof org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException) {
			org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException te = (org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException) e;
			if ("Error removing old webhook".equals(e.getMessage())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isConnectTimeOut(Throwable c) {
		if (c instanceof java.net.SocketTimeoutException) {
			java.net.SocketTimeoutException se = (java.net.SocketTimeoutException) c;
			if ("Connect timed out".equals(se.getMessage())) {
				return true;
			}
		} else if (c instanceof java.net.SocketException) {
			java.net.SocketException se = (java.net.SocketException) c;
			if ("connect timed out".equals(se.getMessage())) {
				return true;
			}
		}
		return false;
	}

	public static void printRecallMessage(String message, int tc) {
		ApiTg.L.warn("printRecallMessage::" + tc + "::" + message);

	}
}
