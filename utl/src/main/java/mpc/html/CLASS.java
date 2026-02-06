package mpc.html;

import mpu.core.ARR;
import mpu.core.ARG;
import mpu.str.JOIN;
import mpu.X;
import mpu.str.SPLIT;

import java.util.List;
import java.util.Set;

public class CLASS {

	public static String addClass(String classes, String[] singleClass, boolean... checkUniq) {
		if (X.empty(classes)) {
			return JOIN.argsBy(" ", singleClass);
		} else if (ARG.isDefNotEqTrue(checkUniq)) {
			return classes + " " + JOIN.argsBy(" ", singleClass);
		}

		Set<String> classesList = ARR.asLSET(SPLIT.argsBy(classes, " "));
		for (String clazz : singleClass) {
			classesList.add(clazz);
		}
		return JOIN.allBySpace(classesList);
	}

	public static String rmClass(String classes, String[] singleClass) {
		if (X.empty(classes)) {
			return "";
		}
		List<String> classesList = ARR.asAL(SPLIT.argsBy(classes, " "));
		List<String> needRemove = ARR.as(singleClass);
		classesList.removeAll(needRemove);
		return JOIN.allBySpace(classesList);
	}
}
