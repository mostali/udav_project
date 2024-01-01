package mpc.str;

import mpc.ERR;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JOIN {

	public static String args(Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining());
	}

	public static String argsBy(CharSequence delimetr, Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining(delimetr));
	}

	public static String allBy(CharSequence delimetr, Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining(ERR.NN(delimetr))).toString();
	}

	public static String all(Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining()).toString();
	}

	public static String COMMA(Object... args) {
		return argsBy(",", args);
	}

	public static String COMMA(Collection args) {
		return allBy(",", args);
	}

	public static String SPACE(Object... objects) {
		return Arrays.stream(objects).map(String::valueOf).collect(Collectors.joining(" "));
	}

	public static String SPACE(Collection args) {
		return allBy(" ", args);
	}

	public static String NL(CharSequence... args) {
		return argsBy(STR.NL, args);
	}

	public static String TAB(CharSequence... args) {
		return argsBy(STR.TAB, args);
	}

	public static String NL(Collection args) {
		return allBy(STR.NL, args);
	}

	public static String SC(Collection args) {
		return allBy(";", args);
	}

}
