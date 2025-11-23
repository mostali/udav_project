package zk_notes.node_srv;

import lombok.Getter;
import mpc.env.APP;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpe.core.ERR;
import mpu.X;
import mpu.func.FunctionT;
import mpu.pare.Pare3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.SqlECS;
import zk_notes.node_state.ObjState;
import zk_os.sec.UO;

import java.util.Optional;

public class NodeEvalAction<N> {

	public static final Logger L = LoggerFactory.getLogger(NodeEvalAction.class);

	public static final FunctionT DEFAULT_OUT_ANY_OBJECT = evaluated -> ZKI.infoEditorDark("" + evaluated + "");

	private final @Getter NodeDir nodeDir;
	private final @Getter NodeEvalType evalType;
	private final @Getter ENV envType;

	private @Getter RESULT resultType;

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
				return String.valueOf(SqlECS.doSqlCall_VALUE(nodeDir, false));
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

	//
	//
	//
	//

	public static <T> Pare3<NodeDir, Optional<T>, Object> doEvalNodeAction(NodeDir node, FunctionT<NodeDir, T> evalFunction, FunctionT<T, Object> viewFunction) {

		ObjState state = node.state();

		state.deletePathFc_OkErr();

		T evaluate;

		try {

			UO.RUN.isAllowed(node, true);

			node.inject(true);

			evaluate = evalFunction.apply(node);

			if (evaluate == null) {
				L.info("doEventActionWebDefault return empty result for node " + node.nodeId());
			}
			state.writeFcDataOk(X.toStringNN(evaluate, ""));

			Object outView = viewFunction.apply(evaluate);

			return Pare3.of(node, Optional.ofNullable(evaluate), outView);

		} catch (Throwable ex) {
			String stackTrace = ERR.getStackTrace(ex);
			L.error("doEventActionWebDefault:" + node, ex);
			state.writeFcDataErr(stackTrace);
			if (APP.IS_DEBUG_ENABLE) {
				ZKI.errorEditorDark(stackTrace);
			} else {
				ZKI.infoAfterPointer(ex.getMessage(), ZKI.Level.WARN);
//				ZKI.alert(ex.getMessage());
			}
			return Pare3.of(node, Optional.ofNullable(null), ex);
		}

	}

}
