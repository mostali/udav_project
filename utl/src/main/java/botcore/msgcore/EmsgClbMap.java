package botcore.msgcore;

import java.util.HashMap;
import java.util.Map;

public class EmsgClbMap extends ClbMap {

	public static final String EMSG_ID = "emsg_id";

	public EmsgClbMap(int emsgId) {
		super(new HashMap<>());
		emsgId(emsgId);
	}

	public EmsgClbMap(Map ctx) {
		super(ctx);
	}

	public Integer emsgId(Integer val) {
		put(EMSG_ID, val);
		return val;
	}

}
