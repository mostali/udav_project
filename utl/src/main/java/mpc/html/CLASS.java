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
			return JOIN.byOf(" ", singleClass);
		} else if (ARG.isDefNotEqTrue(checkUniq)) {
			return classes + " " + JOIN.byOf(" ", singleClass);
		}

		Set<String> classesList = ARR.asLSet(SPLIT.by_(classes, " "));
		for (String clazz : singleClass) {
			classesList.add(clazz);
		}
		return JOIN.bySpace(classesList);
	}

	public static String rmClass(String classes, String[] singleClass) {
		if (X.empty(classes)) {
			return "";
		}
		List<String> classesList = ARR.ar(SPLIT.by_(classes, " "));
		List<String> needRemove = ARR.as(singleClass);
		classesList.removeAll(needRemove);
		return JOIN.bySpace(classesList);
	}
}
