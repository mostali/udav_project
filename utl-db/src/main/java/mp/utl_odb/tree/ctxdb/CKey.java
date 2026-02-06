package mp.utl_odb.tree.ctxdb;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.query_core.QP;
import mpe.core.OPR;
import mpu.str.UST;

@RequiredArgsConstructor
public class CKey {
	final String val;

	public static CKey of(String value) {
		return new CKey(value);
	}

	public String colName() {
		return getClass() == CKey.class ? "key" : getClass().getSimpleName().toLowerCase();
	}

	public QP pEQ() {
		return QP.pEQ(colName(), val);
	}

	public QP pLIKE() {
		return QP.param(colName(), val, OPR.LIKE);
	}

	public QP p_LIKE_() {
		return QP.param(colName(), QP.likeWrapVal(val), OPR.LIKE);
	}

	public Long asLong(Long... defRq) {
		return UST.LONG(val, defRq);
	}

	public String colVal() {
		return val;
	}

	public static class Val extends CKey {
		public Val(String val) {
			super(val);
		}

		public static Val of(String value) {
			return new Val(value);
		}

		public String colName() {
			return "value";
		}
	}

	public static class Ext extends CKey {
		public Ext(String val) {
			super(val);
		}

		public static Ext of(String value) {
			return new Ext(value);
		}
	}

	public static class O1 extends CKey {
		public O1(String val) {
			super(val);
		}

		public static O1 of(String value) {
			return new O1(value);
		}
	}

	public static class O2 extends CKey {
		public O2(String val) {
			super(val);
		}

		public static O2 of(String value) {
			return new O2(value);
		}
	}

	public static class O3 extends CKey {
		public O3(String val) {
			super(val);
		}

		public static O3 of(String value) {
			return new O3(value);
		}
	}

	public static class O4 extends CKey {
		public O4(String val) {
			super(val);
		}

		public static O4 of(String value) {
			return new O4(value);
		}
	}

	public static class O5 extends CKey {
		public O5(String val) {
			super(val);
		}

		public static O5 of(String value) {
			return new O5(value);
		}
	}

	public static class O6 extends CKey {
		public O6(String val) {
			super(val);
		}

		public static O6 of(String value) {
			return new O6(value);
		}
	}

	public static class O7 extends CKey {
		public O7(String val) {
			super(val);
		}

		public static O7 of(String value) {
			return new O7(value);
		}
	}

	public static class O8 extends CKey {
		public O8(String val) {
			super(val);
		}

		public static O8 of(String value) {
			return new O8(value);
		}
	}

	public static class O9 extends CKey {
		public O9(String val) {
			super(val);
		}

		public static O9 of(String value) {
			return new O9(value);
		}
	}

	public static class O10 extends CKey {
		public O10(String val) {
			super(val);
		}

		public static O10 of(String value) {
			return new O10(value);
		}
	}

	public static class Time extends CKey {
		public Time(String val) {
			super(val);
		}

		public static Time of(String value) {
			return new Time(value);
		}

		public static Time of(Long ms) {
			return of(ms.toString());
		}
	}
}
