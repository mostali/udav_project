package mpz_deprecated.simple_task;

import mpu.Sys;
import mpe.UFlags;
import mpz_deprecated.EER;
import mpu.IT;

import java.util.Arrays;

@Deprecated
public abstract class AbsTask<TYPE, CONTEXT, ERROR extends Exception> {
	public static final String TP = ":::";
	protected int opts = 0;

	public static final int OPT__LOG = 1;

	public abstract TYPE apply(CONTEXT... objects) throws ERROR;

	public TYPE OPT__LOG__TRUE() {
		return OPT__SET(OPT__LOG);
	}

	public boolean OPT__IS(int opt) {
		return UFlags.isSet(opts, opt);
	}

	public TYPE OPT__SET(int opt) {
		opts = UFlags.set(opts, opt);
		return (TYPE) this;
	}

	public TYPE OPT__UNSET(int opt) {
		opts = UFlags.unset(opts, opt);
		return (TYPE) this;
	}

	private final String typeName;
	private final String typeTT;

	protected AbsTask(String typeTT, String typeName) {
		this.typeTT = typeTT;
		this.typeName = typeName;
	}

	public String getAbsTypeName() {
		return typeTT;
	}

	public String getTypeName() {
		return typeName;
	}

	public class ApplyContext {
		private final boolean isOne;
		public final CONTEXT[] srcs;

		public ApplyContext(CONTEXT... srcs) {
			this.srcs = srcs;
			isOne = srcs.length == 1;
		}

		public boolean isOne() {
			return isOne;
		}

		public boolean isEmpty() {
			return srcs.length == 0;
		}

		public void throwIfOneSrcArgument() {
			if (isOne) {
				throw EER.IS("Set dest argument");
			}
		}

		public void throwIfEmpty() {
			IT.isTrue(!ctx.isEmpty(), "Set Apply Object's");
		}
	}

	protected ApplyContext ctx = null;

	public void applyContext(CONTEXT... srcs) {
		ctx = new ApplyContext(srcs);
		applyLog();
	}

	private void applyLog_NU(Object absType, Object nestedType, Object... srcs) {
		if (true) {
			if (OPT__IS(OPT__LOG)) {
				String mes = Arrays.asList(srcs).toString();
				Sys.p(getClass().getSimpleName() + TP + absType + TP + nestedType + TP + mes);
			}
		}
	}

	private void applyLog() {
		if (OPT__IS(OPT__LOG)) {
			Sys.p(getApplyLog());
		}
	}

	public String getApplyLog() {
		String mes = Arrays.asList(this.ctx.srcs).toString();
		return getClass().getSimpleName() + TP + getAbsTypeName() + TP + getTypeName() + TP + mes;
	}
}
