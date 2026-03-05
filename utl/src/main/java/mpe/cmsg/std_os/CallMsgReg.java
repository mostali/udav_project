//package mpe.cmsg.std_os;
//
//import lombok.Getter;
//import lombok.SneakyThrows;
//import mpe.cmsg.core.CallMsg;
//import mpe.cmsg.core.INodeDesc;
//import mpe.cmsg.core.INodeType;
//import mpf.CallCmdLine;
//import mpu.X;
//import mpu.core.ARG;
//import mpu.core.RW;
//import mpu.pare.Pare;
//import mpu.str.SPLIT;
//import mpu.str.STR;
//import mpu.str.TKN;
//import mpu.str.UST;
//import org.jetbrains.annotations.NotNull;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//
//public class CallMsgReg extends CallMsg {
//
//	public static final String KEY = "reg";
//
////	public static boolean isValidKeyFirstLine(String msg) {
////		return STR.startsWith(msg, KEY + ":", true);
////	}
//
//	@Override
//	public Pare<CallMsgReg, INodeType> evalObj() {
//
//		CallMsgReg callMsgReg = this;
//
//		Class stdClass = callMsgReg.loadStdClass();
//		Class srvClass = callMsgReg.loadSrvClass();
//
//		String keyLc = callMsgReg.getRegStdTypeName();
//		String keyUc = keyLc.toUpperCase();
//
//		Map headersMap = callMsgReg.getHeaders_MAP();
//
//		INodeType nodeType = new INodeType() {
//			@Override
//			public String stdTypeUC() {
//				return keyUc;
//			}
//
//			@Override
//			public INodeDesc stdDesc() {
//				return new INodeDesc() {
//					@Override
//					public String stdTypeUC() {
//						return keyUc;
//					}
//
//					@Override
//					public Class stdTypeClass(Class... defRq) {
//						return stdClass != null ? stdClass : ARG.throwMsg("Except stdTypeClass", defRq);
//					}
//
//					@Override
//					public Class stdTypeSrvClass(Class... defRq) {
//						return srvClass != null ? srvClass : ARG.throwMsg("Except stdTypeSrvClass", defRq);
//					}
//
//					@Override
//					public String line0() {
//						return keyLc;
//					}
//
//					@Override
//					public Class sub0() {
//						return null;
//					}
//
//					@Override
//					public Object holder() {
//						return callMsgReg;
//					}
//
//					@Override
//					public Map<String, Object> props() {
//						return headersMap;
//					}
//				};
//			}
//		};
//
//		return Pare.of(callMsgReg, nodeType);
//
//	}
//
//	//	public String _getDst(String... defRq) {
////		return getHeaderValueByKey("dst", defRq);
////	}
//
////	private Class srv = null;
//
//	@SneakyThrows
//	public Class loadStdClass() {
//		String headerValueByKey = getHeaderValueByKey("std.type");
//		return Class.forName(headerValueByKey.trim());
//	}
//
//
//	@SneakyThrows
//	public Class loadSrvClass() {
//		String headerValueByKey = getHeaderValueByKey("srv.type");
//		return Class.forName(headerValueByKey.trim());
//	}
//
//	public List<String> _getDstTypes() {
//		return SPLIT.allBySpace(getHeaderValueByKey("dst.types", ""));
//	}
//
//	public Pare<Integer, String> _getDstUt() {
//		return Pare.of(getHeaderValueByKey("dst.ut.id", Integer.class), getHeaderValueByKey("dst.ut.tk"));
//	}
//
//	public Boolean getIsRandom(Boolean... defRq) {
//		return getHeaderValueByKey("dst.ut.id", Boolean.class, defRq);
//	}
//
//	public String getRegStdTypeName() {
//		return TKN.lastGreedy(line0, ":");
//	}
//
//	public enum TYPE {
//		VK
//	}
//
//	@Override
//	public TYPE subtype(Object... defRq) {
//		Map<String, Object> head = getHeaders_MAP();
//		if (head.keySet().stream().anyMatch(k -> k.startsWith("dst:g"))) {
//			return TYPE.VK;
//		}
//		return (TYPE) ARG.throwMsg(() -> X.f("Not found PublCallMsg"), defRq);
//	}
//
//	public CallMsgReg(String fullMsg) {
//		super(fullMsg, false);
//
//		if (X.empty(linesMsgHeadersAndBody())) {
//			addError("Empty msg");
//			return;
//		}
//
//		if (!STR.startsWith(line0, true, KEY + ":")) {
//			addError("Except first line with starts %s", KEY + ":");
//		}
//
//
//	}
//
//	@Override
//	public String toString() {
//		return "PublCallMsg{" + "msg='" + fileData + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", errs=" + X.sizeOf0(getErrors()) + '}';
//	}
//
//	public static CallMsgReg of(Path file) {
//		String msg = RW.readString(file);
//		CallMsgReg publCallMsg = of(msg);
//		publCallMsg.setFromSrc(file);
//		return publCallMsg;
//	}
//
//	public static CallMsgReg of(String msg) {
//		return (CallMsgReg) ofQk(msg).throwIsErr();
//	}
//
//	public static CallMsgReg ofQk(String msg) {
//		return new CallMsgReg(msg);
//	}
//
//	public static boolean isValid(String data) {
//		return CallMsgReg.of(data).isValid();
//	}
//
//	public static class SingleSrcLine extends CallCmdLine implements Comparable<SingleSrcLine> {
//
//		@Override
//		public String toString() {
//			return line0 + " " + STR.ARR_DEL_RIGHT + (isValid() ? "ok" : getMultiOrSingleErrorOrNullStr());
//		}
//
//		public final String link;
//
//		public Integer getLinkOid(Integer... defRq) {
//			Integer last = TKN.last(link, "/club", Integer.class, null);
//			return last != null ? -1 * last : ARG.throwMsg(() -> X.f("Except link oid from pattern: %s", last), defRq);
//		}
//
//		private final @Getter Integer searchDaysAgo;
//		private final @Getter Integer countPost;
//
//		public SingleSrcLine(String line) {
//			super(line);
//
//			if (line.startsWith("#")) {
//				addError("line is commment: " + line);
//				link = null;
//				searchDaysAgo = null;
//				countPost = null;
//				return;
//			}
//			String[] two = TKN.twoGreedy(line, " ", null);
//
//			if (!checkNotNull(two, "Illegal line format: %s", line)) {
//				link = null;
//				searchDaysAgo = null;
//				countPost = null;
//				return;
//			}
//
//			this.link = UST.URL(two[1], null);
//
//			if (!checkNotNull(this.link, "Except url from line: %s", line)) {
//				searchDaysAgo = null;
//				countPost = null;
//				return;
//			}
//
//			String[] params = TKN.twoGreedy(two[0], " ", null);
//
//			this.searchDaysAgo = UST.INT(params[0], null);
//			if (!checkNotNull(this.searchDaysAgo, "Except 'X:searchDaysAgo' as [ X? *:countPost *:url ] : %s", line)) {
//				countPost = null;
//				return;
//			}
//			this.countPost = UST.INT(params[1], null);
//			if (!checkNotNull(this.countPost, "Except 'X:countPost' as [ *:searchDaysAgo X? *:url ] : %s", line)) {
//				return;
//			}
//
//		}
//
//		@Override
//		public int compareTo(@NotNull CallMsgReg.SingleSrcLine singleDonorLine) {
//			return line0.compareTo(singleDonorLine.line0);
//		}
//
//	}
//}
