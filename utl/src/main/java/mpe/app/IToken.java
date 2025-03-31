package mpe.app;

import mpu.str.UST;

public interface IToken {

	String getTokenNid();

	default Integer getTokenNidInt() {
		return UST.INT(getTokenNid());
	}

	String getTokenValue();

	String getTokenType();
}
