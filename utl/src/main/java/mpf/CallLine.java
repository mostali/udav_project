package mpf;

import mpe.call_msg.CallErrMsg;
import mpu.X;

public class CallLine extends CallErrMsg {

	public final String line0;


	public Object type() {
		return null;
	}

	public CallLine(String line) {
		this.line0 = line;
	}


	//
	//

	@Override
	public String toString() {
		return "CallLine{" +
				", line='" + line0 + '\'' +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}
}
