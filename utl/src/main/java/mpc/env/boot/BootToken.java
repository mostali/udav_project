//package mpc.env.boot;
//
//import lombok.RequiredArgsConstructor;
//import mpc.env.APP;
//import mpc.types.AtomicObj;
//import mpc.env.Env;
//import mpc.exception.FIllegalStateException;
//import mpc.exception.RequiredRuntimeException;
//import mpc.rfl.RFL;
//import mpc.log.L;
//import mpc.types.opts.SeqOptions;
//import mpc.types.ruprops.RuProps;
//import mpu.core.ARR;
//import mpu.core.ARG;
//import mpu.IT;
//
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//public class BootToken<T> {
//
//	final String key;
//	final Class<T> type;
//	final boolean required;
//
//	private Optional<T> bootTokenValue = null;
//
//	//
//	private boolean dblSeqRunOpts = false;
//	//
//	private String tlp;
//
//	//
//	private T def = null;
//	//
//	private Class setToClass;
//	private String setToClassField;
//	//
//	private T[] setToArray;
//	private int setToArrayIndex;
//	//
//	private Class setToObj;
//	private String setToObjField;
//	//
//	private AtomicObj atomicValue;
//	//
//	private RFL.CallToMethod callToClass;
//
//	//
//	//
//	public static BootToken RQ(String key, Class... type) {
//		return of(key, ARG.toDefOr(String.class, type), true);
//	}
//
//	public static BootToken OPT(String key, Class... type) {
//		return of(key, ARG.toDefOr(String.class, type), false);
//	}
//
//	public static BootToken of(String key, boolean... required) {
//		return of(key, String.class, required);
//	}
//
//	public static BootToken of(String key, Class type, boolean... required) {
//		return new BootToken(key, type, ARG.isDefEqTrue(required));
//	}
//
//	/**
//	 * *************************************************************
//	 * ------------------------ INIT -----------------------
//	 * *************************************************************
//	 */
//	public static void init(Class appClass, Object... froms) {
//		List<BootToken> bootTokens = RFL.fieldValuesSt(appClass, BootToken.class, null, true, null);
//		if (bootTokens == null) {
//			L.info("BootToken's is empty from app-boot-class '{}'", appClass);
//			return;
//		}
//		for (BootToken bootToken : bootTokens) {
//			bootToken.init(froms);
//		}
//	}
//
//	public T init(Object... froms) {
//		T val = get(froms);
//		if (setToClass != null) {
//			IT.notNull(setToClassField);
//			RFL.fieldValueStSet(setToClass, setToClassField, val, true);
//		} else if (setToObj != null) {
//			IT.notNull(setToObjField);
//			RFL.fieldValueSet(setToObj, setToObjField, val, true);
//		} else if (setToArray != null) {
//			ARR.isIndex(setToArrayIndex, setToArray);
//			setToArray[setToArrayIndex] = val;
//		} else if (atomicValue != null) {
//			atomicValue.set(val);
//		} else if (callToClass != null) {
//			callToClass.call(val);
//		}
//		return val;
//	}
//
//	/**
//	 * *************************************************************
//	 * ------------------------ SET TO -----------------------
//	 * *************************************************************
//	 */
//	public void setDblSeqRunOpts(boolean dblSeqRunOpts) {
//		if (dblSeqRunOpts && !type.isAssignableFrom(Boolean.class)) {
//			throw new FIllegalStateException("dblSeqRunOpts need Boolean type");
//		}
//		this.dblSeqRunOpts = dblSeqRunOpts;
//
//	}
//
//	public BootToken<T> def(T def) {
//		this.def = def;
//		return this;
//	}
//
//	public BootToken tlp(String tlp) {
//		this.tlp = tlp;
//		return this;
//	}
//
//	public BootToken setTo(T[] array, int ind) {
//		this.setToArray = array;
//		this.setToArrayIndex = ind;
//		return this;
//	}
//
//	public BootToken setTo(RFL.CallToMethod callToClass) {
//		this.callToClass = callToClass;
//		return this;
//	}
//
//	public BootToken setTo(Class toClass, String toClassField) {
//		this.setToClass = toClass;
//		this.setToClassField = toClassField;
//		return this;
//	}
//
//	public BootToken setTo(AtomicObj atomicValue) {
//		this.atomicValue = atomicValue;
//		return this;
//	}
//
//	public T get(Object... froms) {
//		if (bootTokenValue != null) {
//			return bootTokenValue.get();
//		}
//		out:
//		for (Object from : froms) {
//			if (from == null) {
//				continue;
//			} else if (from instanceof SeqOptions) {
//				bootTokenValue = getFromSeqRunOptions((SeqOptions) from, dblSeqRunOpts, key, type);
//				if (bootTokenValue != null) {
//					break out;
//				}
//			} else if (from instanceof RuProps) {
//				bootTokenValue = getFromRuProps((RuProps) from, key, type);
//				if (bootTokenValue != null) {
//					break out;
//				}
//			}
//		}
//		if (bootTokenValue == null) {
//			bootTokenValue = getFromTlp(tlp, key, type);
//		}
//
//		if (bootTokenValue == null && def != null) {
//			if (def instanceof BootToken) {
//				bootTokenValue = (Optional<T>) Optional.of(((BootToken) def).get());
//			} else {
//				bootTokenValue = Optional.of(def);
//			}
//		}
//		if (required && bootTokenValue == null) {
//			throw new RequiredRuntimeException(key);
//		}
//		return bootTokenValue.get();
//	}
//
//	/**
//	 * *************************************************************
//	 * ------------------------ GET FROM INIT -----------------------
//	 * *************************************************************
//	 */
//
//	private static <T> Optional<T> getFromTlp(String tlp, String key, Class<T> type) {
//		Optional opt = null;
//		if (tlp != null) {
//			T val = Env.getTlpValType(tlp, type, null);
//			if (val != null) {
//				opt = Optional.of(val);
//			}
//		}
//		if (L.isTraceEnabled()) {
//			logOpt("Tlp", opt, key, type, tlp, null);
//		}
//		return opt;
//	}
//
//	private static <T> Optional<T> getFromRuProps(RuProps ruProps, String key, Class<T> type) {
//		T val = ruProps.getAsType(key, type, null);
//		Optional opt = null;
//		if (val != null) {
//			opt = Optional.of(val);
//		}
//		if (L.isTraceEnabled()) {
//			logOpt("RuProps", opt, key, type, null, null);
//		}
//		return opt;
//	}
//
//	private static <T> Optional<T> getFromSeqRunOptions(SeqOptions from, boolean dblSeqRunOpts, String key, Class<T> type) {
//		Optional opt = null;
//		if (dblSeqRunOpts) {
//			Boolean bool = from.hasDouble(key, null);
//			if (bool != null) {
//				opt = Optional.of((T) bool);
//			}
//		} else {
//			T val = from.getSingleAs(key, type, null);
//			if (val != null) {
//				opt = Optional.of(val);
//			}
//		}
//		if (L.isTraceEnabled()) {
//			logOpt("Seq", opt, key, type, null, dblSeqRunOpts ? "--" : "-");
//		}
//		return opt;
//	}
//
//	private static <T> void logOpt(String typeBT, Optional opt, String key, Class<T> type, String tlp, String dblSeqRunOpts) {
//		boolean result = opt != null;
//		if (!result) {
//			return;
//		}
//		String pfx_active = result ? ">>>" : "";
//		String show_val = APP.IS_DEBUG_ENABLE ? "" : "=" + (opt == null ? null : opt.get());
//		String tlp_val = tlp != null ? "#" + tlp : "";
//		String dbl_opt = dblSeqRunOpts != null ? "(" + dblSeqRunOpts + ")" : "";
//		L.trace("{}BootToken#{}{}{}#{}:{}{}", pfx_active, typeBT, dbl_opt, tlp_val, type.getSimpleName(), key, show_val);
//	}
//
//}
