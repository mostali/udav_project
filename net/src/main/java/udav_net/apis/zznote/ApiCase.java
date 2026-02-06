package udav_net.apis.zznote;

import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;

import java.util.Arrays;
import java.util.Optional;

//API Common Read/Write notes
//ATI TreeApi
//ADI DownloadFileApi
//ACI Docker Container Api
public enum ApiCase {
	_api, _ati, _adi, _aci;

	public final String NAME;
	public final String _NAME;
	public final String _NAME_;

	ApiCase() {
		this.NAME = name();
		this._NAME = "/" + NAME;
		this._NAME_ = _NAME + "/";
	}

	public static ApiCase valueOf(String pagename, ApiCase... defRq) {
		if (pagename.startsWith("_a")) {
			ApiCase apiCase = ENUM.valueOf(pagename, ApiCase.class, null);
			if (apiCase != null) {
				return apiCase;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Except API case of " + pagename), defRq);
	}

	public static ApiCase ofServletPath(String servletPath, ApiCase... defRq) {
		if (servletPath.startsWith("/_a")) {
			Optional<ApiCase> optionalApiCase = Arrays.stream(ApiCase.values()).filter(c -> servletPath.startsWith(c._NAME_)).findFirst();
			if (optionalApiCase.isPresent()) {
				return optionalApiCase.get();
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Not found any api part from servletPath: %s", servletPath), defRq);
	}

	public CharSequence apiName() {
		return name();
	}
}
