package mpe.call_msg;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpc.net.JHttp;
import mpe.call_msg.core.INode;
import mpu.IT;
import mpu.X;
import mpu.core.*;
import mpu.pare.Pare;
import mpu.str.STR;

import java.util.List;
import java.util.Map;

public class IICallMsg extends CallMsg {

	public static final String KEY = "ii";
	public static final String KEY_ = "ii:";

	enum II {
		OPENAI
	}

	public final II ii;
	public final String token;

	@Override
	public String type(Object... defRq) {
		return KEY;
	}

	public static IICallMsg of(String msg) {
		return new IICallMsg(msg, true);
	}

	public static IICallMsg ofQk(String msg) {
		return new IICallMsg(msg, false);
	}

	public static void main(String[] args) {

		String msg = "ii:\nнапиши хоку о работе в IT";

		IICallMsg callMsg = IICallMsg.of(msg);
		Object call = callMsg.call(true);
		X.p(call);
	}

	public IICallMsg(INode iNode) {
		this(iNode.readNodeDataStr(), false);
	}


	public IICallMsg(String fullMsg, boolean... strict) {
		super(fullMsg, true);

		if (ARG.isDefEqTrue(strict)) {
			IT.state(X.notEmpty(fullMsg), "Illegal state, msg is empty");
		}

		switch (state) {
			case EMPTY:
				addError("Empty msg");
				this.token = null;
				this.ii = null;
				return;

			case LINE:
				break;

			default:
			case BODY:
				break;
		}

		//
		//

		Map headersMap = getHeaders_MAP();

		Object token1 = headersMap.get("token");
		Object net = headersMap.get("net");

		if (token1 == null) {
			addError("Set key 'token'");
			this.token = null;
		} else {
			this.token = token1.toString();
		}

		//

		if (net == null) {
			addError("Set key  'net - type of ii'");
			this.ii = null;

		} else {
			String string = X.toStringNN(token1, null);
			this.ii = ENUM.valueOf(string, II.class, null);
		}

		//

		if (ARG.isDefEqTrue(strict)) {
			if (hasErrors()) {
				getErrors().forEach(e -> {
					if (true) {
						throw new FIllegalStateException(e.getMessage());
					}
				});
			}

		} else {
			addError("Except file with first line %s", KEY);
		}


	}

	@Override
	public Object call(boolean throwIfHasError, Object... args) {

//		String msg = "напиши хоку о работе в IT";
//		EnvTlp envTlp = EnvTlp.ofSysAcc("openai");
//		String token = envTlp.readLogin();

//		Map headersMap = getHeaders_MAP();
//		String token1 = (String) headersMap.get("token");
//		String net = (String) headersMap.get("net");

		IT.state(token != null, "set token");

		Pare<String, GsonMap> rsp = sendMsg2Gpt(token, getBody_STRING());

		String fullMsg = rsp.key();
		if (L.isInfoEnabled()) {
			L.info("II Answer:\n" + getBody_STRING());
			L.info("II Rsp:\n" + fullMsg);

		}
		return fullMsg;
	}

	@SneakyThrows
	public static Pare<String, GsonMap> sendMsg2Gpt(String token, String msg) {
		String url = "https://api.openai.com/v1/chat/completions";
		String[][] headers = JHttp.HEADERS_ARGS_BY_SEMICOLON("Content-Type: application/json", "Authorization: Bearer " + token);
		GsonMap inJson = new GsonMap();
		inJson.put("model", "gpt-4o-mini");
		inJson.put("store", true);
		Map msgJson = MAP.of("role", "user", "content", msg);
		inJson.put("messages", ARR.as(msgJson));

		GsonMap rsp = JHttp.GET_BODY(url, headers, inJson.toStringJson(), GsonMap.class, 200);

		List<GsonMap> choices = rsp.getAsArrayGsonMap("choices");

		GsonMap firstChoice = ARRi.first(choices);
		GsonMap firstMsg = firstChoice.getAsGsonMap("message");

		String content = firstMsg.getAsString("content");

		L.info(msg);
		L.info("----------------ANSWER-----------------");
		L.info(content);

		GsonMap usage = rsp.getAsGsonMap("usage");

		L.info("----------------USAGE-----------------");
		L.info(usage.toStringPrettyJson());

		return Pare.of(content, usage);
	}

	@Override
	public String toString() {
		return "BashCallMsg(errs*" + X.sizeOf0(getErrors()) + ")" + STR.NL + fileData;
	}


//	public static IICallMsg of(IPath file, boolean... lazyValid) {
//		return (IICallMsg) ofQk(file, lazyValid).throwIsErr();
//	}

//	public static IICallMsg ofQk(Path file, boolean... lazyValid) {
//		return ofQk(IPath.of(file), lazyValid);
//	}

//	public static IICallMsg ofQk(IPath file, boolean... lazyValid) {
//		return (IICallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
//	}

//	public static IICallMsg of(Path file, boolean... lazyValid) {
//		return of(RW.readContent(file), lazyValid);
//	}

//	public static IICallMsg of(String msg, boolean... strict) {
//		return (IICallMsg) ofQk(msg, strict).throwIsErr();
//	}

//	public static IICallMsg ofQk(String msg, boolean... strict) {
//		return new IICallMsg(msg, strict);
//	}

	public static boolean isValid(String data) {
		try {
			IICallMsg.of(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isValidQk(String data) {
		return STR.startsWith(data, KEY_);
	}
}
