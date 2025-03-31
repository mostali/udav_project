package langj;

import lombok.SneakyThrows;
import mpu.IT;
import mpc.types.abstype.AbsType;
import mpc.exception.FIllegalStateException;
import mpc.rfl.AbsFieldType;
import mpu.str.ToString;
import mpc.str.sym.SYMJ;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Host<T> extends AbsType<T> {

	public static final Logger L = LoggerFactory.getLogger(Host.class);

	public static final Host NULL = Host.from(null, null, Host.class);

	public Host(T value) {
		super(UUID.randomUUID().toString(), value, value == null ? null : (Class<T>) value.getClass());
	}

	public Host(Host<T> value) {
		this(UUID.randomUUID().toString(), value);
	}

	public Host(String name, Number value) {
		super(name, value);
	}

	public Host(String name, String value) {
		super(name, value);
	}

	public Host(String name, Host value) {
		super(name, (T) value.val(), value.type());
	}

	public Host(String name, Object value, Class type) {
		super(name, (T) value, type);
	}

	public Host(Object value, Class type) {
		super(null, (T) value, type);
	}

	/**
	 * *************************************************************
	 * --------------------------- BUILDER's -----------------------
	 * *************************************************************
	 */

	public static <T> Host<T> from(String name, T value, Class<T> type) {
		return new Host(name, value, type);
	}

	public static <T> Host<T> ofObjectOrClass(T host) {
		return host == null ? NULL : new Host(host, host.getClass());
	}

	public static <T> Host<T> ofClass(T value, Class<T> type) {
		return new Host(null, value, type);
	}


	/**
	 * *************************************************************
	 * -------------------------- JAVA EVAL ------------------------
	 * *************************************************************
	 */

	public static Host eval(String code) {
		return eval(code, NULL, new Ctx(), 0);
	}

	public static Host eval(String code, Host host, Ctx ctx, int level) {
		ECode eCode = ECode.firstType(code, host, ctx);
		ObjPt objPt = eCode.firstObj(code, host, ctx);
		IT.state(objPt.ecode() != ECode.ACTION);
		Host hostType = objPt.toType();
		if (L.isInfoEnabled()) {
			String tab = level == 0 ? SYMJ.ARROW_RIGHT_SPEC : STR.repeat("  ", level);
			L.info("{}Host '{}' Code {}:'{}' >>> '{}', ctx [{}]", tab, host, eCode, code, hostType, ctx);
		}
		if (objPt.isLastIndex()) {
			return hostType;
		}
		String nextCode = objPt.nextCode();
		return eval(nextCode, hostType, ctx, ++level);
	}


	/**
	 * *************************************************************
	 * ----------------------------- API's -------------------------
	 * *************************************************************
	 */

	public static Host getLink(Ctx ctx, String link) {
		IT.isTrue(ctx.isLink(link));
		Object linkVal = ctx.get(link);
		if (linkVal instanceof Host) {
			return (Host) linkVal;
		}
		if (linkVal == null) {
			throw new NullPointerException("Unsupported NULL as NULL. Wrap as HostType.NULL");
		}
		return Host.ofObjectOrClass(linkVal);
	}

	public boolean isNull() {
		return this == NULL;
	}

	/**
	 * *************************************************************
	 * ---------------------------- CHECK's ------------------------
	 * *************************************************************
	 */

	public void checkNullType() {
		if (this == NULL) {
			throw new FIllegalStateException("Host is typeof NULL");
		}
	}

	public void checkNeedNullValue() {
		if (val() != null) {
			throw new FIllegalStateException("Host '%s' value must be null", type());
		}
	}

	public void checkNullValue() {
		if (val() == null) {
			throw new FIllegalStateException("Host '%s' value is null", type());
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- EVAL -----------------------
	 * *************************************************************
	 */

	@SneakyThrows
	public Host evalFieldAny(String field) {
		return evalFieldAny_(field);
	}

	public Host evalFieldAny_(String field) throws NoSuchFieldException, IllegalAccessException {
		checkNullType();
		AbsFieldType aft = AbsFieldType.any_(type(), val(), IT.notBlank(field));
		return Host.ofClass(aft.value_(), aft.type());
	}

	/**
	 * *************************************************************
	 * ---------------------------- TO STRING -----------------------
	 * *************************************************************
	 */

	@Override
	public String toString() {
		return type().getName() + "{" +
//			   "name='" + name() + '\'' +
			   "" + (getValueAsObject() instanceof CharSequence ? "'" + getValueAsObject() + "'" : getValueAsObject()) +
//			   ", type=" + type().getSimpleName() +
			   '}';
	}

	public boolean isLong() {
		return type() == Long.TYPE || type() == Long.class;
	}
}
