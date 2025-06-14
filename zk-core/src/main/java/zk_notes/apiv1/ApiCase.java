package zk_notes.apiv1;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import udav_net.apis.zznote.NoteApi;

public enum ApiCase {
	_api, _ati, _adi, _aci;

	public static ApiCase valueOf(String pagename, ApiCase... defRq) {
		if (pagename.startsWith("_a")) {
			switch (pagename) {
				case NoteApi._API:
					return _api;
				case NoteApi._ATI:
					return _ati;
				case NoteApi._ADI:
					return _adi;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Except API case of " + pagename), defRq);
	}

	public static ApiCase ofServletPath(String servletPath) {
		if (servletPath.startsWith("_a")) {
			return null;
		} else if (servletPath.startsWith(NoteApi._API_PARTURL_)) {
			return _api;
		} else if (servletPath.startsWith(NoteApi._ATI_PARTURL_)) {
			return _ati;
		} else if (servletPath.startsWith(NoteApi._ACI_PARTURL_)) {
			return _aci;
		}
		return null;
	}

	public CharSequence apiName() {
		switch (this) {
			case _api:
				return NoteApi._API;
			case _ati:
				return NoteApi._ATI;
			default:
				throw new WhatIsTypeException(this);
		}
	}
}
