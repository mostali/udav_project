package mpe.call_msg;

import mpc.exception.*;
import mpc.fs.path.IPath;
import mpu.X;
import mpu.core.ENUM;
import mpu.core.RW;
import mpu.str.UST;
import mpu.str.TKN;

import java.nio.file.Path;

public class KafkaCallMsg extends CallMsg {

	public final String url;

	public final KafkaMethodType kafka_method;

	@Override
	public KafkaMethodType type(Object...defRq) {
		return kafka_method;
	}

	public enum KafkaMethodType {
		UNDEFINED, KPUT, KGET;

		public static KafkaMethodType of(String name, KafkaMethodType... defRq) {
			return ENUM.valueOf(name, KafkaMethodType.class, true, defRq);
		}
	}

	public KafkaCallMsg(String fullMsg) {
		super(fullMsg, true);

		if (X.empty(linesMsgHeadersAndBody())) {
			url = null;
			addError("Empty msg");
			kafka_method = KafkaMethodType.UNDEFINED;
			return;
		}


		String[] two = TKN.two(line0, " ", null);
		if (two == null) {
			addError("Except two arg kafkaMethod + url, but came %s", line0);
		}

		{//KAFKA_METHOD
			if (hasErrors()) {
				kafka_method = KafkaMethodType.of(line0, null);
			} else { // METHOD
				kafka_method = KafkaMethodType.of(two[0], KafkaMethodType.UNDEFINED);
				if (kafka_method == KafkaMethodType.UNDEFINED) {
					addError("Except first KafkaMethodType from string %s", two[0]);
				}
			}
		}

		{//URL
			out:
			if (hasErrors()) {
				url = UST.URL(line0, null) == null ? null : line0;
			} else { //URL

				this.url = two[1];

				if (UST.URL(url, null) == null) {
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
				"msg='" + fileData + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", kafka_method=" + kafka_method +
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
		return of(RW.readString(file));
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
