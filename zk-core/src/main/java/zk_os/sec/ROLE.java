package zk_os.sec;

import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpc.exception.WhatIsTypeException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import zk_os.db.net.AnonimZkosWebUsr;
import zk_os.db.net.WebUsr;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum ROLE {
	OWNER, ADMIN, EDITOR, USER, ANONIM;

	public static ROLE now() {
		if (Sec.isOwner()) {
			return OWNER;
		} else if (Sec.isAdmin()) {
			return ADMIN;
		} else if (Sec.isEditor()) {
			return EDITOR;
		} else if (Sec.isNotAnonim()) {
			return USER;
		} else if (Sec.isAnonim()) {
			return ANONIM;
		}
		Authentication auth = Sec.getAuth();
		throw new WhatIsTypeException(auth + "");
	}

	public static String toIcon() {
		ROLE role = now();
		switch (role) {
			case OWNER:
				return SYMJ.REPTIL;
			case ADMIN:
				return SYMJ.USER;
			case EDITOR:
				return SYMJ.USER_TWO;
			case USER:
				return SYMJ.SEARCH_MAN;
			case ANONIM:
				return SYMJ.CASPER;
			default:
				throw new WhatIsTypeException(role);
		}
	}

	public static final String ROLE_OWNER = ROLE.OWNER.name();
	public static final String ROLE_ADMIN = ROLE.ADMIN.name();
	public static final String ROLE_EDITOR = ROLE.EDITOR.name();
	//	public static final String ROLE_ENTITY = ROLE.ENTITY.name();
	public static final String ROLE_USER = ROLE.USER.name();
	//
	//
	public static final GrantedAuthority GA_ROLE_OWNER = () -> "ROLE_OWNER";
	public static final GrantedAuthority GA_ROLE_ADMIN = () -> "ROLE_ADMIN";
	public static final GrantedAuthority GA_ROLE_EDITOR = () -> "ROLE_EDITOR";
	//	public static final GrantedAuthority GA_ROLE_ENTITY = () -> "ROLE_ENTITY";
	public static final GrantedAuthority GA_ROLE_USER = () -> "ROLE_USER";

	public static boolean[] getRolesFlags() {
		ROLE[] roles = ROLE.values();
		boolean[] flags = new boolean[roles.length];
		Collection<GrantedAuthority> gaRoles = getCurrentUserRolesGA(Collections.EMPTY_LIST);
		for (int i = 0; i < flags.length; i++) {
			flags[i] = gaRoles.contains(roles[i].toGaRole());
		}
		return flags;
	}

	public static GrantedAuthority toGaRole(String role) {
		return toGaRole(ROLE.valueOf(role));
	}

	public static boolean isAllowed(ROLE role) {
		Authentication auth = Sec.getAuth(null);
		return auth == null ? false : auth.getAuthorities().contains(role.toGaRole());
	}

	public static boolean hasAdminMin() {
		return hasRoleAny(GA_ROLE_ADMIN, GA_ROLE_OWNER);
	}

	public static boolean hasEditorMin() {
		return hasRoleAny(GA_ROLE_EDITOR, GA_ROLE_ADMIN, GA_ROLE_OWNER);
	}


	public static boolean hasRoleAny(GrantedAuthority... values) {
		WebUsr user = Sec.getUser();
		if (user instanceof AnonimZkosWebUsr) {
			return false;
		}
		List<GrantedAuthority> rolesListGA = user.getRolesListGA();
		for (GrantedAuthority userRoleGA : rolesListGA) {
			for (GrantedAuthority checkedRoleGA : values) {
				if (checkedRoleGA == userRoleGA) {
					return true;
				}
			}
		}
		return false;
	}

	public GrantedAuthority toGaRole() {
		return toGaRole(this);
	}

	public static GrantedAuthority toGaRole(ROLE role) {
		switch (role) {
			case OWNER:
				return GA_ROLE_OWNER;
			case ADMIN:
				return GA_ROLE_ADMIN;
			case EDITOR:
				return GA_ROLE_EDITOR;
			case USER:
				return GA_ROLE_USER;
			case ANONIM:
				return null;
			default:
				throw new WhatIsTypeException(role);
		}
	}


//	public static GrantedAuthority getMaxUserGA() {
//		Collection<? extends GrantedAuthority> all = getCurrentUserRolesGA(null);
//		if (all == null) {
//			return null;
//		}
//		switch (all.size()) {
//			case 0:
//				return null;
//			case 1:
//				return ARRi.first(all);
//			default:
//				for (GrantedAuthority ga : all) {
//					for (ROLE role : ROLE.values()) {
//						if (all.contains(role.toGaRole())) {
//							return ga;
//						}
//					}
//				}
//				throw new WrongLogicRuntimeException("GaRole not found");
//		}
//	}

	public static Collection<? extends GrantedAuthority> getCurrentUserRolesGA(Collection<GrantedAuthority>... authorities) {
		Authentication auth = Sec.getAuth(null);
		if (auth != null) {
			return auth.getAuthorities();
		}
		return ARG.toDefRq(authorities);
	}

	@Deprecated
	public boolean has(boolean[] roles) {
		return roles[ENUM.indexOf(this)];
	}

	@Deprecated
	public boolean has() {
//		Sec.Mode secMode = Sec.getSecMode();
//		switch (secMode) {
//			case LP:
		GrantedAuthority targetGA = toGaRole();
		Collection userGA = getCurrentUserRolesGA(Collections.EMPTY_LIST);
		return userGA.contains(targetGA);
//			case OPEN_ALL:
//				return true;
//			case DISABLE:
//				return false;
//			case LP_BEAR:
//				return true;
//			default:
//				throw new WhatIsTypeException(secMode);
//		}
	}


	public boolean isEq(GrantedAuthority ga) {
		return ga == null ? false : toGaRole().getAuthority().equals(ga.getAuthority());
	}

	public boolean hasGaRole(Authentication auth) {
		return auth.getAuthorities().contains(toGaRole());
	}
}
