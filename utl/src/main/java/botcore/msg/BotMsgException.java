package botcore.msg;

import mpu.X;
import mpc.exception.ICleanMessage;

import java.util.Map;

public class BotMsgException extends Exception implements ICleanMessage {
	final BotMsgLevel level;
	final Map context;

	public BotMsgLevel level() {
		return level;
	}

	public BotMsgException(String message, Object... args) {
		this(BotMsgLevel.ERROR, message, args);
	}

	public BotMsgException(BotMsgLevel level, String message, Object... args) {
		this(level, null, message, args);
	}

	public BotMsgException(BotMsgLevel level, Map context, String message, Object... args) {
		super(X.f(message, args));
		this.level = level;
		this.context = context;
	}

	public static BotMsgException of(String msg, Object... args) {
		return new BotMsgException(msg, args);
	}

	public static BotMsgException ERR(String msg, Object... args) {
		return new BotMsgException(BotMsgLevel.ERROR, msg, args);
	}

	public static BotMsgException INFO(String msg, Object... args) {
		return new BotMsgException(BotMsgLevel.INFO, msg, args);
	}

	public static BotMsgException WARN(String msg, Object... args) {
		return new BotMsgException(BotMsgLevel.WARN, msg, args);
	}

	public static BotMsgException FAIL(String msg, Object... args) {
		return new BotMsgException(BotMsgLevel.FAIL, msg, args);
	}

//	public static BotMsgException FAIL_NET_CALL(INetRsp rsp, Object... args) {
//		return new BotMsgException(BotMsgLevel.FAIL, UMap.of("rsp="+rsp), null, args);
//	}

	public static BotMsgException FAIL_UUID(String msg, Object... args) {
		return new BotMsgException(BotMsgLevel.ERRUUID, msg, args);
	}

	@Override
	public String getCleanMessage() {
		return getMessage();
	}
}
