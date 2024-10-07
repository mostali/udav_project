package zk_form.head;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IHeadCom {

	IHeadRsrc[] getHeadRsrc();

	static boolean isAppendRsrc(Map<String, Object> attrs, IHeadRsrc rsrc) {
		Boolean isAlreadyAppend = (Boolean) attrs.get(getKey(rsrc));
		return isAlreadyAppend == null ? false : isAlreadyAppend;
	}

	static final String PFX_HEAD_RSRC = "IHCR:";

	@NotNull
	 static String getKey(IHeadRsrc rsrc) {
		String keyname;
//		if (rsrc instanceof Enum) {
//			keyname = rsrc.toString();
//		} else
		if (rsrc.getStdHeadLib() != null) {
			keyname = rsrc.getStdHeadLib().name();
		} else {
			keyname = rsrc.getClass().getSimpleName();
		}
		return PFX_HEAD_RSRC + keyname;
	}


	static boolean updateStateAppendRsrc(Map<String, Object> attrs, IHeadRsrc rsrc, boolean newState) {
		String key = getKey(rsrc);
		attrs.put(key, newState);
//		if (L.isDebugEnabled()) {
//			L.debug("updateStateAppendRsrc with key '{}', state '{}'", key, newState);
//		}
		return newState;
	}
}
