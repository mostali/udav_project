package mpe.str;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpu.X;
import mpc.exception.ICleanMessage;

//CleanMessage
@RequiredArgsConstructor
public class CMsg implements ICleanMessage {
	@Getter
	final String cleanMessage;

	public static CMsg of(String msg, Object... args) {
		return new CMsg(X.f(msg, args));
	}

	@Override
	public String toString() {
		return cleanMessage;
	}
}
