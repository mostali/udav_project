package mpc.core;

import mpc.types.pare.Pare3;
import mpc.str.USToken;

//Operation
public enum OPR {
	EQ, NEQ, LIKE, NOTLIKE, LE, LT, GE, GT, BETWEEN, IN, NOTIN, ISNULL, ISNOTNULL;

	public static Pare3<String, String, OPR> decodeToPare3(String coded) {//eq:col:value
		String[] two = USToken.two(coded, ":");
		OPR op = EN.valueOf(two[0], OPR.class, true);
		two = USToken.two(two[1], ":");
		String col = two[0];
		String val = two[1];
		return Pare3.of(col, val, op);
	}
}