package mpc.exception;

import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.IT;
import mpu.X;

public class EException extends Exception implements IEE<Enum> {

	private static final long serialVersionUID = 1L;

	public final int enumIndex;

	final Class<? extends Enum> enumClass;

	public int index() {
		return enumIndex;
	}

	public int indexNegative() {
		return -1 * enumIndex;
	}

	public boolean is(Class<? extends Enum> error) {
		return error.isAssignableFrom(enumClass);
	}

	public <E extends Enum> E type() {
		return (E) type(enumClass);
	}

	public <E extends Enum> E type(Class<E> clazz) {
		return IT.notNull(ENUM.getEnum(enumIndex, clazz));
	}

	public EException(Enum error) {
		super(error.name());
		this.enumIndex = ENUM.indexOf(error);
		enumClass = error.getClass();
	}

	public EException(Enum error, Throwable cause) {
		super(error.name(), cause);
		this.enumIndex = ENUM.indexOf(error);
		enumClass = error.getClass();
	}

	/**
	 * *************************************************************
	 * ---------------------------- CLEAN MESSAGE ------------------
	 * *************************************************************
	 */

	public String getCleanMessageAny(String... defRq) {
		String msg = getCleanMessageOnly(null);
		return X.notEmpty(msg) ? msg : getMessage();
	}

	public String getCleanMessageOnly(String... defRq) {
		if (hasCleanMessage()) {
			return getCleanMessageType().getCleanMessage();
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Clean Message not found:" + this);
	}

	public boolean hasCleanMessage() {
		return getCause() != null && getCause() instanceof ICleanMessage;
	}

	private ICleanMessage getCleanMessageType() {
		return (ICleanMessage) getCause();
	}

	/**
	 * *************************************************************
	 * ---------------------------- NATIVE ------------------
	 * *************************************************************
	 */
	@Override
	public String toString() {
		return IEE.getMessageV2(this, super.getMessage());
	}

	@Override
	public String getMessage() {
		return IEE.getMessageV0(this, super.getMessage());
//		return IEE.getMessageV1(this, super.getMessage());
	}

}
