package zk_page.node_state;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.json.GsonMap;
import mpc.str.ObjTo;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_page.ZKS;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RequiredArgsConstructor
public abstract class EntityState implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(EntityState.class);

	public static final String PK_USER = "user";
	public static final String PK_TITLE = "title";
	public static final String PK_VIEW = "view";
	public static final String PK_STATE = "state";


	protected final Pare<String,String> sdn;

	protected final boolean isForm;//eq isJson

	//
	//
	// CONTRACT

	public boolean hasPropNotEmpty(String prop) {
		return X.notEmpty(get(prop, null));
	}

	public abstract void updatePropSingle(String prop, Object value);

	public abstract String get(String prop, String... defRq);

	public abstract <T> T getAs(String prop, Class<T> asType, T... defRq);

	public abstract Boolean hasPropEnable(String prop, Boolean... defRq);

}
