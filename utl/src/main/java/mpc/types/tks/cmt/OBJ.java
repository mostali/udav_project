package mpc.types.tks.cmt;

import mpu.core.ARG;
import mpu.core.EQ;
import mpc.rfl.R;
import mpc.str.ObjTo;

public class OBJ<O> {

	public static final ANY ANY = new ANY(null);
	public static final STR STR = new STR(null);
	public static final LNG LNG = new LNG(null);
	public static final CMD1 CMD1 = new CMD1(null);
	public static final CMD5 CMD5 = new CMD5(null);
	public static final CMD6 CMD6 = new CMD6(null);
	public static final CMD7 CMD7 = new CMD7(null);

	final Class<O> clazz;
	final O val;

	public O get() {
		return val;
	}

	public String str(String... defRq) {
		if (val != null) {
			return val.toString();
		}
		return ARG.toDefRq(defRq);
	}

	@Override
	public String toString() {
		return "OBJ{" +
				"clazz=" + clazz +
				", val=" + val +
				'}';
	}

	public OBJ(Class<O> clazz) {
		this(clazz, null);
	}

	public OBJ(Class<O> clazz, O val) {
		this.val = val;
		this.clazz = clazz;
	}

	public static <K> OBJ<K> of(K key) {
		return key == null ? (OBJ<K>) ANY : new OBJ(key.getClass(), key);
	}

	public OBJ newObj(O obj) {
		return new OBJ(clazz, obj);
	}

	public boolean eq(Object obj) {
		return EQ.equalsSafe(obj, val);
	}

//	public boolean eq(String obj, boolean throwError, boolean... ignoreCase) {
//		return EQ.equalsSafe(obj, val);
//	}

	public boolean eqUnsafe(Object obj) {
		return EQ.equalsUnsafe(obj, val);
	}

	public String toDebugStringTypeVal() {
		return val == null ? "" : " '" + val + "' (" + R.sn(clazz) + ") ";
	}

	public boolean isEmpty() {
		return val == null;
	}

	public boolean isPresent() {
		return !isEmpty();
	}

	public <T> T as(Class<T> asType, T... defRq) {
		return ObjTo.objTo(val, asType, defRq);
	}

	public Long asLong(Long... defRq) {
		return as(Long.class, defRq);
	}

	public Integer asInt(Integer... defRq) {
		return as(Integer.class, defRq);
	}

	public Boolean asBool(Boolean... defRq) {
		return as(Boolean.class, defRq);
	}

	public String asStr(String... defRq) {
		return as(String.class, defRq);
	}

	public static class ANY extends OBJ<Object> {
		public ANY() {
			super(Object.class);
		}

		public ANY(Object val) {
			super(Object.class, val);
		}
	}

	public static class STR extends OBJ<String> {
		public STR() {
			super(String.class);
		}

		public STR(String val) {
			super(String.class, val);
		}
	}

	public static class LNG extends OBJ<Long> {
		public LNG() {
			super(Long.class);
		}

		public LNG(Long val) {
			super(Long.class, val);
		}
	}

	public static class CMD1 extends OBJ<Cmd1> {
		public CMD1() {
			super(Cmd1.class);
		}

		public CMD1(Cmd1 val) {
			super(Cmd1.class, val);
		}
	}

	public static class CMD5 extends OBJ<Cmd5> {
		public CMD5() {
			super(Cmd5.class);
		}

		public CMD5(Cmd5 val) {
			super(Cmd5.class, val);
		}
	}

	public static class CMD6 extends OBJ<Cmd6> {
		public CMD6() {
			super(Cmd6.class);
		}

		public CMD6(Cmd6 val) {
			super(Cmd6.class, val);
		}
	}

	public static class CMD7 extends OBJ<Cmd7> {
		public CMD7() {
			super(Cmd7.class);
		}

		public CMD7(Cmd7 val) {
			super(Cmd7.class, val);
		}
	}
}
