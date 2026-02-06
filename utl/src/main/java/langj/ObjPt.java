package langj;

import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.IT;
import mpu.core.EQ;
import mpe.core.P;
import mpc.types.abstype.AbsType;
import mpc.exception.NI;
import mpc.rfl.RFL;
import mpu.Sys;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjPt {

	public static final Logger L = LoggerFactory.getLogger(ObjPt.class);

	public final Host host;

	public final Ctx ctx;
	public final String code;
	private ECode _ecode;

	public Integer index_last = null;

	public Class clazz;
	public String class_name;
	public String method;
	public String field;
	public String link;
	public CharSequence body;
	public String num;
	public String string;
	public String ch;


	public ECode ecode() {
//		if (_ecode == null) {
//			_ecode = ECode.firstType(host, ctx, code);
//		}
		if (_ecode == null) {
			throw new PtException(code, "need ecode");
		}
		return _ecode;
	}

	public ObjPt(String code, ECode ecode) {
		this(code, ecode, Host.NULL, new Ctx());
	}

	public ObjPt(String code, ECode ecode, Host host, Ctx ctx) {
		this.host = IT.NN(host);
		this.ctx = ctx;
		this.code = code;
		this._ecode = ecode;
	}

	public static void main(String[] args) {

		ObjPt objPt = new ObjPt("new TTTTT()", ECode.NEW);
		ObjPt objPt1 = ECode.cutClass("");
		Host type1 = objPt.toType();
		P.exit(type1);
//		U.exit(eval("new lj2.JTest()"));
		Sys.exit(Host.eval("new lj2.JTest().simple_method()"));
//		U.exit(METHOD.matches(" hello()"));
	}

	private Host type = null;

	public Host toType() {
		if (type != null) {
			return type;
		}
		ECode eCode = ecode();
		Host _type = null;
		switch (eCode) {
			case NUM:
				ObjPt numStr = eCode.firstObj(code, host, ctx);
				if (numStr.code.contains(".")) {
					_type = Host.ofClass(Double.parseDouble(numStr.code), Double.class);
				} else {
					_type = Host.ofClass(Long.parseLong(numStr.code), Long.class);
				}
				break;
			case STRING:
				_type = Host.ofClass(string, String.class);
				break;
			case CHAR:
				_type = Host.ofClass(ch.charAt(0), Character.class);
				break;
			case BODY_RB: {
//				String body = eCode.firstCode(code);
//				if ("".equals(body)) {
//					throw new PtException(code, "empty body");
//				}
				break;
			}
			case CLASS:
				_type = Host.ofClass(IT.NN(clazz), clazz);
				break;
			case LINK:
				_type = Host.getLink(ctx, IT.NN(link));
				break;
			case FIELD:
				_type = host.evalFieldAny(field);
				break;
			case METHOD:
				checkNullType();
				try {
					Method declaredMethod = host.type().getMethod(method);
					Object result = null;
					if (RFL.isStatic(declaredMethod)) {
						result = declaredMethod.invoke(null);
					} else {
						result = declaredMethod.invoke(host.val());
					}
					Class returnType = declaredMethod.getReturnType();
					_type = Host.ofClass(result, returnType);
				} catch (NoSuchMethodException e) {
					throw new PtException(code, e);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new PtException(code, e);
				}
				break;
			case NEW:
				ObjPt bodyObjPt = getBodyObjPt();
				String body = bodyObjPt.getBodyUnwrapString();
				if (X.blank(body)) {
					_type = new Host(RFL.instEmptyConstructor(clazz), clazz);
					break;
				}
				String args = body;
				List<AbsType> bodyArgs = new ArrayList<>();
				do {
					ECode argType = ECode.firstType(args, ctx);
					ObjPt argObj = argType.firstObj(body, ctx);
					bodyArgs.add(argObj.toType());
					args = argObj.nextString().trim();
				} while (X.notBlank(args));
				try {
					Object inst = Refl.inst_(clazz, bodyArgs);
					_type = Host.ofClass(inst, clazz);
				} catch (IllegalArgumentException e) {
					throw new PtException(code, e, bodyArgs.toString());
				} catch (Exception e) {
					throw new PtException(code, e);
				}
				break;

			case ACTION:
			default:
				throw new NI(eCode + ":" + code + ":" + this);
		}
		return type = IT.notNull(_type);
	}

	/**
	 * *************************************************************
	 * ---------------------------- CHECK's -----------------------
	 * *************************************************************
	 */

	private void checkNullType() {
		if (host.isNull()) {
			throw new PtException(code, "Code typeof '%s', but host is null-type", ecode());
		}
	}

	private void checkNullValue() {
		if (host.val() == null) {
			throw new PtException(code, "Code typeof '%s', but host-value is null", ecode());
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- SET -----------------------
	 * *************************************************************
	 */
	public ObjPt clazz(String className, int index_last) {
		this.class_name = className;
		index_last(index_last);
		return this;
	}

	public ObjPt clazz(Class clazz, int index_last) {
		this.clazz = clazz;
		index_last(index_last);
		return this;
	}

	public ObjPt method(String method) {
		this.method = method;
		updateIndex(method);
		return this;
	}

	public ObjPt field(String field) {
		this.field = field;
		updateIndex(field);
		return this;
	}

	public ObjPt link(String link) {
		this.link = link;
		updateIndex(link);
		return this;
	}

	public ObjPt body(CharSequence body, int index_brc) {
		this.body = body;
		index_last(index_brc);
		return this;
	}

	public ObjPt num(String num) {
		this.num = num;
		updateIndex(num);
		return this;
	}

	public ObjPt string(String str) {
		this.string = str;
		updateIndex(str);
		return this;
	}

	public ObjPt ch(String str) {
		this.ch = str;
		updateIndex(str);
		return this;
	}

	public ObjPt updateIndex(CharSequence obj) {
		this.index_last = obj.length() - 1;
		return this;
	}

	public ObjPt index_last(int index_last) {
		this.index_last = index_last;
		return this;
	}

	/**
	 * *************************************************************
	 * ---------------------------- API -----------------------
	 * *************************************************************
	 */

	private String nextString() {
		if (!hasNextSymbol()) {
			return "";
		}
		return code.substring(index_last + 1);
	}

//	public boolean isEmpty() {
//		switch (ecode()) {
//			case BODY_RB:
//				return X.blank(getBodyUnwrapString());
//			default:
//				throw new NI(ecode());
//		}
//	}

	public String[] getTypeName() {
		if (clazz != null) {
			return new String[]{"class", clazz.getName()};
		} else if (class_name != null) {
			return new String[]{"class", class_name};
		} else if (method != null) {
			return new String[]{"method", method};
		}
		throw new NI();
	}

	private Optional<ObjPt> bodyArgs = null;

	public ObjPt getBodyObjPt(ObjPt... defRq) {
		if (bodyArgs != null) {
			ObjPt args = bodyArgs.get();
			if (args != null) {
				return args;
			}
		} else {
			ECode ecode = ecode();
			ECode[] objects = {ECode.BODY_RB, ECode.METHOD, ECode.NEW};
			if (EQ.notEqualsAny(ecode, true, objects)) {
				throw new PtException(code, "Body Pattern support only: " + Arrays.asList(objects));
			}
			String[] tn = getTypeName();
			if (!hasNextSymbol(1)) {
				throw new PtException(code, "Object %s '%s'. BODY not found (small length) from index [%s]", tn[0].toUpperCase(), tn[1], index_last);
			}
			String bodyArgsAll = code.substring(index_last + 1).trim();
			ECode bodyType = ECode.firstType(bodyArgsAll);
			if (bodyType != ECode.BODY_RB) {
				throw new PtException(code, "Object %s '%s'. BODY type incorrect (%s) from index [%s]", tn[0].toUpperCase(), tn[1], bodyType, index_last);
			}
			ObjPt objArgs = bodyType.firstObj(bodyArgsAll, ctx);
			bodyArgs = Optional.of(objArgs);
			return objArgs;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		String[] tn = getTypeName();
		throw new PtException(code, "Body Args not found for type %s '%s' from index [%s]", tn[0].toUpperCase(), tn[1], index_last);
	}

	public String getBodyUnwrapString() {
		String clean = clean();
		return clean.substring(1, clean.length() - 1);
	}

	private String clean = null;

	public String clean() {
		if (clean != null) {
			return clean;
		}
		return clean = code.substring(0, index_last + 1);
	}

	public boolean isLastIndex() {
		return code.length() - 1 == IT.isPosOrZero(index_last);
	}

	public boolean hasNextSymbol(int... plusLast) {
		return index_last + ARGn.toDefOr(0, plusLast) < code.length() - 1;
	}

	private String nextCode = null;

	public String nextCode() {
		if (nextCode != null) {
			return nextCode;
		}
		String nextCode = code.substring(index_last + 1);
		if (X.sizeOf(nextCode) <= 1) {
			throw new PtException(nextCode, "Next Type impossible");
		} else if (nextCode.charAt(0) != '.') {
			throw new PtException(nextCode, "Next Type impossible. First char must be '.'");
		}
		return this.nextCode = nextCode.substring(1);
	}

	@Override
	public String toString() {
		return "ObjPt{" +
				"_ecode=" + _ecode +
				", index_last=" + index_last +
				", clean='" + clean + '\'' +
				", code='" + code + '\'' +
				", host=" + host +
				", ctx=" + ctx +
				", clazz=" + clazz +
				", class_name=" + class_name +
				", method='" + method + '\'' +
				", body=" + body +
				", num='" + num + '\'' +
				", string='" + string + '\'' +
				", ch='" + ch + '\'' +
				", bodyArgs=" + bodyArgs +
				'}';
	}
}
