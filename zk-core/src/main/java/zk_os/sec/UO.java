package zk_os.sec;

import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpe.call_msg.core.INodeID;
import mpe.call_msg.core.ISpaceID;
import mpu.X;
import mpu.core.ARG;
import mpu.str.SPLIT;
import zk_form.notify.ZKI_Sec;
import zk_notes.node_state.EntityState;
import zk_os.AppZos;
import zk_os.coms.AFC;
import zk_os.db.net.WebUsr;

import java.util.List;
import java.util.function.Function;

public enum UO {
	USR, VIEW, EDIT, RUN;

	public  boolean isAllowedIfEmpty() {
		switch (this) {
			case USR:
			case VIEW:
			case EDIT:
			case RUN:
				return SecApp.IS_ALLOWED_IFEMPTY__USR;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public List<String> getPropList(EntityState entityState) {
		switch (this) {
			case VIEW:
			case EDIT:
			case RUN:
				return SPLIT.allByComma(entityState.get(propName(), ""));
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public String propName() {
		switch (this) {
			case USR:
				return SecApp.USER;
			case VIEW:
				return SecApp.SECV;
			case EDIT:
				return SecApp.SECE;
			case RUN:
				return SecApp.SECR;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public static Function<UO, String> getterErrorMessage = (oper) -> X.f("Access [%s] denied for user [%s]", oper, WebUsr.get().getAliasOrLogin());

	public static UO of(int operaion) {
		switch (operaion) {
			case SecApp.I_USR:
				return USR;
			case SecApp.I_SECV:
				return VIEW;
			case SecApp.I_SECE:
				return EDIT;
			case SecApp.I_SECR:
				return RUN;
			default:
				throw new WhatIsTypeException(operaion);
		}
	}

	public static FIllegalStateException throwAccessDeniedByAppMode(ISpaceID objID, UO oper) {
		if (AppZos.isDebugEnable()) {
			throw new NodeAccessDenied("Access denied to node '%s'", objID.toObjId());
		}
		throw new FIllegalStateException(getterErrorMessage.apply(oper));
	}

	public static boolean isAllowed_OPERATION(ISpaceID objId, UO operation, boolean... THROW) {
		boolean allowedNodeFormOperation = SecMan.isAllowed_OPERATION(WebUsr.get(), objId, AFC.SpaceType.of(objId), operation);
		if (allowedNodeFormOperation) {
			return true;
		} else if (ARG.isDefNotEqTrue(THROW)) {
			ZKI_Sec.infoBottomRightFast(getterErrorMessage.apply(operation));
			return false;
		}
		throw throwAccessDeniedByAppMode(objId, operation);
	}

	/// /
	/// /		if (isAnonim()) {
	/// /			if (ARG.isDefEqTrue(THROW)) {
	/// /				throw new FIllegalStateException("Action Forbidden");
	/// /			}
	/// /			return false;
	/// /		} else {
	/// /			return isAllowedRun(nodeDir, THROW);
	/// /		}
	//	}
	public static boolean isAllowed_VIEW(INodeID nodeDir, boolean... THROW) {
		return isAllowed_OPERATION(nodeDir, VIEW, THROW);
	}

	public static boolean isAllowed_EDIT(INodeID nodeDir, boolean... THROW) {
		return isAllowed_OPERATION(nodeDir, EDIT, THROW);
	}

	public static boolean isAllowed_RUN(INodeID nodeDir, boolean... THROW) {
		return isAllowed_OPERATION(nodeDir, RUN, THROW);
	}

	public int index() {
		switch (this) {
			case USR:
				return SecApp.I_USR;
			case VIEW:
				return SecApp.I_SECV;
			case EDIT:
				return SecApp.I_SECE;
			case RUN:
				return SecApp.I_SECR;
			default:
				throw new WhatIsTypeException(this);
		}
	}

	public boolean isAllowed(ISpaceID objID, boolean... THROW) {
		return isAllowed_OPERATION(objID, this, THROW);
	}


	public static class NodeAccessDenied extends FIllegalStateException {
		public NodeAccessDenied(String message, Object... args) {
			super(message, args);
		}

		public NodeAccessDenied(Throwable throwable, String message, Object... args) {
			super(throwable, message, args);
		}
	}
}
