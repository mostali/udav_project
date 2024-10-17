package zk_os.sec;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MatrixAccess {
	private final Map<ROLE, Boolean> roles;

	public static final MatrixAccess ADMIN_FULL = MatrixAccess.of(ROLE.OWNER, true, ROLE.ADMIN, true);

	public static final MatrixAccess EDITOR_FULL = MatrixAccess.of(ROLE.OWNER, true, ROLE.ADMIN, true, ROLE.EDITOR, true);
	public static final MatrixAccess USER = MatrixAccess.of(ROLE.USER, true);

//	public static MatrixAccess of(ROLE role) {
//		switch (role){
//			case USER:
//				return USER;
//			case ADMIN:
//				return ADMIN_FULL;
//			case EDITOR:
//				return EDITOR_FULL;
//			case OWNER:
//				return
//		}
//	}

	public static MatrixAccess of(ROLE role, boolean rslt, ROLE role2, boolean rslt2, ROLE role3, boolean rslt3) {
		return new MatrixAccess(new HashMap() {
			{
				put(role, rslt);
				put(role2, rslt2);
				put(role3, rslt3);
			}
		});
	}

	public static MatrixAccess of(ROLE role, boolean rslt, ROLE role2, boolean rslt2) {
		return new MatrixAccess(new HashMap() {
			{
				put(role, rslt);
				put(role2, rslt2);
			}
		});
	}

	public static MatrixAccess of(ROLE role, Boolean rslt) {
		return new MatrixAccess(new HashMap() {
			{
				put(role, rslt);
			}
		});
	}

	public static boolean hasAccessForCurrentUser(MatrixAccess ma, boolean hasAccessIfNull) {
//		if (Sec.getSecMode() == Sec.Mode.OPEN_ALL) {
//			return true;
//		}
		if (ma == null) {
			return hasAccessIfNull;
		}
		Collection<GrantedAuthority> roles = ROLE.getCurrentUserRolesGA(Collections.EMPTY_LIST);
		return MatrixAccess.hasAccessForUser(ma, roles, hasAccessIfNull);
	}

	public boolean hasAccess() {
		return hasAccessForUser(this);
	}

	public static boolean hasAccessForUser(MatrixAccess matrixAccess) {
		return hasAccessForUser(matrixAccess, (Collection) Sec.getAuth().getAuthorities(), false);
	}

	public static boolean hasAccessForUser(MatrixAccess matrixAccess, Collection<GrantedAuthority> roles, boolean hasAccessIfNull) {
		if (matrixAccess == null) {
			return hasAccessIfNull;
		}
		for (Map.Entry<ROLE, Boolean> role : matrixAccess.roles.entrySet()) {
			for (GrantedAuthority ga : roles) {
				if (role.getKey().isEq(ga)) {
					return role.getValue();
				}
			}
		}
		return false;
	}

}
