package zk_os.sec;

import lombok.SneakyThrows;
import mpe.NT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpe.core.P;
import mpc.env.AP;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpu.pare.Pare3;
import mpu.str.Sb;
import mpt.IaUser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import utl_rest.StatusException;
import mpc.fs.query.QueryUrl;
import zk_os.AppZosConfig;
import zk_os.db.WebUsrService;
import zk_os.db.net.IAnonim;
import zk_os.db.net.NetZkosWebUsr;
import zk_os.db.net.WebUsr;
import zk_os.db.net.AnonimZkosWebUsr;
import zk_page.core.PageRoute;
import zk_notes.types.AuthRspContract;
import zk_page.core.SpVM;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class Sec {

	public static final Logger L = LoggerFactory.getLogger(Sec.class);
	public static final String SKA = "ska";

	public static Authentication getSkBear() {
		return new SkUsernamePasswordAuthenticationToken(WebUsrService.findWebUsrRoot(), Arrays.asList(ROLE.GA_ROLE_ADMIN, ROLE.GA_ROLE_OWNER));
	}

	public static Authentication doAuthByLoginPass(String name, String password, boolean isPhc) {
		WuUsernamePasswordAuthenticationToken auth = null;
		Mode secMode = Sec.getSecMode();
		switch (secMode) {
			case LP: {
				WebUsr webUsr = WebUsrService.findWebUsr(name, password, isPhc, null);
				if (webUsr != null) {
					auth = new WuUsernamePasswordAuthenticationToken(webUsr);
				} else {
					L.info("User '{}:{}' not found", name, password);
				}
				break;
			}
			case LP_BEAR: {
				if ("bear".equals(name) && "123".equals(password)) {
					auth = new WuUsernamePasswordAuthenticationToken(WebUsrService.findWebUsrRoot(), Arrays.asList(ROLE.GA_ROLE_ADMIN));
				}
				break;
			}
			case OPEN_ALL: {
				WebUsr webUsrRoot = WebUsrService.findWebUsrRoot();
				if (webUsrRoot != null) {
					auth = new WuUsernamePasswordAuthenticationToken(webUsrRoot, webUsrRoot.getRolesListGA());
				}
				break;
			}
			case DISABLE:
				break;
			default:
				throw new WhatIsTypeException(secMode);
		}
		if (L.isInfoEnabled()) {
			L.info("User is auth with mode '{}', status '{}'", secMode, auth != null);
		}
		return auth;
	}

	public static WebUsr getUser(WebUsr... defRq) {
		Authentication auth = getSpringAuth();
		if (auth instanceof AnonymousAuthenticationToken) {
			return new AnonimZkosWebUsr();
		} else if (auth instanceof WuUsernamePasswordAuthenticationToken) {
			return ((WuUsernamePasswordAuthenticationToken) auth).usr;
		} else if (auth instanceof BeaAuth) {
			return ((BeaAuth) auth).webUsr();
		} else if (auth instanceof RememberMeAuthenticationToken) {
			L.warn("Auth is RememberMeAuthenticationToken");
		}
		return ARG.toDefRq(defRq);
	}

	public static Authentication getAuth(Authentication... defRq) {
		Authentication auth = getSpringAuth();
		if (auth instanceof AnonymousAuthenticationToken) {
			return auth;
		} else if (auth instanceof WuUsernamePasswordAuthenticationToken) {
			return auth;
		} else if (auth instanceof RememberMeAuthenticationToken) {
			return auth;
		}
		return ARG.toDefRq(defRq);
	}

//	public static WuUsernamePasswordAuthenticationToken getAuth() {
//		return (WuUsernamePasswordAuthenticationToken) getSecContext().getAuthentication();
//	}

	public static Authentication getSpringAuth() {
		return getSecContext().getAuthentication();
	}

	public static boolean isAnonim(IaUser usr) {
		//return EQ.eq(usr.id, 0) && EQ.eq(usr.sid, 0) && EQ.eq(usr, 0);
		return usr instanceof IAnonim;
	}

	public static boolean isNotAnonim() {
		return !isAnonim();
	}

	public static boolean isAnonim() {
		WebUsr user = Sec.getUser(null);
		return user == null ? true : isAnonim(user);
	}

	public static boolean isAuthMode(Mode lpBear) {
		return lpBear == getSecMode();
	}

	public static void checkAndApplyAuthBySuperKey(QueryUrl queryUrl) {
		if (AppZosConfig.SUPER_KEY == null) {
			return;
		}
		String super_key = queryUrl.getFirstAs(SKA, String.class, null);
		Sec.checkAndApplyAuthBySuperKey(super_key);
	}

	public static boolean hasSka(QueryUrl queryUrl) {
		if (AppZosConfig.SUPER_KEY == null) {
			return false;
		}
		String super_key = queryUrl.getFirstAs(SKA, String.class, null);
		return X.notEmpty(super_key);
	}

	public static void checkAndApplyAuthBySuperKey(String superKey) {
		boolean isSuperKeyEquals = AppZosConfig.SUPER_KEY.equals(superKey);
		if (!isSuperKeyEquals) {
			return;
		}
		Authentication skBear = Sec.getSkBear();
		SecurityContextHolder.getContext().setAuthentication(skBear);
		if (L.isInfoEnabled()) {
			L.info("ENABLE checkAndAuthBySuperKey:" + skBear);
		}
	}

	public static boolean isAdmin() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_ADMIN);
	}

	public static boolean isOwner() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_OWNER);
	}

	public static boolean isPlaneOwner() {
		if (isAnonim()) {
			return false;
		}
		WebUsr user = Sec.getUser();
		return X.equals(SpVM.get().subdomain3(), user.getNetNidNamed(null));
	}

	public static boolean isAdminOrOwner() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_ADMIN) || user.getRolesListGA().contains(ROLE.GA_ROLE_OWNER);
	}

	public static boolean isEditorAdminOwner() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_EDITOR) || user.getRolesListGA().contains(ROLE.GA_ROLE_ADMIN) || user.getRolesListGA().contains(ROLE.GA_ROLE_OWNER) || Sec.isPlaneOwner();
	}

	public static boolean isEditor() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_EDITOR) || Sec.isPlaneOwner();
	}


	@Nullable
	static <T> T returnDefOrThrow(T... defRq) {
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			throw StatusException.CODE(HttpStatus.UNAUTHORIZED, "Authentication* undefined");
		}
		throw StatusException.CODE(HttpStatus.UNAUTHORIZED, "Authentication undefined");
	}

	public static String alias() {
		return Sec.getUser().getAlias();
	}

	public static String aliasOrLogin() {
		return Sec.getUser().getAliasOrLogin();
	}

	public static String login() {
		return Sec.getUser().getLogin();
	}

	public static WebUsr setAuthByToken(Pare3<Boolean, String, String> authRslt) {
		return setAuthByToken(authRslt.ext());
	}

	public static WebUsr setAuthByToken(String authRspJson) {

		AuthRspContract authRspContract = AuthRspContract.of(authRspJson);

		NT nt = authRspContract.getNet();
		switch (nt) {
			case TG:
			case VK: {

				long nid = authRspContract.getNid();

				WebUsrService webUsrService = WebUsrService.get();

				WebUsr webUsr = null;

				WebUsr webUsrExisted = webUsrService.loadUserByNetNid(nt.name(), nid);
				if (webUsrExisted == null) {
					//that new
					webUsr = new NetZkosWebUsr();
					webUsr.setLogin(nt.shortPfx() + nid);
					webUsr.setAlias(webUsr.getLogin());
					webUsr.setNet(nt.name());
					webUsr.setRoles(ROLE.ROLE_EDITOR.toString());
					webUsr.setFirst_name(null);
//					webUsr.alias = authRspContract.getAlias();
					webUsrService.save(webUsr);
					if (L.isInfoEnabled()) {
						L.info("Created new user:" + webUsr);
					}
				} else {
					webUsr = webUsrExisted;
				}

				BeaAuth authentication = BeaAuth.of(webUsr);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				authentication.setAuthenticated(true);

				return webUsr;
			}
			default:
				throw new WhatIsTypeException(nt);
//				Clients.showNotification("ni:" + nt, "warning", null, "bottom_right", 5000);
//				return;
		}

	}

	public static void checkIsOwnerOr404() {
		if (!Sec.isOwner()) {
			throw StatusException.C404();
		}
	}

	public static class SkUsernamePasswordAuthenticationToken extends WuUsernamePasswordAuthenticationToken {
		public SkUsernamePasswordAuthenticationToken(WebUsr webUsr, Collection<? extends GrantedAuthority> authorities) {
			super(webUsr, authorities);
		}
	}

	private static class WuUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

		private final WebUsr usr;

		private WuUsernamePasswordAuthenticationToken(WebUsr webUsr) {
			this(webUsr, webUsr.getRolesListGA());
		}

		@Deprecated
		private WuUsernamePasswordAuthenticationToken(WebUsr webUsr, Collection<? extends GrantedAuthority> authorities) {
			super(webUsr.getLogin(), webUsr.getPhc(), authorities);
			this.usr = webUsr;
		}

	}

	public static Sb info() {

		Sb sb = new Sb();

		SecurityContext context = SecurityContextHolder.getContext();
		SecurityContext secContext = getSecContext();
		Sb info = info(context);
		sb.NL(info);

		if (context != secContext) {
			info = info(secContext);
			sb.NL(info);
		}

		return sb;
	}

	public static SecurityContext getSecContext() {
		return SecurityContextHolder.getContextHolderStrategy().getContext();
	}

	public static Sb info(SecurityContext sc) {
		Authentication auth = sc.getAuthentication();
		Sb sb = new Sb("Sec");
		if (auth == null) {
			return sb.append(":NULL");
		}
		sb.TABNL(1, auth.getName());
		sb.TABNL(1, auth.getPrincipal());
		sb.TABNL(1, auth.getCredentials());
		sb.TABNL(1, auth.getDetails());
		sb.TABNL(1, auth.getAuthorities());
		return sb;
	}

	//https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
	//https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	public static class Phc {
		@SneakyThrows
		public static boolean matches(String pass, String phc) {
//			Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
//			String pbkdf2CryptedPassword = pbkdf2PasswordEncoder.encode(pass);
//			return new Pbkdf2PasswordEncoder().matches(pass, phc);

//			boolean matched = BCrypt.checkpw(pass, phc);

			String passHashCode = getPassHashCode(pass);
			return passHashCode.equals(phc);

		}

		public static final String SALT = "a3567babc5e4f83a765f0bdc5130be24e6fe13bb9ff3ba6138da95e856e02b6dd7f6f36a18a8d8e5";

		@SneakyThrows
		public static String getPassHashCode(String pass) {
			return get_SHA_512_SecurePassword(pass, SALT);
//			MessageDigest md = MessageDigest.getInstance("SHA-512");
//			return String.valueOf(md.digest(pass.getBytes(StandardCharsets.UTF_8)));

//			String generatedSecuredPasswordHash = BCrypt.hashpw(pass, BCrypt.gensalt(12));

//			return new Pbkdf2PasswordEncoder().encode(pass);
		}

		private static String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
			String generatedPassword = null;
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-512");
				md.update(salt.getBytes());
				byte[] bytes = md.digest(passwordToHash.getBytes());
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < bytes.length; i++) {
					sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
				}
				generatedPassword = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return generatedPassword;
		}

		// Add salt
		private static String getSalt() throws NoSuchAlgorithmException {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[16];
			sr.nextBytes(salt);
			return salt.toString();
		}

		public static String[] generateRandomPass() {
			String pass = UUID.randomUUID().toString();
			return new String[]{pass, getPassHashCode(pass)};
		}

		public static void main(String[] args) throws NoSuchAlgorithmException {
			//5b1b55ee-a6f7-402d-a47c-105973af3642:a3567babc5e4f83a765f0bdc5130be24e6fe13bb9ff3ba6138da95e856e02b6dd7f6f36a18a8d8e5
			//P.exit(generateRandomPass());
//			String pass = "aa62fd8b-00e3-4d72-8340-3da68fa40671";
			String pass = "wtf";
			Sys.p(getPassHashCode(pass));
			Sys.p(getPassHashCode(pass));
			Sys.p(getPassHashCode(pass));
			Sys.p(getPassHashCode(pass));
			P.exit(matches(pass, getPassHashCode(pass)));
//			P.exit(getPassHashCode(pass));
//			P.exit(isValidPass("5b1b55ee-a6f7-402d-a47c-105973af3642", "a3567babc5e4f83a765f0bdc5130be24e6fe13bb9ff3ba6138da95e856e02b6dd7f6f36a18a8d8e5"));
		}


	}


	//	public static boolean secOn = true;
	private static Mode mode;

	public static Mode getSecMode() {
//		if (!secOn) {
//			return Mode.DISABLE;
//		}
		return mode != null ? mode : (mode = AP.getAs("web.sec", Sec.Mode.class, Mode.DISABLE));
	}

	public enum Mode {
		LP, OPEN_ALL, LP_BEAR, DISABLE;

		public boolean isEnable() {
			return this == getSecMode();
		}
	}

}
