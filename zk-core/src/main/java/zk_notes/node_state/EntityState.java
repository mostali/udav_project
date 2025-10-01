package zk_notes.node_state;

import lombok.RequiredArgsConstructor;
import mpe.str.CN;
import mpu.X;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@RequiredArgsConstructor
public abstract class EntityState implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(EntityState.class);

	public static final String PK_USER = CN.USER;
	public static final String PK_TITLE = CN.TITLE;
	public static final String PK_TITLEX = "titlex";
	public static final String PK_VIEW = CN.VIEW;
	public static final String PK_STATE = CN.STATE;
	public static final String PK_FIXED = CN.FIXED;
	public static final String PK_SIZE = CN.SIZE;


	protected final Pare<String, String> sdn;

	protected final boolean isForm;//eq isJson

	//
	//
	// CONTRACT


	public abstract void set(String prop, Object value);

	public abstract String get(String prop, String... defRq);

	public abstract <T> T getAs(String prop, Class<T> asType, T... defRq);

	public abstract Boolean hasPropEnable(String prop, Boolean... defRq);

	public boolean hasPropNotEmpty(String prop) {
		return X.notEmpty(get(prop, null));
	}

}
