package zk_old_core.coms;

import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_com.base_ctr.Span0;

import java.util.Map;

public abstract class AbsVF extends Span0 {

	public static final Logger L = LoggerFactory.getLogger(AbsVF.class);

	public final Map<String, Object> formProps;

	public AbsVF(Map<String, Object> formProps) {
			this.formProps = formProps;
	}

	public Map<String, Object> getFormProps(Map<String, Object>... defRq) {
		if (X.notEmpty(formProps)) {
			return formProps;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("FormRootProps from '%s' is null", cn()), defRq);
	}

	protected abstract void initImpl() throws Exception;

}
