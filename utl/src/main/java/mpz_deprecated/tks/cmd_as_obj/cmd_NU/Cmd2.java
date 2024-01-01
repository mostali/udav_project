package mpz_deprecated.tks.cmd_as_obj.cmd_NU;

import mpc.ERR;
import mpc.str.UST;

public class Cmd2<K, V> extends Cmd1<K> {

	public final V val;

	public Cmd2(String pattern, K key, V val) {
		super(pattern, key);
		this.val = val;
	}


	public static Cmd2 of2(String pattern, Class key, Class val) {
		String[] tks = pattern.split("\\s+");
		ERR.isLength(tks, 2);
		return new Cmd2(pattern, UST.strTo(tks[0], key), UST.strTo(tks[1], val));
	}

}
