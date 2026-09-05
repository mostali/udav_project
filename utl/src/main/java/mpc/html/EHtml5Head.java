package mpc.html;

import mpu.str.WRAP;

public enum EHtml5Head {
	html, base, head, link, meta, script, style, title, body;

	public String wrap(Object data) {
		return WRAP.byTag(data, name());
	}

	public String wrap(String data, Object... tagAttributes) {
		return WRAP.byTag(data, name(), tagAttributes);
	}

}
