package mpz_deprecated.tks.cmd_as_obj.cmd_NU;

import mpc.ERR;
import mpc.str.UST;

public class Cmd3<K, V, E> extends Cmd2<K, V> {

	public final E ext;

	public Cmd3(String pattern, K key, V val, E ext) {
		super(pattern, key, val);
		this.ext = ext;
	}

	public static Cmd3 of3(String pattern, Class key, Class val, Class ext) {
		String[] tks = pattern.split("\\s+");
		ERR.isLength(tks, 3);
		return new Cmd3(pattern, UST.strTo(tks[0], key), UST.strTo(tks[1], val), UST.strTo(tks[2], ext));
	}

//	public static <K, V, E> Cmd3 of3(String pattern, OBJ<K> key, OBJ<V> val, OBJ<E> ext) {
//		String[] tks = pattern.split("\\s+");
//		UC.isLength(tks, 3);
//		return new Cmd3(pattern, UST.strTo(tks[0], key), UST.strTo(tks[1], val), UST.strTo(tks[2], ext));
//	}
}
