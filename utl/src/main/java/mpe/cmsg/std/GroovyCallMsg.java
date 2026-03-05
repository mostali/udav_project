package mpe.cmsg.std;

import mpc.fs.path.IPath;
import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;

public class GroovyCallMsg extends CallMsg {

	public static final String KEY = "groovy";
	public static final String LINE0 = "//groovy";

	public static boolean isValidKey(String data) {
		return STR.startsWith(data, LINE0);
	}

	public static boolean isValid(String data) {
		return GroovyCallMsg.ofQk(data).isValid();
	}

	public GroovyCallMsg(String fullMsg) {
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


	public static GroovyCallMsg of(IPath file) {
		return (GroovyCallMsg) ofQk(file).throwIsErr();
	}

	public static GroovyCallMsg ofQk(Path file) {
		return ofQk(IPath.of(file));
	}

	public static GroovyCallMsg ofQk(IPath file) {
		return (GroovyCallMsg) of(file.fCat()).setFromSrc(file);
	}

	public static GroovyCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static GroovyCallMsg of(String msg) {
		return (GroovyCallMsg) ofQk(msg).throwIsErr();
	}

	public static GroovyCallMsg ofQk(String msg) {
		return new GroovyCallMsg(msg);
	}


}
