package mpe.core;

import mpu.core.ENUM;
import mpu.pare.Pare3;
import mpu.str.TKN;

//Operation
public enum OPR {
	EQ, NEQ, LIKE, NOTLIKE, LE, LT, GE, GT, BETWEEN, IN, NOTIN, ISNULL, ISNOTNULL;

	public static Pare3<String, String, OPR> decodeToPare3(String coded) {//eq:col:value
		String[] two = TKN.two(coded, ":");
		OPR op = ENUM.valueOf(two[0], OPR.class, true);
		two = TKN.two(two[1], ":");
		String col = two[0];
		String val = two[1];
		return Pare3.of(col, val, op);
	}
}