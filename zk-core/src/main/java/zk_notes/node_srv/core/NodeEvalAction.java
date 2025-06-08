package zk_notes.node_srv.core;

import lombok.Getter;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpe.core.ERR;
import mpu.X;
import mpu.func.FunctionT;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.SqlCallService;
import zk_notes.node_state.FormState;

import java.util.List;
import java.util.Optional;

public class NodeEvalAction<N> {

	public static final FunctionT<Pare<Integer, Object>, Object> DEFAULT_OUT_EXEC_OBJECT = evaluated -> {
		if (evaluated instanceof List) {//class java.util.ArrayList cannot be cast to class mpu.pare.Pare (java.util.ArrayList
			return ZKI.infoEditorBw((List) evaluated);
		}
		return ZKI.infoEditorBwTitle(evaluated.keyStr(), "" + evaluated.val() + "");
	};

	public static final FunctionT DEFAULT_OUT_ANY_OBJECT = evaluated -> ZKI.infoEditorBw("" + evaluated + "");
	public static final FunctionT DEFAULT_OUT_VOID = evaluated -> evaluated;

	private final @Getter NodeDir nodeDir;
	private final @Getter NodeEvalType evalType;
	private final @Getter ENV envType;

	private @Getter RESULT resultType;

	//
	//

	//
	//

	public enum ENV {
		WEB, REST, TASK;
	}

	public enum RESULT {
		NATIVE, STRING;
	}

	public NodeEvalAction(NodeDir nodeDir, ENV env, RESULT string) {
		this.nodeDir = nodeDir;
		this.evalType = nodeDir.evalType(true);
		this.envType = env;
	}


	public String doEvalActionString() {
		NodeEvalType nodeEvalType = nodeDir.evalType(true);
		switch (nodeEvalType) {
			case SQL:
				return String.valueOf(SqlCallService.doSqlCall_VALUE(nodeDir, false));
			default:
				throw NI.stop(nodeEvalType);
		}
	}


	public String evalString() {
		switch (resultType) {
			case STRING:
				return doEvalActionString();
			case NATIVE:
				//
				NI.stop("wth");
				return doEvalActionString();
			default:
				throw new WhatIsTypeException(resultType);
		}
	}

//	class NodeEvalAction0 {
//
//	}

	//
	//
	//
	//

	public static <T> Pare3<NodeDir, Optional<T>, Object> doEvalNodeAction(NodeDir node, FunctionT<NodeDir, T> evalFunction, FunctionT<T, Object> viewFunction) {
		FormState state = node.state();
		state.deletePathFc_OkErr();

		T evaluate;

		try {

			node.nodeDataInjected(true);

			evaluate = evalFunction.apply(node);

			if (evaluate == null) {
				L.info("doEventActionWebDefault return empty result for node " + node.nodeId());
			}
			state.writeFcDataOk(X.toString(evaluate, ""));

			Object outView = viewFunction.apply(evaluate);

			return Pare3.of(node, Optional.ofNullable(evaluate), outView);

		} catch (Throwable ex) {
			String stackTrace = ERR.getStackTrace(ex);
			L.error("doEventActionWebDefault:" + node, ex);
			state.writeFcDataErr(stackTrace);
			ZKI.alert(stackTrace);
			return Pare3.of(node, Optional.ofNullable(null), ex);
		}

	}

}
