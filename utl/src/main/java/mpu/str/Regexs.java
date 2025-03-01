package mpu.str;

import mpc.types.AtomicString;
import mpu.IT;

import java.util.Map;
import java.util.Set;

public class Regexs {

	public static final String WORD = "\\w+";
	public static final String WORDDASH = "[\\w-]+";
	public static final String WORD_RU = "[a-zA-zа-яА-Я]+";
	public static final String PH = "[{](\\w+)}";

	public static String applyMask(String pattern, Map context) {
		IT.NE(context);
		Set<Map.Entry> set = context.entrySet();
		AtomicString str0 = new AtomicString(pattern);
		set.forEach(e -> str0.set(str0.get().replace("{" + e.getKey() + "}", IT.NN(e.getValue()) + "")));
		return str0.get();
	}
}
