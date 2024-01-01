package mpf.ns.space.oper;

import lombok.RequiredArgsConstructor;
import mpc.args.ARG;
import mpc.ERR;
import mpf.ns.space.core.ISs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class BaseNsOp<R> {

	public static final Logger L = LoggerFactory.getLogger(BaseNsOp.class);

	private final ISs iSpaceSrc;
	private Throwable error;

	public ISs ss() {
		return iSpaceSrc;
	}

	protected R result = null;

	public R doOp() {
		ERR.state(result == null, "doOp expired");
		try {
			doOpImpl();
		} catch (Throwable ex) {
			if (!skipThrow) {
				throw ex;
			}
			error = ex;
		}
		return result;
	}

	public abstract void doOpImpl();


	private boolean skipThrow = false;

	public BaseNsOp skipThrow(boolean... skipThrow) {
		this.skipThrow = ARG.isDefNotEqFalse(skipThrow);
		return this;
	}

	protected Boolean mkdirs_mkdir_not = null;

	public BaseNsOp mkdirs_mkdir_not(boolean... mkdirs_mkdir_not) {
		this.mkdirs_mkdir_not = ARG.isDefNotEqFalse(mkdirs_mkdir_not);
		return this;
	}

}
