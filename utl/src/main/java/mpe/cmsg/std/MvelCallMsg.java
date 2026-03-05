package mpe.cmsg.std;

import mpc.fs.path.IPath;
import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;

public class MvelCallMsg extends CallMsg {

	public static final String KEY = "mvel";
	public static final String LINE0 = "//mvel";

	public static boolean isValidKey(String data) {
		return STR.startsWith(data, LINE0);
	}

	public static boolean isValid(String data) {
		return MvelCallMsg.ofQk(data).isValid();
	}

	public MvelCallMsg(String fullMsg) {
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


	public static MvelCallMsg of(IPath file) {
		return (MvelCallMsg) ofQk(file).throwIsErr();
	}

	public static MvelCallMsg ofQk(Path file) {
		return ofQk(IPath.of(file));
	}

	public static MvelCallMsg ofQk(IPath file) {
		return (MvelCallMsg) of(file.fCat()).setFromSrc(file);
	}

	public static MvelCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static MvelCallMsg of(String msg) {
		return (MvelCallMsg) ofQk(msg).throwIsErr();
	}

	public static MvelCallMsg ofQk(String msg) {
		return new MvelCallMsg(msg);
	}


}
