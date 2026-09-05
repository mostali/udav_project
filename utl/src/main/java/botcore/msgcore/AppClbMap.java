package botcore.msgcore;

import mpc.map.IGetterAs;
import mpc.map.MAP;
import mpc.types.ruprops.URuProps;
import mpu.IT;
import mpu.core.ARG;
import mpu.str.UST;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public abstract class AppClbMap extends ClbMap {

	public static final String EMSG_ID = "-";
	public static final String RMSG_ID = "_";
	public static final String IDMODEL = "i";

	public AppClbMap(Map ctx) {
		super(ctx);
	}

	public AppClbMap setEmsgId(Integer val) {
		put(EMSG_ID, val);
		return this;
	}

	public AppClbMap setRmsgId(Integer val) {
		put(RMSG_ID, val);
		return this;
	}

	public AppClbMap setIdModel(Long idModel) {
		put(IDMODEL, idModel);
		return this;
	}

	public Long getIdModel(Long... defRq) {
		return getAsLong(IDMODEL, defRq);
	}

	public Integer getEmsgId(Integer... defRq) {
		return getAsInt(EMSG_ID, defRq);
	}

	public Integer getRmsgId(Integer... defRq) {
		return getAsInt(RMSG_ID, defRq);
	}

}
