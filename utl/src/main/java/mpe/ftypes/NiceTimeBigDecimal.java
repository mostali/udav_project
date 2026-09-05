package mpe.ftypes;


import mpe.ftypes.core.FBigDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NiceTimeBigDecimal extends FBigDecimal {

	public NiceTimeBigDecimal(String val) {
		super(val);
	}

	public NiceTimeBigDecimal(double val) {
		super(val);
	}

	public NiceTimeBigDecimal(BigInteger val) {
		super(val);
	}

	public NiceTimeBigDecimal(int val) {
		super(val);
	}

	public NiceTimeBigDecimal(long val) {
		super(val);
	}

	@Override
	public BigDecimal toBigDecimal() {
		return this;
	}

	@Override
	public String toString() {
		return toString(new BigDecimal(super.toString()));
	}

	public static String toString(double val) {
		return toString(new BigDecimal(val));
	}

	public static String toString(BigDecimal d) {
		d = d.setScale(1, RoundingMode.HALF_UP);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator(',');
		DecimalFormat df = new DecimalFormat();
		df.setDecimalFormatSymbols(otherSymbols);
		df.setMaximumFractionDigits(3);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(true);
		//DecimalFormat df = new DecimalFormat("###");
		String str = df.format(d);
		return str;
	}
}
