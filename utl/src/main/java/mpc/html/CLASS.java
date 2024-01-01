package mpc.html;

import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.str.JOIN;
import mpc.X;
import mpc.str.SPLIT;

import java.util.List;
import java.util.Set;

public class CLASS {

	public static String addClass(String classes, String[] singleClass, boolean... checkUniq) {
		if (X.empty(classes)) {
			return JOIN.argsBy(" ", singleClass);
		} else if (ARG.isDefNotEqTrue(checkUniq)) {
			return classes + " " + JOIN.argsBy(" ", singleClass);
		}

		Set<String> classesList = Arr.asLSet(SPLIT.by_(classes, " "));
		for (String clazz : singleClass) {
			classesList.add(clazz);
		}
		return JOIN.SPACE(classesList);
	}

	public static String rmClass(String classes, String[] singleClass) {
		if (X.empty(classes)) {
			return "";
		}
		List<String> classesList = Arr.ar(SPLIT.by_(classes, " "));
		List<String> needRemove = Arr.as(singleClass);
		classesList.removeAll(needRemove);
		return JOIN.SPACE(classesList);
	}
}
