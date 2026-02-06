package mpc.net;

import lombok.Getter;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.UST;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

@Getter
public class IllegalHttpStatusException extends RuntimeException {
	private final Integer[] legalCode;
	private final INetRsp response;
	private final int code;//or
	@Getter
	private final Object msg;//or

	public static boolean isCode(Exception ex, int code, boolean... checkCauseMessage) {
		return (ex instanceof IllegalHttpStatusException && ((IllegalHttpStatusException) ex).code() == code) || (ARG.isDefEqTrue(checkCauseMessage) && ex.getMessage().contains(".IllegalHttpStatusException: Response Code [401] is illegal"));
	}

	public int code() {
		return response != null ? response.code() : code;
	}

	public IllegalHttpStatusException(Integer[] legalCode, INetRsp response) {
		super(X.f("Response Code [%s] is illegal, Except %s, Msg [%s]\n%s", response.code(), ARR.as(legalCode), response.msg(), response.any()));
		this.legalCode = legalCode;
		this.response = response;
//		this.msg = response.any();
		this.msg = response.msg();
		code = -1;
	}

	public String getMsgWithCode() {
		return code() + ":" + getMsg();
	}

	public IllegalHttpStatusException(Integer[] legalCode, int code) {
		super(X.f("Response Code [%s] is illegal, Except %s]", code, ARR.as(legalCode)));
		this.legalCode = legalCode;
		this.response = null;
		this.code = IT.isPosNotZero(code);
		this.msg = "";
	}

//	@SneakyThrows
//	public String getMsgAsAnyStr(String... defRq) {
//		return response.anyStr(defRq);
//	}

	public String getMsgAsXml(String... defRq) {
		if (msg != null && msg instanceof CharSequence) {
			String msg0 = (String) msg;
			if (UST.isXml(msg0)) {
				return msg0;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Msg is not XML type:\n%s", msg), defRq);
	}

	public String getMsgAsJsonAny(String... defRq) {
		if (msg != null && msg instanceof CharSequence) {
			String msg0 = (String) msg;
			if (UST.isJsonAny(msg0)) {
				return msg0;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Msg is not XML type:\n%s", msg), defRq);
	}
}
