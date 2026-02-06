package mpc.exception;

import mpu.core.ENUM;
import mpu.X;
import mpc.str.sym.SYMJ;
import mpu.pare.Pare;

public class NotifyMessageRtException extends ERxception {

	public String icon() {
		switch (type()) {
			case GREEN:
				return SYMJ.OK_GREEN;
			case BLUE:
				return SYMJ.WARN;
			case RED:
				return SYMJ.FAIL_RED_THINK;
			case LOG:
				return SYMJ.CLIPBOARD;
			default:
				throw new WhatIsTypeException(type());
		}
	}

	public String namehu() {
		switch (type()) {
			case GREEN:
				return "Ok";
			case BLUE:
				return "Warning";
			case RED:
				return "Error";
			case LOG:
				return "Log";
			default:
				throw new WhatIsTypeException(type());
		}
	}


	@Override
	public String getCleanMessageOrMessage(String... defRq) {
		String msg = getCleanMessage(null);
		return X.notEmpty(msg) ? msg : icon() + namehu();
	}

	public enum LEVEL {
		LOG, GREEN, BLUE, RED;

		public String namesys() {
			switch (this) {
				case GREEN:
					return "info";
				case BLUE:
					return "warning";
				case RED:
					return "error";
				case LOG:
					return "trace";
				default:
					throw new WhatIsTypeException(this);
			}
		}

		public void ON() throws NotifyMessageRtException {
			throw I();
		}

		public NotifyMessageRtException I() {
			return new NotifyMessageRtException(this);
		}

		public NotifyMessageRtException I(String message, Object... args) {
			return new NotifyMessageRtException(this, new SimpleMessageRuntimeException(message, args));
		}

		public NotifyMessageRtException I(Exception ex) {
			return new NotifyMessageRtException(this, ex);
		}

		public Pare<LEVEL, String> toPare(String msg) {
			return Pare.of(NotifyMessageRtException.LEVEL.BLUE, msg);
		}
	}

	private static final long serialVersionUID = 1L;

	public LEVEL type() {
		return ENUM.getEnum(index(), LEVEL.class);
	}

	public NotifyMessageRtException(LEVEL error) {
		super(error);
	}

	public NotifyMessageRtException(LEVEL error, Throwable cause) {
		super(error, cause);
	}

}

