package mpz_deprecated;

import mpu.Sys;
import mpu.str.UST;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class DArgs {

	// TODO
	public static void main(String[] args) {

		Sys.exit(DArgs.getDargAsString("asd a1sd s4 sd", "a1s", "3"));
		String fa = new DArg("asd asd s4 sd", "s").fullArg;
		Sys.p(fa);
	}


	public static int getDargAsInt(String settings, String dargName, int defValue) {
		return DArg.getDargAsInt(settings, dargName, defValue);
	}


	public static String getDargAsString(String settings, String dargName, String defValue) {
		return DArg.getDargAsString(settings, dargName, defValue);
	}

	public static DArg getDarg(String[] cmds, String name) {
		List<DArg> args = getDargsStartFromIndex(cmds, 0);
		for (DArg dArg : args) {
			if (dArg.name.equals(name)) {
				return dArg;
			}
		}
		return null;
	}

	public static DArg getDargsStartFromIndex(String[] cmds, int startDargs, String name) {
		List<DArg> args = getDargsStartFromIndex(cmds, startDargs);
		for (DArg dArg : args) {
			if (dArg.name.equals(name)) {
				return dArg;
			}
		}
		return null;
	}

	public static List<DArg> getDargsStartFromIndex(String[] cmds, int start) {
		List<DArg> args = new ArrayList<DArg>();
		for (int i = start; i < cmds.length; i++) {
			DArg da = DArg.ofSafe(cmds[i]);
			if (da != null) {
				args.add(da);
			}
		}
		return args;
	}

	public static class DArg {
		public final String fullArg;
		public final String name;
		public final String value;

		public DArg(String fullArg) {
			this.fullArg = fullArg;

			String[] pare = pareFromDarg(fullArg);
			name = pare[0];
			value = pare[1];
		}

		public DArg(String[] args, String nameArg) {
			String[] pare = pareFromArray(args, nameArg);
			this.name = pare[0];
			this.value = pare[1];
			this.fullArg = this.name + this.value;
		}

		public DArg(String original, String nameArg) {
			this(original.split("\\s++"), nameArg);
		}

		public static Integer getDargAsInt(String settings, String name, Integer def) {
			return UST.getInt(getValueDargAsString(settings, name), def);
		}

		public static String getValueDargAsString(String settings, String name) {
			DArg da = getDarg(settings.split("\\s++"), name);
			return da == null ? null : da.value;
		}

		public static String getDargAsString(String settings, String name, String def) {
			String v = getValueDargAsString(settings, name);
			return v == null ? def : v;
		}

		public int integer() {
			return Integer.parseInt(value);
		}

		public static DArg of(String original, String nameArg) {
			return new DArg(original, nameArg);
		}

		private static DArg of(String param) {
			DArg darg = ofSafe(param);
			if (darg == null) {
				throw new IllegalArgumentException("is not darg:" + param);
			}
			return darg;
		}

		public static DArg ofSafe(String param) {
			DArg darg = new DArg(param);
			if (!darg.isValid()) {
				return null;
			}
			return darg;
		}

		public static String[] pareFromArray(String[] args, String nameArg) {
			try {
				for (String ar : args) {
					String[] pare = pareFromDarg(ar);
					if (nameArg.equals(pare[0])) {
						return pare;
					}

				}
			} catch (Exception ex) {
			}
			return new String[]{null, null};
		}

		public boolean isValid() {
			return !(name == null && value == null);
		}

		public static boolean isDarg(String param) {
			try {
				String[] pare = pareFromDarg(param);
				return pare[0] != null && pare[1] != null;
			} catch (Exception ex) {
				return false;
			}
		}

		private static String[] pareFromDarg(String arg) {
			try {
				String name = arg.split("\\d++")[0];
				String value = arg.substring(name.length());
				if (value.matches("\\d+")) {
					if (name.endsWith("-")) {
						name = name.substring(0, name.length() - 1);
						return new String[]{name, "-" + value};
					} else {
						return new String[]{name, value};
					}
				}
			} catch (Exception ex) {
			}
			return new String[]{null, null};
		}
	}
}
