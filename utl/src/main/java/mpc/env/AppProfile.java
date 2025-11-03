package mpc.env;

import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpc.log.L;
import mpu.str.SPLIT;

import java.util.LinkedList;
import java.util.List;

public enum AppProfile {
	nil, undefined, DEFAULT, local, dev_local, dev, prod, prod_local;

	public static final String PROFILES_ACTIVE = "spring.profiles.active";

	public static AppProfile[] loadAll() {
		String property = System.getProperty(PROFILES_ACTIVE);
		AppProfile[] appProfiles = new AppProfile[0];
		if (property == null) {
			return appProfiles;
		}
		return valuesOf(SPLIT.argsByComma(property), true, appProfiles);
	}

	public boolean isEnable() {
		return isEnable(this);
	}

	public String toFilenameWithProfile() {
		return AP.toFilenameWithProfile(this);
	}

	public static boolean isEnable(AppProfile... profile) {
		for (AppProfile appProfile : profile) {
			if (getFirstUseful() == appProfile) {
				return true;
			}
		}
		return false;
	}

	public String str() {
		switch (this) {
			case prod_local:
			case dev_local:
				return name().replace("_", "-");
		}
		return name();
	}

	public static AppProfile of(String name, AppProfile... defRq) {
		return ENUM.valueOf(name, AppProfile.class, defRq);
	}

	private static AppProfile firstUseful = null;

	public static AppProfile getFirstUsefulOr(AppProfile def) {
		AppProfile firstUseful = getFirstUseful();
		return firstUseful == null ? def : firstUseful;

	}

	public static void setFirstUseful(AppProfile appProfile) {
		AppProfile.firstUseful = appProfile;
	}

	public static AppProfile getFirstUseful() {
		return firstUseful;
	}

	public static AppProfile getFirstUseful(String[] activeProfiles, AppProfile... defRq) {
		if (firstUseful != null) {
			return firstUseful;
		}
		AppProfile[] appProfiles = valuesOf(activeProfiles, true, defRq);
		return firstUseful = ARG.isDefNNF(appProfiles) ? appProfiles[0] : null;
	}

	public static AppProfile[] valuesOf(String[] activeProfiles, boolean onlyUseful, AppProfile[]... defRq) {
		if (X.notEmpty(activeProfiles)) {
			List<AppProfile> appProfiles = new LinkedList<>();
			for (int i = 0; i < activeProfiles.length; i++) {
				String activeProfile = activeProfiles[i];
				if (activeProfile.indexOf('-') >= 0) {
					activeProfile = activeProfile.replace("-", "_");
				}
				AppProfile prof = ENUM.valueOf(activeProfile, AppProfile.class, true, undefined);
				if (prof == undefined) {
					if (L.isWarnEnabled()) {
						L.warn("AppProfile '{}' NOT defined", activeProfile);
					}
				}
				if (onlyUseful) {
					switch (prof) {
						case nil:
						case undefined:
							continue;
						default:
							appProfiles.add(prof);
					}
				} else {
					appProfiles.add(prof);
				}
			}
			if (!appProfiles.isEmpty()) {
				return appProfiles.toArray(new AppProfile[activeProfiles.length]);
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("AppProfiles%s is empty", onlyUseful ? "(onlyUseful)" : ""), defRq);
	}

	public static boolean isLocal() {
		return getFirstUseful() == AppProfile.local;
	}

	public boolean isActive() {
		return getFirstUseful() == this;
	}


}
