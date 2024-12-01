package mpe.checks;


import mpu.IT;
import mpu.X;
import mpu.str.STR;

//Check Utility ( for integer )
public class UCi {
	public static int[] isLength(int[] checkable, long length, Object... message) {
		if (checkable != null && checkable.length == length) {
			return checkable;
		}
		throw new IT.CheckException(message.length == 0 ? X.f("Checked array length must be equals Length(%s)==Array(%s)", length, checkable.length) : STR.formatAll(message));
	}
}
