package zk_os.sec;

import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpc.exception.WhatIsTypeException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import zk_os.db.net.AnonimAppWebUsr;
import zk_os.db.net.WebUsr;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum ROLE {
	OWNER, ADMIN, EDITOR, RUNNER, ANONIM;

	public static boolean[] getRolesFlags() {
		ROLE[] roles = ROLE.values();
		boolean[] flags = new boolean[roles.length];
		Collection<GrantedAuthority> gaRoles = getCurrentUserRolesGA(Collections.EMPTY_LIST);
		for (int i = 0; i < flags.length; i++) {
			flags[i] = gaRoles.contains(roles[i].toRoleGa());
		}
		return flags;
	}

	public static String toIcon() {
		ROLE role = WebUsr.get().getMainRoleType();
		switch (role) {
			case OWNER:
				return SYMJ.REPTIL;
			case ADMIN:
				return SYMJ.USER;
			case EDITOR:
				return SYMJ.USER_TWO;
			case RUNNER:
				return SYMJ.ROLE;
//			case USER:
//				return SYMJ.SEARCH_MAN;
			case ANONIM:
				return SYMJ.CASPER;
			default:
				throw new WhatIsTypeException(role);
		}
	}

	public static final String ROLE_ANONIM = ROLE.ANONIM.name();
	public static final String ROLE_OWNER = ROLE.OWNER.name();
	public static final String ROLE_ADMIN = ROLE.ADMIN.name();
	public static final String ROLE_EDITOR = ROLE.EDITOR.name();
	public static final String ROLE_RUNNER = ROLE.RUNNER.name();

	//
	//
	public static final GrantedAuthority GA_ROLE_OWNER = () -> "ROLE_OWNER";
	public static final GrantedAuthority GA_ROLE_ADMIN = () -> "ROLE_ADMIN";
	public static final GrantedAuthority GA_ROLE_EDITOR = () -> "ROLE_EDITOR";
	public static final GrantedAuthority GA_ROLE_USER = () -> "ROLE_USER";
	public static final GrantedAuthority GA_ROLE_RUNNER = () -> "ROLE_RUNNER";


	public static GrantedAuthority toRoleGa(String role) {
		ROLE roleType = ENUM.valueOf(role, ROLE.class, true);
		return toRoleGa(roleType);
	}

	public boolean isAllowed(WebUsr usr) {
		return toRoleName().equals(usr.getMainRoleString());
	}

	public static boolean isAllowed(ROLE role) {
		Authentication auth = Sec.getAuth(null);
		return auth == null ? false : auth.getAuthorities().contains(role.toRoleGa());
	}

	public GrantedAuthority toRoleGa() {
		return toRoleGa(this);
	}

	public static GrantedAuthority toRoleGa(ROLE role) {
		switch (role) {
			case OWNER:
				return GA_ROLE_OWNER;
			case ADMIN:
				return GA_ROLE_ADMIN;
			case EDITOR:
				return GA_ROLE_EDITOR;
			case RUNNER:
				return GA_ROLE_RUNNER;
//			case USER:
//				return GA_ROLE_USER;
			case ANONIM:
				return null;
			default:
				throw new WhatIsTypeException(role);
		}
	}

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

	public boolean hasGaRole(Authentication auth) {
		return auth.getAuthorities().contains(toRoleGa());
	}

	public void checkAccess_or404() {
		switch (this) {
			case OWNER:
				SecCheck.checkIsOwnerOr404();
				break;
			case ADMIN:
				SecCheck.checkIsAdminOrOwnerOr404();
				break;
			case EDITOR:
				SecCheck.checkIsEditor_Admin_Owner_Or404();
				break;
//			case USER:
//				Sec.checkIsNotAnonimOr404();
//				break;

			default:


		}
	}

	public String toRoleName() {
		return name();
	}
}
