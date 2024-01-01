package mpe.str;

import mpc.args.ARG;
import mpc.ERR;
import mpc.str.ObjTo;
import mpc.str.UST;

import java.util.Arrays;
import java.util.List;

public class ARGS {

	public static Long argsAsLong(String[] args, int index, Long... defRq) {
		try {
			return Long.parseLong(args[index]);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Integer argsAsInt(Object[] args, int index, Integer... defRq) {
		try {
			return UST.INT((String) args[index]);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static String argsAsStr(Object[] args, int index, String... defRq) {
		try {
			return (String) args[index];
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static <T> T argsAsType(Object[] args, int index, Class<T> asType, T... defRq) {
		try {
			return ObjTo.objTo(args[index], asType, defRq);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Integer argsAsInt(String[] args, int index, Integer... defRq) {
		try {
			return UST.INT(args[index]);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Integer argsAsInt(String[] args, int index, String pfx, boolean isPfxRq, Integer[] minMax, Integer... defRq) {
		return argsAsInt(Arrays.asList(args), index, pfx, isPfxRq, minMax, defRq);
	}

	public static Integer argsAsInt(List<String> args, int index, String pfx, boolean isPfxRq, Integer[] minMax, Integer... defRq) {
		try {
			return UST.INT(args.get(ERR.isIndex(index, args)), pfx, isPfxRq, minMax);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Double argsAsDbl(String[] args, int index, Double... defRq) {
		try {
			return UST.DBL(args[index], defRq);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static String argsAsStr(String[] args, int index, String... defRq) {
		try {
			return args[index];
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}
}
