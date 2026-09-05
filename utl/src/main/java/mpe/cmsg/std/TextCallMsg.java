package mpe.cmsg.std;

import mpc.fs.path.IPath;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.INode;
import mpu.X;
import mpu.core.RW;

import java.nio.file.Path;

public class TextCallMsg extends CallMsg {

//	public static final String KEY = "python";
//	public static final String LINE0 = "#python";

//	public static boolean isValidKey(String data) {
//		return true;
//	}
//
//	public static boolean isValid(String data) {
//		return TextCallMsg.ofQk(data).isValid();
//	}

	public TextCallMsg(INode iNode) {
		super(iNode);
	}

	public TextCallMsg(String fullMsg) {
		super(fullMsg, true);

//		switch (state) {
//			case EMPTY:
//				addError("Empty msg");
//				return;
//
//			case LINE:
//				break;
//
//			default:
//			case BODY:
//				break;
//		}

	}

	@Override
	public String toString() {
		return "TextCallMsg{" +
//				"msg='" + fullMsg + '\'' +
//				", class='" + className + '\'' + ", method='" + classMethodName + '\'' + ", headers=" + headersParams + ", context=" + context +
				", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public static TextCallMsg of(IPath file) {
		return (TextCallMsg) ofQk(file).throwIsErr();
	}

	public static TextCallMsg ofQk(Path file) {
		return ofQk(IPath.of(file));
	}

	public static TextCallMsg ofQk(IPath file) {
		return (TextCallMsg) of(file.fCat()).setFromSrc(file);
	}

	public static TextCallMsg of(Path file) {
		TextCallMsg callMsg = of(RW.readString(file));
		callMsg.setFromSrc(file);
		return callMsg;
	}

	public static TextCallMsg of(String msg) {
		return (TextCallMsg) ofQk(msg).throwIsErr();
	}

	public static TextCallMsg ofQk(String msg) {
		return new TextCallMsg(msg);
	}

}
