package mpc.html;

import mpu.str.STR;

public enum EHtml5Head {
	html, base, head, link, meta, script, style, title, body;

	public String wrap(Object data) {
		return STR.wrapTag(data, name());
	}

	public String wrap(String data, Object... tagAttributes) {
		return STR.wrapTag(data, name(), tagAttributes);
	}

}
