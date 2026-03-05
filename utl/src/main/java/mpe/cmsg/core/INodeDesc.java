package mpe.cmsg.core;

import lombok.SneakyThrows;
import mpc.exception.IErrorsCollector;
import mpc.map.MAP;
import mpc.map.MapTableContract;
import mpc.rfl.RFL;
import mpf.contract.IContract;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.TKN;

import java.util.Map;

public interface INodeDesc {

	Class stdTypeClass(Class... defRq);

	Class stdTypeSrvClass(Class... defRq);

	@SneakyThrows
	default <SRV> SRV stdTypeSrvInstance(SRV... defRq) {
		Class aClass = stdTypeSrvClass(null);
		if (aClass != null) {
			return (SRV) RFL.inst0(aClass);
		}
		return ARG.throwMsg(() -> X.f("NodeDesc '%s' except srvtype instance", stdTypeUC()), defRq);
	}

	static INodeDesc valueOf(String stdName, INodeDesc... defRq) {
		INodeDesc aClass = NodeDescCache.TYPES_CACHED().get(stdName);
		if (aClass != null) {
			return aClass;
		}
		return ARG.throwMsg(() -> X.f("NodeDesc '%s' stdtype undefined", stdName), defRq);
	}

	static String getNameFromClass(Class c, String... defRq) {
		String simpleName = c.getSimpleName();
		String simpleNameSfx = CallMsg.class.getSimpleName();
		if (simpleName.endsWith(simpleNameSfx)) {
			simpleName = TKN.firstGreedy(simpleName, simpleNameSfx);
			return simpleName;
		}
		return ARG.throwMsg(() -> X.f("Except name from CallMsg class '%s'", c), defRq);
	}


	String stdTypeUC();


	String line0();

	Class sub0();

	Object holder();

	Map<String, Object> props();

	static Map serializeJson(INodeDesc iNodeDesc) {
		Map stdtype = MAP.of(
				"stdtype", iNodeDesc.stdTypeUC(),//
				"class", iNodeDesc.stdTypeClass().getName(),//
				"line0", iNodeDesc.line0(),//
				"sub0", iNodeDesc.sub0(),//
				"props", iNodeDesc.props()//
		);
		String s = iNodeDesc.line0();
		if (X.notEmpty(s)) {
			stdtype.put("line0", s);
		}
		Class subClass = iNodeDesc.sub0();
		if (subClass != null) {
			stdtype.put("sub0", subClass.getClass().getName());
		}
		return stdtype;
	}

	default ICallMsg newInstanceCallMsgValid(Object fromSrc, String data, ICallMsg... defRq) {
		ICallMsg inst = newInstanceCallMsg(data);
		if (inst.isValidStrict()) {
			CallMsg m = (CallMsg) inst;
			m.setFromSrc(fromSrc);
			return inst;
		}
		Throwable multiOrSingleErrorOrNull = ((IErrorsCollector) inst).getMultiOrSingleErrorOrNull();
		return ARG.throwErrHead("ICallMsg except valid from | " + inst.toObjMsgId(), multiOrSingleErrorOrNull, defRq);
	}

	default ICallMsg newInstanceCallMsg(String data) {
		ICallMsg callMsg = (ICallMsg) RFL.inst(stdTypeClass(), ARR.of(String.class), ARR.of(data));
		return callMsg;
	}

	default INodeType toNodeType() {
		return NodeType.of(this);
	}

	default Map serializeJson() {
		return serializeJson(this);
	}


	public interface StdTypeIC extends IContract {

		default String getStdclass(String... defRq) {
			Map props = getProps(null);
			if (props != null) {
				String o = (String) props.get("stdclass");
				if (X.notEmpty(o)) {
					return o;
				}
			}
			return ARG.throwMsg(() -> X.f("NodeDesc '%s' except stdclass", this), defRq);
		}

		default Class getStdclass0(Class... defRq) {
			String stdclass0 = getStdclass(null);
			if (stdclass0 != null) {
				return RFL.clazz(stdclass0, defRq);
			}
			return ARG.throwMsg(() -> X.f("NodeDesc '%s' except stdclass type", this), defRq);
		}

		//
		//

		default String getSrvclass(String... defRq) {
			Map props = getProps(null);
			if (props != null) {
				String o = (String) props.get("srvclass");
				if (X.notEmpty(o)) {
					return o;
				}
			}
			return ARG.throwMsg(() -> X.f("NodeDesc '%s' except srvclass", this), defRq);
		}

		default Class getSrvclass0(Class... defRq) {
			String srvclass0 = getSrvclass(null);
			if (srvclass0 != null) {
				return RFL.clazz(srvclass0, defRq);
			}
			return ARG.throwMsg(() -> X.f("NodeDesc '%s' except srvclass type", this), defRq);
		}

		//
		//


		String getStdtype(String... defRq);

		String getLine0(String... defRq);

		String getSub0(String... defRq);

		Map getProps(Map... defRq);

		static StdTypeIC of(Map data) {
			return MapTableContract.buildContract_DefRq(data, StdTypeIC.class);
		}

		default String toStringSimple() {
			return X.f("%s : %s", getStdtype(), getClass());
		}
	}
}
