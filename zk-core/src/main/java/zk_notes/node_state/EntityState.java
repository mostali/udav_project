package zk_notes.node_state;

import lombok.RequiredArgsConstructor;
import mpe.call_msg.core.ISpaceID;
import mpe.str.CN;
import mpu.X;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@RequiredArgsConstructor
public abstract class EntityState implements Serializable, ISpaceID {

	public static final Logger L = LoggerFactory.getLogger(EntityState.class);

	public static final String PK_TITLE = CN.TITLE;
	public static final String PK_TITLEX = "titlex";
	public static final String PK_VIEW = CN.VIEW;
	public static final String PK_STATE = CN.STATE;
	public static final String PK_FIXED = CN.FIXED;
	public static final String PK_POS = "pos";
	//	public static final String PK_ABSOLUTE = "absolute";
//	public static final String PK_RELATIVE = CN.FIXED;
	public static final String PK_SIZE = CN.SIZE;
	public static final String DEPRECATED = CN.DEPRECATED;
	public static final String LINK_VISIBLE = "link.visible";


	public static final String HREF_TARGET = "href.target";

	public static final String BODY_TOGGLE = "body.toggle";
	public static final String BODY_VISIBLE = "body.visible";
	public static final String BODY_OPENIFHIDE = "body.openIfHide";

	public static final String FK_FROM_FILE =  "from_file";


	protected final Pare<String, String> sdn;

//	@Override
//	public Pare<String, String> sdn() {
//		return sdn;
//	}

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
