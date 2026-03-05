package mpe.cmsg.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.IErrorsCollector;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UF;
import mpc.rfl.RFL;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.TKN;

import java.nio.file.Path;

public interface ICallMsg {

	default <T> Pare<CallMsg, T> evalMsg() {
		return null;
	}

	static INodeType defineNodeType(INode node, boolean strictValid, INodeType... defRq) {

		String nodeData = node.readNodeDataStr(null);

		String line0 = nodeData == null ? null : ARRi.firstLine(nodeData);

		String objMsgId = node.toObjId();

		if (X.empty(line0)) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' line0 is empty", objMsgId), defRq);
		}

		INodeType iNodeType = INodeType.findNodeTypeByLine0(line0, null);

		if (iNodeType == null) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' not found from std, line0 : %s", objMsgId, line0), defRq);
		}

		if (!strictValid) {
			return iNodeType;
		}

		//only check
		ICallMsg callMsgValid = iNodeType.stdDesc().newInstanceCallMsgValid(null, nodeData, null);
		if (callMsgValid == null) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' not deserialize from data", objMsgId), defRq);
		}

		if (!callMsgValid.isValidStrict()) {
			return ARG.throwErr(() -> new RequiredRuntimeException(callMsgValid.toErrCollector().getMultiOrSingleErrorOrNull(), "INodeType '%s' invalid", objMsgId), defRq);
		}
		return iNodeType;
	}

	String toObjMsgId(String... defRq);

	default boolean isValidStrict() {
		if (!(this instanceof IErrorsCollector)) {
			return false;
		}
		return ((IErrorsCollector) this).isValid();
	}

	default String iLine0(String... defRq) {
		return ARRi.firstLine(iNodeDataCached(), defRq);
	}

	default String[] iKeyTwo(String[]... defRq) {
		String line0 = iLine0(null);
		if (X.notEmpty(line0)) {
			String[] two = TKN.two(line0, ":", null);
			if (two != null) {
				return two;
			}
			return new String[]{line0, null};
		}
		return ARG.throwMsg(() -> X.f("CallMsg '%s' except two key from line0 %s", toObjMsgId(), line0), defRq);
	}

	default String iKeyDirty(String... defRq) {
		String line0 = iLine0(null);
		if (X.notEmpty(line0)) {
			return TKN.firstOr(line0, line0, ' ', ':', '\n');
		}
		return ARG.throwMsg(() -> X.f("CallMsg '%s' except dirty key from line0 %s", toObjMsgId(), line0), defRq);
	}


	default INodeDesc iDefineDescByKey(INodeDesc... defRq) {
		return INodeDesc.valueOf(iKeyDirty(), defRq);
	}

	static String findAnyKey(String line, String... defRq) {
		String s = TKN.firstOr(line, null, ' ', ':', '\n');
		if (X.notEmpty(s)) {
			return s;
		}
		return ARG.throwMsg(() -> X.f("Except first line key from %s", line), defRq);
	}


	String iNodeDataCached(boolean... fresh);

	default ProxyObjSrc getFromSrcProxy() {
		return new ProxyObjSrc(getFromSrc());
	}

	Object getFromSrc();

	default IErrorsCollector toErrCollector() {
		return this.toErrCollector();
	}


	@RequiredArgsConstructor
	public class ProxyObjSrc {

		public final Object fromSrc;

		public String toObjMsgId(String... defRq) {
			return toObjMsgId(fromSrc, defRq);
		}

		public static String toObjMsgId(Object fromSrc, String... defRq) {
			if (fromSrc instanceof INode) {
				return ((INode) fromSrc).toObjID().toString();
			} else if (fromSrc instanceof Path) {
				return UF.PFX_FILE + fromSrc;
			} else if (fromSrc instanceof String) {
				return (String) fromSrc;
			}
			return ARG.throwMsg(() -> X.f("Except objID key from src %s", fromSrc), defRq);
		}

		public boolean isPath() {
			return fromSrc instanceof Path;
		}

		public Path asPath(Path... defRq) {
			if (isPath()) {
				return (Path) fromSrc;
			}
			return ARG.throwMsg(() -> X.f("FromSrc [%s] except type Path", RFL.scn(fromSrc, null)), defRq);
		}

		public boolean isINode() {
			return fromSrc instanceof INode;
		}

		public INode asINode(INode... defRq) {
			if (isINode()) {
				return (INode) fromSrc;
			}
			return ARG.throwMsg(() -> X.f("FromSrc [%s] except type INode", RFL.scn(fromSrc, null)), defRq);
		}
	}
}
