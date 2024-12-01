package zk_os.db.net;

import lombok.Getter;
import mpc.exception.RequiredRuntimeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.SPLIT;
import mpe.NT;
import mp.utl_odb.netapp.usr.InaUser;
import mpc.types.tks.FID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "usr")
public class WebUsr implements Serializable, InaUser, UserDetails {//

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

	public FID getFID() {
		return NT.of(getNt()).FID(getUserUid());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long id;

	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long sid;

	public String net;
	public Long nid;

	@Getter
	public String login, alias;

	public String getAliasOrLogin() {
		return X.empty(getAlias()) ? getLogin() : getAlias();
	}

	public String first_name, last_name;
	public String phc, roles;

	public List<String> getRolesList() {
		return SPLIT.allBySpaceStrict(roles);
	}

	public List<GrantedAuthority> getRolesListGA() {
		return getRolesList().stream().map(ROLE::toGaRole).collect(Collectors.toList());
	}

	public Long getUserUid() {
		return sid;
	}

	public String getNt() {
		return net;
	}

	@Deprecated //NID vs UID
	public String getUserNID() {
		return getUserUid().toString();
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

//	public WebUsr setFirstName(String name) {
//		this.first_name = name;
//		return this;
//	}

	@Override
	public String toString() {
		return X.f("%s*%s(#%s)/N:%s", net, sid, id, first_name);
	}

	public Boolean isAdmin() {
		return getRolesListGA().contains(ROLE.GA_ROLE_ADMIN) || getRolesListGA().contains(ROLE.GA_ROLE_OWNER);
	}

	public boolean equalsByLoginOrAlias(String usr) {
		return X.equals(getAlias(), usr) || X.equals(getLogin(), usr);
	}

	public String getNetNidNamed(String... defRq) {
		if (nid != null) {
			return NT.ofUid(net).shortPfx() + IT.NN(nid);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not empty Net NID"), defRq);
	}
}
