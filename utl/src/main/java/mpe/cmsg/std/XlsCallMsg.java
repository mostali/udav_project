package mpe.cmsg.std;

import mpe.cmsg.core.CallMsg;
import mpf.zbin.ZBin;
import mpu.X;
import mpu.core.ENUM;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.TKN;

import java.nio.file.Path;
import java.nio.file.Paths;

public class XlsCallMsg extends CallMsg {

//
//	public static <T extends CallMsg> T ofPathRfl(Path file, Class<T> callMsgType, T... defRq) {
//		Function<String, T> ofCaller = (data) -> ofRfl(callMsgType, data, defRq);
//		T callMsg = ofCaller.apply(RW.readString(file));
//		callMsg.setFromSrc(file);
//		return callMsg;
//	}

	public static XlsCallMsg ofAny(Object msg, boolean checkLazyInjectType, XlsCallMsg... defRq) {
		return ofAny(msg, checkLazyInjectType, XlsCallMsg.class, defRq);
	}


//	public Integer getFromDayDayAgo(Integer... defRq) {
//		return getHeaderValueByKey("fromDayAgo", Integer.class, defRq);
//	}

	public Path getPath() {
		return Paths.get(key);
	}

	//
	//
	//

	@Override
	public Object call(boolean throwIfHasError, Object... args) {

		Object rslt = ZBin.POI.newJarCall().invokeMsg(getMsg());

		return rslt;
	}

	//
	//

	public static final String KEY = "xt";
	public static final String LINE0 = "xt:";

	public final String args;

	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

	public final Method method;
	public final String key;

	@Override
	public Method subtype(Object... defRq) {
		return method;
	}

	public enum Method {
		UNDEFINED, FILE, GD, MARKUP;

		public static Method of(String name, Method... defRq) {
			return ENUM.valueOf(name, Method.class, true, defRq);
		}

	}

	public XlsCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			addError("Empty qz msg");
			method = Method.UNDEFINED;
			key = null;
			args = null;
			return;
		}

		String[] two_Method = TKN.two(line0, " ", null);
		if (two_Method == null) {
			addError("Except two arg Msv.METHOD + url, but came %s", line0);
			method = Method.UNDEFINED;
			key = null;
			args = null;
			return;
		}

		method = Method.of(two_Method[0].trim(), Method.UNDEFINED);
		if (method == Method.UNDEFINED) {
			addError("Except first Msv.METHOD from string %s", two_Method[0]);
			key = null;
			args = null;
			return;
		}

		String two_MethodBody = two_Method[1].trim();
		String[] two_owner = TKN.two(two_MethodBody, " ", null);
//
		key = two_owner == null ? two_MethodBody : two_owner[0].trim();

		args = two_owner == null ? null : two_owner[1].trim();
	}

	//
	//

	@Override
	public String toString() {
		return "XlsCallMsg{" + "msg='" + msg + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", method=" + method + ", key=" + key +
//                ", class=" + jobClassName +
				", args=" + args + '}';
	}

	@Deprecated//see rfl
	public static XlsCallMsg of(Path file) {
		XlsCallMsg msvCallMsg = of(RW.readString(file));
		msvCallMsg.setFromSrc(file);
		return msvCallMsg;
	}


	public static XlsCallMsg of(String msg) {
		return (XlsCallMsg) ofQk(msg).throwIsErr();
	}

	public static XlsCallMsg ofQk(String msg) {
		return new XlsCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return XlsCallMsg.of(data).isValid();
	}

}
