package zk_form.head;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IHeadCom {

	String PFX_HEAD_RSRC = "IHCR:";

	IHeadRsrc[] getHeadRsrc();

	static void updateStateAppendRsrc(Map<String, Object> attrs, IHeadRsrc rsrc) {
		updateStateAppendRsrc(attrs, getKey(rsrc));
	}

	static void updateStateAppendRsrc(Map<String, Object> attrs, String keyRsrc) {
		attrs.put(keyRsrc, true);
	}

	static boolean isAppendRsrc(Map<String, Object> attrs, IHeadRsrc rsrc) {
		return isAppendRsrc(attrs, getKey(rsrc));
	}

	static boolean isAppendRsrc(Map<String, Object> attrs, String keyRsrc) {
		Boolean isAlreadyAppend = (Boolean) attrs.get(keyRsrc);
		return isAlreadyAppend == null ? false : isAlreadyAppend;
	}

	@NotNull
	static String getKey(IHeadRsrc rsrc) {
		String keyname;
		if (rsrc.getStdHeadLib() != null) {
			keyname = rsrc.getStdHeadLib().name();
		} else {
			keyname = rsrc.getClass().getSimpleName();
		}
		return PFX_HEAD_RSRC + keyname;
	}

}
