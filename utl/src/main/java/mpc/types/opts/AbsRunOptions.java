package mpc.types.opts;

import mpu.Sys;
import mpc.exception.NI;
import lombok.Getter;
import mpu.IT;
import mpu.str.UST;

import java.util.List;

public abstract class AbsRunOptions {

	@Getter
	private List<String> protectedKeys;

	//	public abstract SeqRunOptions setProtectedValues(List<String> keys);
	public AbsRunOptions setProtectedValues(List<String> keys) {
		this.protectedKeys = keys;
		return this;
	}

	public static void test(AbsRunOptions opts) {

		//test 1
		{
			Object r = opts.getSingle("sd",null);
			IT.isNull(r, "need null");
			Sys.p("Test1 is ok.");

			try {
				opts.getSingle("sd");
				IT.notNull(null, "test 2 need throw error");
			} catch (Exception ex) {
				Sys.p("Test1.1 is ok:" + ex.getMessage());
			}

			String name = opts.getSingle("name");
			IT.isEq(name, "S1", "test1.3 eq names");
			Sys.p("Test1.3 is s ok:getSingle(name):" + name);

		}
		//test 3
		{
			String message = "test 2 hasDoubleQk:false";
			Boolean r = opts.hasDouble("sd",false);
			IT.isFalse((Boolean) r, message, "sd");
			Sys.p(message);

			message = "test 2.1 hasDoubleQk:true";
			IT.isTrue(opts.hasDouble("skip.md5",false), message, "skip.md5");
			Sys.p(message);

			message = "test 2.2 hasDoubleRq throw error";
			try {
				opts.hasDouble("sd");
				IT.notNull(null, message, "sd");
			} catch (Exception ex) {
				Sys.p(message + ":" + ex.getMessage());
			}
		}


	}

	public abstract String getSingle(String key, String... defRq);

	public abstract <T> T getSingleAs(String key, Class<T> type, T... defRq);

	public abstract List<String> getSingleAll(String key, List<String>... defRq);

	public abstract Boolean hasDouble(String key, Boolean... defRq);

	public abstract boolean hasSimple(String key);

	//
	//

	public abstract boolean hasSingleNotEmpty(String key);

	public boolean hasAnyToken(String s) {
		throw new NI(s);
	}

	public enum OptType {
		EMPTY, CHAR, DASH, DASH2, SIMPLE, SINGLE, DOUBLE, NUM;

		public static OptType of(String val) {
			switch (val.length()) {
				case 0:
					return EMPTY;
				case 1:
					return val.charAt(0) == '-' ? DASH : CHAR;
				default:
					switch (val.charAt(0)) {
						case '-':
							if (UST.BD(val, null) != null) {
								return NUM;
							}
							return val.charAt(1) == '-' ? (val.length() == 2 ? DASH2 : DOUBLE) : SINGLE;
						default:
							return SIMPLE;
					}
			}
		}
	}

	public static class CmdOption {
		public final String key, val;
		public final OptType type;

		public OptType type() {
			return type;
		}

		public CmdOption(String key) {
			this.key = key;
			this.val = null;
			this.type = OptType.of(key);
		}

		public CmdOption(String key, String val) {
			this(key, val, OptType.SINGLE);
		}

		public CmdOption(String key, String val, OptType optType) {
			this.key = key;
			this.val = val;
			this.type = optType;
		}

		@Override
		public String toString() {
			return "RunOption{" +
					"key='" + key + '\'' +
					", val='" + val + '\'' +
					", type=" + type() +
					'}';
		}

		public static CmdOption of(String opt) {
			return new CmdOption(opt);
		}

		public boolean eq(String val) {
			switch (type) {
				case SINGLE: {
					return key.substring(1).equals(val);
				}
				case DOUBLE: {
					return key.substring(2).equals(val);
				}
				default:
					return key.equals(val);
			}
		}
	}
}
