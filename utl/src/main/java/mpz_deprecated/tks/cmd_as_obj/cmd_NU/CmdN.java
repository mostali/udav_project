package mpz_deprecated.tks.cmd_as_obj.cmd_NU;

import lombok.RequiredArgsConstructor;
import mpc.ERR;
import mpc.str.UST;

@RequiredArgsConstructor
public class CmdN<K, V, E> {
	public final String pattern;
	public final Object[] objects;
//		public final CmdTk[] tokenType;

//		public static Cmdn ofSC(String pattern, CmdTk... types) {
//			String[] tks = US.SPLIT.splitRx(pattern, "\\s+", types.length);
//		}

//		public static Cmd3 ofAny(String cmd, CmdTk... types) {
//			String[] tks = cmd.split("\\s+");
//			UC.isLength(tks, types.length);
//			Object[] tokens = new Object[types.length];
//			for (int i = 0; i < tks.length; i++) {
//				tokens[i] = types[i].strTo(tks[i]);
//			}
//			return new Cmd3(cmd, tks, types);
//		}

	public static CmdN of(String pattern, Class... types) {
		String[] tks = pattern.split("\\s+");
		ERR.isLength(tks, types.length);
		Object[] tokens = new Object[types.length];
		for (int i = 0; i < tks.length; i++) {
			tokens[i] = UST.strTo(tks[i], types[i]);
		}
		return new CmdN(pattern, tks);
	}

	public K key() {
		return (K) objects[0];
	}

	public V val() {
		return (V) objects[1];
	}

	public E ext() {
		return (E) objects[2];
	}

}
