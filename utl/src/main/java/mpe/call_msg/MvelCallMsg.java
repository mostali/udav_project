package mpe.call_msg;

import mpc.fs.path.IPath;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;

public class MvelCallMsg extends CallMsg {

	public static final String KEY = "//mvel";

	public static boolean isValidKeyFirstLine(String data) {
		return STR.startsWith(data, KEY);
	}

	public static boolean isValid(String data) {
		return MvelCallMsg.ofQk(data).isValid();
	}

	@Override
	public Object type(Object...defRq) {
		return KEY;
	}


	public MvelCallMsg(String fullMsg, boolean... lazyValid) {
		super(fullMsg, true);

		switch (state) {
			case EMPTY:
				addError("Empty msg");
				return;

			case LINE:
				break;

			default:
			case BODY:
				break;
		}


	}


	@Override
	public String toString() {
		return "GroovyCallMsg{" +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static MvelCallMsg of(IPath file, boolean... lazyValid) {
		return (MvelCallMsg) ofQk(file, lazyValid).throwIsErr();
	}

	public static MvelCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static MvelCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (MvelCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static MvelCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readString(file), lazyValid);
	}

	public static MvelCallMsg of(String msg, boolean... lazyValid) {
		return (MvelCallMsg) ofQk(msg, lazyValid).throwIsErr();
	}

	public static MvelCallMsg ofQk(String msg, boolean... lazyValid) {
		return new MvelCallMsg(msg, lazyValid);
	}


}
