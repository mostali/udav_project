package mpe.cmsg.std;

import mpc.exception.*;
import mpc.fs.path.IPath;
import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.ENUM;
import mpu.core.RW;
import mpu.str.UST;
import mpu.str.TKN;

import java.nio.file.Path;

public class KafkaCallMsg extends CallMsg {

	public static final String KEY = "kafka";
	public static final Class<? extends Enum> SUB0 = Method.class;

	public final String url;

	public final Method method;

	@Override
	public Method subtype(Object... defRq) {
		return method;
	}

	public enum Method {
		UNDEFINED, KPUT, KGET;

		public static Method of(String name, Method... defRq) {
			return ENUM.valueOf(name, Method.class, true, defRq);
		}
	}

	public KafkaCallMsg(String fullMsg) {
		super(fullMsg, true);

		if (X.empty(getLinesMsg())) {
			url = null;
			addError("Empty msg");
			method = Method.UNDEFINED;
			return;
		}


		String[] two = TKN.two(line0, " ", null);
		if (two == null) {
			addError("Except two arg kafkaMethod + url, but came %s", line0);
		}

		{//KAFKA_METHOD
			if (hasErrors()) {
				method = Method.of(line0, null);
			} else { // METHOD
				method = Method.of(two[0], Method.UNDEFINED);
				if (method == Method.UNDEFINED) {
					addError("Except first KafkaMethodType from string %s", two[0]);
				}
			}
		}

		{//URL
			out:
			if (hasErrors()) {
				url = UST.URL0(line0, null) == null ? null : line0;
			} else { //URL

				this.url = two[1];

				if (UST.URL0(url, null) == null) {
					FIllegalArgumentException e = new FIllegalArgumentException("Illegal url '%s'", url);
					addError(e);
					break out;
				}

			}
		}

		try {
			getTopic();
		} catch (Exception ex) {
			addError(ex);
		}

		if (X.emptyAll(getKey(null), getBody_STRING())) {
			addError("Set key or body");
		}

	}

	@Override
	public String toString() {
		return "KafkaCallMsg{" +
				"msg='" + msg + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", kafka_method=" + method +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}


	public String getTopic(String... defRq) {
		return getHeaderValueByKey("topic", defRq);
	}

	public String getKey(String... defRq) {
		return getHeaderValueByKey("key", defRq);
	}

	public String getGroup(String... defRq) {
		return getHeaderValueByKey("group", defRq);
	}

	public static KafkaCallMsg of(IPath file) {
		return (KafkaCallMsg) ofQk(file).throwIsErr();
	}

	public static KafkaCallMsg ofQk(IPath file) {
		return (KafkaCallMsg) of(file.fCat()).setFromSrc(file);
	}

	public static KafkaCallMsg of(Path file) {
		KafkaCallMsg callMsg = of(RW.readString(file));
		callMsg.setFromSrc(file);
		return callMsg;
	}

	public static KafkaCallMsg of(String msg) {
		return (KafkaCallMsg) ofQk(msg).throwIsErr();
	}

	public static KafkaCallMsg ofQk(String msg) {
		return new KafkaCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return KafkaCallMsg.ofQk(data).isValid();
	}
}
