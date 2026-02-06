package mpe.ftypes;

import mpe.ftypes.core.FBigDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;

public class F202BigDecimal extends FBigDecimal {

	public static final int ROUND = BigDecimal.ROUND_HALF_DOWN;
	public static final int SCALE = 2;

	public F202BigDecimal(String val) {
		super(val);
	}

	public F202BigDecimal(double val) {
		super(val);
	}

	public F202BigDecimal(BigInteger val) {
		super(val);
	}

	public F202BigDecimal(int val) {
		super(val);
	}

	public F202BigDecimal(long val) {
		super(val);
	}

	@Override
	public BigDecimal toBigDecimal() {
		return setScale(SCALE, ROUND);
	}
}
