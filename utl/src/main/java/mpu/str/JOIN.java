package mpu.str;

import mpu.IT;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JOIN {

	//

	public static String args(Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining());
	}

	public static String all(Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining()).toString();
	}

	//

	public static String argsBy(CharSequence delimetr, Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining(IT.NN(delimetr)));
	}

	public static String allBy(Collection args, String del, String pfx, String sfx) {
		Stream<String> stream = args.stream().map(o -> String.valueOf(o));
		return stream.collect(Collectors.joining(del, pfx, sfx));
	}

	public static String allBy(CharSequence delimetr, Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining(IT.NN(delimetr))).toString();
	}

	//

	public static String argsByComma(Object... args) {
		return argsBy(",", args);
	}

	public static String allByComma(Collection args) {
		return allBy(",", args);
	}

	//

	public static String argsBySpace(Object... objects) {
		return Arrays.stream(objects).map(String::valueOf).collect(Collectors.joining(" "));
	}

	public static String allBySpace(Collection args) {
		return allBy(" ", args);
	}

	//

	public static String argsByNL(CharSequence... args) {
		return argsBy(STR.NL, args);
	}

	public static String objsByNL(Object... args) {
		return argsBy(STR.NL, args);
	}

	public static String allByNL(Collection args) {
		return allBy(STR.NL, args);
	}

	//

	public static String argsByTab(CharSequence... args) {
		return argsBy(STR.TAB, args);
	}

	public static String argsByTab(Collection args) {
		return allBy(STR.TAB, args);
	}


}
