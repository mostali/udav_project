package mpe.call_msg;

import mpc.fs.path.IPath;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;

public class GroovyCallMsg extends CallMsg {

	public static final String KEY = "//groovy";

	public static boolean isValidKeyFirstLine(String data) {
		return STR.startsWith(data, KEY);
	}

	public static boolean isValid(String data) {
		return GroovyCallMsg.ofQk(data).isValid();
	}

	@Override
	public Object type(Object...defRq) {
		return KEY;
	}


	public GroovyCallMsg(String fullMsg, boolean... lazyValid) {
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


	public static GroovyCallMsg of(IPath file, boolean... lazyValid) {
		return (GroovyCallMsg) ofQk(file, lazyValid).throwIsErr();
	}

	public static GroovyCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static GroovyCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (GroovyCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static GroovyCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readString(file), lazyValid);
	}

	public static GroovyCallMsg of(String msg, boolean... lazyValid) {
		return (GroovyCallMsg) ofQk(msg, lazyValid).throwIsErr();
	}

	public static GroovyCallMsg ofQk(String msg, boolean... lazyValid) {
		return new GroovyCallMsg(msg, lazyValid);
	}


}
