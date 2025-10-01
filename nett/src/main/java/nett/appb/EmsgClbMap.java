package nett.appb;

import botcore.clb.ClbMap;

import java.util.HashMap;
import java.util.Map;

public class EmsgClbMap extends ClbMap {
	public EmsgClbMap(int emsgId) {
		super(new HashMap<>());
		emsgId(emsgId);
	}

	public EmsgClbMap(Map ctx) {
		super(ctx);
	}

	public Integer emsgId(Integer... val) {
		return INT("emsg_id", val);
	}

}
