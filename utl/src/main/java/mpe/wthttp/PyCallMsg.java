package mpe.wthttp;

import mpc.fs.path.IPath;
import mpe.wthttp.core.INode;
import mpu.Sys;
import mpu.X;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;

public class PyCallMsg extends CallMsg {

	public static final String KEY = "#python";

	public static boolean isValidKeyFirstLine(String data) {
		return STR.startsWith(data, KEY);
	}

	public static boolean isValid(String data) {
		return PyCallMsg.ofQk(data).isValid();
	}

	public PyCallMsg(INode iNode) {
		super(iNode);
	}

	@Override
	public Object type(Object... defRq) {
		return KEY;
	}

	public PyCallMsg(String fullMsg, boolean... lazyValid) {
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
	public Object call(boolean throwIfHasError) {
		return Sys.exePython3(fileData);
	}

	@Override
	public String toString() {
		return "PyCallMsg{" +
//				"msg='" + fullMsg + '\'' +
//				", class='" + className + '\'' + ", method='" + classMethodName + '\'' + ", headers=" + headersParams + ", context=" + context +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static PyCallMsg of(IPath file, boolean... lazyValid) {
		return (PyCallMsg) ofQk(file, lazyValid).throwIsErr();
	}

	public static PyCallMsg ofQk(Path file, boolean... lazyValid) {
		return ofQk(IPath.of(file), lazyValid);
	}

	public static PyCallMsg ofQk(IPath file, boolean... lazyValid) {
		return (PyCallMsg) of(file.fCat(), lazyValid).setFromSrc(file);
	}

	public static PyCallMsg of(Path file, boolean... lazyValid) {
		return of(RW.readContent(file), lazyValid);
	}

	public static PyCallMsg of(String msg, boolean... lazyValid) {
		return (PyCallMsg) ofQk(msg, lazyValid).throwIsErr();
	}

	public static PyCallMsg ofQk(String msg, boolean... lazyValid) {
		return new PyCallMsg(msg, lazyValid);
	}

}
