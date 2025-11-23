package zk_os.db.net;

import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.netapp.mdl.NetUserModel;
import mpc.env.APP;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpe.NT;
import mp.utl_odb.netapp.usr.InaUser;
import mpc.types.tks.FID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zk_os.db.WebUsrService;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.ZAuth;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = NetUserModel.TABLE)
public class WebUsr implements Serializable, InaUser, UserDetails {

	public WebUsr() {
	}

	public static WebUsr get(WebUsr... defRq) {
		return Sec.getUser(defRq);
	}

	public static WebUsr fake() {
		WebUsr webUsr = new WebUsr();
		webUsr.first_name = "u";
		webUsr.sid = Long.MAX_VALUE;
		return webUsr;
	}

	public static WebUsr loadBySid(@NonNull Long sid, WebUsr... defRq) {
		String netName = APP.getNetName();
		WebUsr webUsrSid = WebUsrService.findAppUsrSID(netName, sid);
		return webUsrSid != null ? webUsrSid : ARG.toDefThrowMsg(() -> X.f("Except user by SID[%s] and NET[%s] ", sid, netName), defRq);
	}

	public static String login(String... defRq) {
		WebUsr webUsr = get(null);
		return webUsr != null ? webUsr.getLogin() : ARG.toDefThrow(() -> new RequiredRuntimeException("Except known user"), defRq);
	}

	public FID getFID() {
		return NT.of(getNt()).FID(getUserSid());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Setter
	private @Getter Long id;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebUsr) {
			WebUsr webUsr = (WebUsr) obj;
			Boolean state = equalsByLogin(this, webUsr);
			if (state != null) {
				return state;
			}//wrh - who wo login>
			if (true) {
				return false;
			}
			state = equalsByIdOrNull(this, webUsr);
			if (state != null) {
				return state;
			}
			state = equalsByNidOrNull(this, webUsr);
			if (state != null) {
				return state;
			}

			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getLogin().hashCode();
	}

//	public ROLE getROLE(boolean strictContainsGA, ROLE... defRq) {
//		Optional<ROLE> first = Arrays.stream(ROLE.values()).filter(r -> r.has(this, strictContainsGA)).findFirst();
//		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except role ofr usr [%s]", this), first, defRq);
//	}

	private Boolean equalsByLogin(WebUsr webUsr1, WebUsr webUsr2) {
		return webUsr1.getLogin().equals(webUsr2.getLogin());
	}

	public static Boolean equalsByNidOrNull(WebUsr webUsr1, WebUsr webUsr2) {
		if (!webUsr1.hasNetNid() || !webUsr2.hasNetNid()) {
			return null;
		}
		return webUsr1.getNt().equals(webUsr2.getNt()) && webUsr1.getNid().equals(webUsr2.getNid());
	}

	public static Boolean equalsByIdOrNull(WebUsr webUsr1, WebUsr webUsr2) {
		if (webUsr1.getId() == null || webUsr2.getId() == null) {
			return null;
		}
		return webUsr1.getId().equals(webUsr2.getId());
	}

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter
	private @Getter Long sid;

	@Setter
	private String net;

	@Setter
	private @Getter Long nid;

	@Setter
	private @Getter String login, alias;

	public String getAliasOrLogin() {
		return X.empty(getAlias()) ? getLogin() : getAlias();
	}

	@Setter
	private @Getter String first_name, last_name;

	@Setter
	private @Getter String phc;

	private @Getter String roles;

	public void setMainRole(ROLE role) {
		this.roles = role.toRoleName();
	}

	public String getMainRoleString() {
		return this.roles;
	}

	public ROLE getMainRoleType() {
		return ROLE.valueOf(this.roles);
	}

	@Deprecated
	public List<String> getRolesList() {
		return SPLIT.allByComma(roles);
	}

	@Deprecated
	public List<GrantedAuthority> getRolesListGA() {
		return getRolesList().stream().map(ROLE::toRoleGa).collect(Collectors.toList());
	}


	public Long getUserSid() {
		return sid;
	}

	public String getNt() {
		return net;
	}

	public NT getNetType(NT... defRq) {
		return NT.of(getNt(), defRq);
	}

	@Deprecated //NID vs UID
	public String getUserNID() {
		NI.stop("check nid vs uid");
		return getUserSid().toString();
	}

	public String getUserName(int... length) {
		return first_name;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRolesListGA();
	}

	@Override
	public String getPassword() {
		return phc;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean isAllowedForWorkOnSite() {
		return isEnabled() && isCredentialsNonExpired() && isAccountNonLocked() && isAccountNonExpired();
	}

	@Override
	public String toString() {
		if (APP.IS_DEBUG_ENABLE) {
			return X.f("%s#%s", first_name, id);
		}
		return X.f("%s*%s(#%s)/N:%s", net, sid, id, first_name);
	}

	public boolean isMainRole_ADMIN_OWNER() {
		return isMainRole_ADMIN() || isMainRole_OWNER();
	}

	public Boolean isMainRole_ANONIM() {
		return ROLE.ROLE_ANONIM.equals(getMainRoleString());
	}

	public Boolean isMainRole_ADMIN() {
		return ROLE.ROLE_ADMIN.equals(getMainRoleString());
	}

	public Boolean isMainRole_OWNER() {
		return ROLE.ROLE_OWNER.equals(getMainRoleString());
	}

	public boolean isMainRole_USER() {
		return ROLE.GA_ROLE_USER.equals(getMainRoleString());
	}

	public String getNetLogin(String... defRq) {
		Long nid = getNid();
		if (nid != null) {
			return getNetType().toNetShortPfxLogin(nid);
		}
		return ARG.toDefThrowMsg(() -> X.f("Except NID for user [%s]", toStringSidNamed()), defRq);
	}

	public Long getNidByNet(NT nt, Long... defRq) {
		return nt == getNetType(null) ? getNid() : ARG.toDefThrowMsg(() -> X.f("Not found net '%s' for user '%s'", nt, getId()), defRq);
	}

	public ZAuth createZAuth() {
		return new ZAuth(this, getRolesListGA());
	}

	public String toInfo() {
		WebUsr user = this;
		String userName0 = user.getUserName();
		String userName = X.empty(userName0) ? "" : userName0;
		String login0 = user.getLogin();
		String login = X.empty(login0) ? "" : ":" + login0;
		String lbl = userName + login;
		if (APP.IS_DEBUG_ENABLE) {
			lbl += "[" + user.getRoles() + "]";
		}
		return lbl;
	}

	public boolean isEqualsByIds(String... ids) {
		for (String id : ids) {
			if (id.equals(getLogin())) {
				return true;
			} else if (id.equals(getAlias())) {
				return true;
			} else if (getNid() != null && id.equals(getNetLogin())) {
				return true;
			}
		}
		return false;
	}

	public Set<String> getAllLoginValues() {
		Set l = new HashSet();
		if (X.notEmpty(getLogin())) {
			l.add(getLogin());
		}
		if (X.notEmpty(getAlias())) {
			l.add(getAlias());
		}
		if (getNid() != null) {
			l.add(getNetLogin());
		}
		return l;
	}

	public boolean isEqualsUserByLoginOrAlias(String loginOrAlias) {
		String login = getLogin();
		if (loginOrAlias.equals(login)) {
			return true;
		}
		String alias = getAlias();
		return alias != null && loginOrAlias.equals(alias);
	}

	public boolean isEditorFor(Pare<String, String> sdn) {
		return isEditorFor(sdn.key());
	}

	public boolean isEditorFor(String plane) {
		if (plane.equals(getLogin())) {
			return true;
		}
		String alias = getAlias();
		return alias != null && plane.equals(alias);
	}

}
