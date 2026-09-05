package mpe.ftypes.core;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class FBigDecimal extends BigDecimal {

	public FBigDecimal(String val) {
		super(val);
	}

	public FBigDecimal(double val) {
		super(val);
	}

	public FBigDecimal(BigInteger val) {
		super(val);
	}

	public FBigDecimal(int val) {
		super(val);
	}

	public FBigDecimal(long val) {
		super(val);
	}

	public abstract BigDecimal toBigDecimal();
}
