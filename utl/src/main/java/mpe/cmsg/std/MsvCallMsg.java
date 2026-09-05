package mpe.cmsg.std;

import lombok.SneakyThrows;
import mpe.cmsg.core.CallMsg;
import mpf.zbin.ZBin;
import mpe.str.QN;
import mpu.IT;
import mpu.X;
import mpu.core.*;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.TKN;
import mpu.str.UST;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//ZProj
public class MsvCallMsg extends CallMsg {

	public static boolean isEnableOnlyOneCycle(MsvCallMsg callMsg, boolean defaultValue) {
		return callMsg.getHeaderValueByKey("onlyOneCycle", Boolean.class, defaultValue);
	}

	public static MsvCallMsg ofAny(Object msg, boolean checkLazyInjectType, MsvCallMsg... defRq) {
		return ofAny(msg, checkLazyInjectType, MsvCallMsg.class, defRq);
	}

//	public static MsvCallMsg ofAny(Object msg, boolean checkLazyInjectType, MsvCallMsg... defRq) {
//		MsvCallMsg msvCallMsg;
//		if (msg instanceof MsvCallMsg) {
//			msvCallMsg = (MsvCallMsg) msg;
//			if (checkLazyInjectType) {
//				Object fromSrc = msvCallMsg.getFromSrc();
//				NodeData inject = INodeInjector.get().doInject((INode) fromSrc, null);
//				msvCallMsg = mpe.cmsg.std.MsvCallMsg.of(inject.readNodeDataStr());
//			}
//		} else if (msg instanceof INode) {
//			INode<?> src = (INode<?>) msg;
//			msvCallMsg = of(src.readNodeDataStr());
//			msvCallMsg.setFromSrc(src);
//			if (checkLazyInjectType) {
//				Object fromSrc = msvCallMsg.getFromSrc();
//				NodeData inject = INodeInjector.get().doInject((INode) fromSrc, null);
//				msvCallMsg = mpe.cmsg.std.MsvCallMsg.of(inject.readNodeDataStr());
//			}
//		} else if (msg instanceof CharSequence) {
//			String string = msg.toString();
//			if (checkLazyInjectType) {
//				NodeData inject = INodeInjector.get().doInject(new StringNode(string), null);
//				string = inject.readNodeDataStr();
//			}
//			msvCallMsg = of(string);
//		} else if (msg instanceof Path) {
//			Path src = (Path) msg;
//			if (checkLazyInjectType) {
//				NodeData inject = INodeInjector.get().doInject(new FileNode(src), null);
//				msvCallMsg = of(inject.readNodeDataStr());
//			} else {
//				msvCallMsg = of(src);
//			}
//			msvCallMsg.setFromSrc(src);
//		} else {
//			return ARG.throwMsg(() -> X.f("Except legal any object [%s]", msg), defRq);
//		}
//		return msvCallMsg;
//	}

	public Integer getFromDayDayAgo(Integer... defRq) {
		return getHeaderValueByKey("fromDayAgo", Integer.class, defRq);
	}

	public static class RunnerLocalMsv {
		@SneakyThrows
		public static void main(String[] args) {
//            DEMO_CALL_MSG = MsvCallMsg.of(Paths.get("/opt/appVol/bea/.planes/.index/msv/.forms/mlast/AppNotes.props"));
//            DEMO_CALL_MSG = MsvCallMsg.of(Paths.get("/opt/appVol/bea/.planes/.index/msv/.forms/mstore/AppNotes.props"));
			DEMO_CALL_MSG = MsvCallMsg.of(Paths.get("/opt/appVol/bea/.planes/.index/msv/.forms/pubLifeIsGood0/AppNotes.props"));
			Object call = DEMO_CALL_MSG.call(true);
			X.exit(call);
		}
	}

	//
	//
	//

	@Override
	public Object call(boolean throwIfHasError, Object... args) {

		Object rslt = ZBin.GSV.newJarCall().invokeMsg(getMsg());

		return rslt;
	}

	public static MsvCallMsg DEMO_CALL_MSG;

	static {
		try {
			DEMO_CALL_MSG = MsvCallMsg.of(Paths.get("/opt/appVol/bea/.planes/.index/msv/.forms/mstore/AppNotes.props"));
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			DEMO_CALL_MSG = null;
		}
	}

	//
	//

	public static final String KEY = "msv";
	public static final String LINE0 = "msv:";

	public final String args;

	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

	public final Method method;
	public final String key;

	@Override
	public Method subtype(Object... defRq) {
		return method;
	}

	public enum Method {
		UNDEFINED, MSTORE, MLAST, MPOSTSET, MPUBLISH, MVIEW, MSTATS;

		public static Method of(String name, Method... defRq) {
			return ENUM.valueOf(name, Method.class, true, defRq);
		}

		public boolean isUserActorRequired() {
			switch (this) {
				case MPUBLISH:
				case MSTATS:
					return true;

				case MSTORE:
				default:
//                    return true;
			}
			return false;
		}
	}

	public MsvCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			addError("Empty qz msg");
			method = Method.UNDEFINED;
			key = null;
			args = null;
			return;
		}

		String[] two_Method = TKN.two(line0, " ", null);
		if (two_Method == null) {
			addError("Except two arg Msv.METHOD + url, but came %s", line0);
			method = Method.UNDEFINED;
			key = null;
			args = null;
			return;
		}

		method = Method.of(two_Method[0].trim(), Method.UNDEFINED);
		if (method == Method.UNDEFINED) {
			addError("Except first Msv.METHOD from string %s", two_Method[0]);
			key = null;
			args = null;
			return;
		}

		String two_MethodBody = two_Method[1].trim();
		String[] two_owner = TKN.two(two_MethodBody, " ", null);
//
		key = two_owner == null ? two_MethodBody : two_owner[0].trim();

		args = two_owner == null ? null : two_owner[1].trim();
	}

	public String getArgs(String... defRq) {
		return ARG.throwNN(args, "except args", defRq);
	}

	public Integer getArgsAsInt(Integer... defRq) {
		return UST.INT(args, defRq);
	}

	public Integer getOwnerId(Integer... defRq) {
		return ARG.throwNN(UST.INT(key, null), () -> X.f("except ownerId from [%s]", this), defRq);
	}

	public Integer getFROM_fromLine0(Integer... defRq) {
		String s = iLine0();
		List<String> valuesOfKey = getTokenStartsWith(s, QN.FROM, true, true);
		IT.hasLength(valuesOfKey, 1, "Except only one owner from line [%s]", s);
		return ARRi.firstAs(valuesOfKey, Integer.class, defRq);
	}


	public static List<String> getTokenStartsWith(String line0, String tknKey, boolean ic, boolean onlyFirst, Predicate<String>... lineTknFilter) {
		Predicate<String> filter = ARG.toDefOrNull(lineTknFilter);
		List<String> strings = SPLIT.allBySpace(line0);
		Stream<String> stringStream = strings.stream().map(lineToken -> {
			if (filter != null && !filter.test(lineToken)) {
				return null;
			}
			String[] kv = TKN.two(lineToken, ":", null);
			return kv != null && X.equals(tknKey, kv[0], ic) ? kv[1] : null;
		}).filter(X::NN);
		if (onlyFirst) {
			Optional<String> first = stringStream.findFirst();
			strings = first.isPresent() ? ARR.as(first.get()) : ARR.EMPTY_LIST;
		} else {
			strings = stringStream.collect(Collectors.toList());
		}
		return strings;
	}

	//
	//

	@Override
	public String toString() {
		return "MsvEvalMsg{" + "msg='" + msg + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", method=" + method + ", key=" + key +
//                ", class=" + jobClassName +
				", args=" + args + '}';
	}

	public static MsvCallMsg of(Path file) {
		MsvCallMsg msvCallMsg = of(RW.readString(file));
		msvCallMsg.setFromSrc(file);
		return msvCallMsg;
	}

	public static MsvCallMsg of(String msg) {
		return (MsvCallMsg) ofQk(msg).throwIsErr();
	}

	public static MsvCallMsg ofQk(String msg) {
		return new MsvCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return MsvCallMsg.of(data).isValid();
	}

}
