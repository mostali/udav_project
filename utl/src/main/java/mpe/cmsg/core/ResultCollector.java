package mpe.cmsg.core;

import mpu.core.ARG;
import mpu.str.Rt;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultCollector<R> extends ErrorCollector {

	public static final Logger L = LoggerFactory.getLogger(ResultCollector.class);

	protected R result;

	public void setResult(@NotNull R result) {
		this.result = result;
	}

	public R getResult(R... defRq) {
		return result != null ? result : ARG.throwMsg("before set result", defRq);
	}

	@Override
	public String toString() {
		return "ResultCollector{" +
				", result=" + getResult(null) + "\n" + STR.DELR + "" + Rt.buildReport(getErrors(), "errors") +
				'}';
	}
}
