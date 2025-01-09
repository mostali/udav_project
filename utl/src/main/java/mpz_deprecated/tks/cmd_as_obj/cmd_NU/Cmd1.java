package mpz_deprecated.tks.cmd_as_obj.cmd_NU;

import mpu.str.UST;

public class Cmd1<K> {
	public final String pattern;
	public final K key;

	public Cmd1(String pattern, K key) {
		this.pattern = pattern;
		this.key = key;
	}

	public K key() {
		return key;
	}

	public static Cmd1 of1(String pattern, Class type) {
		return new Cmd1(pattern, UST.strTo(pattern, type));
	}

//	public static Cmd1 __strTo__(String pattern) {
//		return new Cmd1(pattern, UST.strTo(pattern, type));
//	}

}
