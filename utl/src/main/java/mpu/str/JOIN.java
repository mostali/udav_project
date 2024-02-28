package mpu.str;

import mpu.IT;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JOIN {

	public static String of(Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining());
	}

	public static String byOf(CharSequence delimetr, Object... args) {
		return Stream.of(args).map(String::valueOf).collect(Collectors.joining(delimetr));
	}

	public static String by(CharSequence delimetr, Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining(IT.NN(delimetr))).toString();
	}

	public static String by(Collection args) {
		return args.stream().map(String::valueOf).collect(Collectors.joining()).toString();
	}

	public static String byComma(Object... args) {
		return byOf(",", args);
	}

	public static String byComma(Collection args) {
		return by(",", args);
	}

	public static String bySpace(Object... objects) {
		return Arrays.stream(objects).map(String::valueOf).collect(Collectors.joining(" "));
	}

	public static String bySpace(Collection args) {
		return by(" ", args);
	}

	public static String byNL(CharSequence... args) {
		return byOf(STR.NL, args);
	}

	public static String byTab(CharSequence... args) {
		return byOf(STR.TAB, args);
	}

	public static String byNL(Collection args) {
		return by(STR.NL, args);
	}

	public static String bySC(Collection args) {
		return by(";", args);
	}

}
