package zk_os.sec;

import lombok.SneakyThrows;
import mpe.NT;
import mpe.str.CN;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpe.core.P;
import mpc.env.AP;
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
import mpc.net.query.QueryUrl;
import udav_net.apis.zznote.NoteApi;
import zk_os.AppZos;
import zk_os.AppZosConfig;
import zk_os.db.WebUsrService;
import zk_os.db.net.*;
import zk_notes.types.AuthRspContract;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class Sec {

	public static final Logger L = LoggerFactory.getLogger(Sec.class);

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
		} else if (auth instanceof ZAuth) {
			return ((ZAuth) auth).webUsr();
		} else if (auth instanceof RememberMeAuthenticationToken) {
			L.warn("Auth is RememberMeAuthenticationToken");
		}
		return ARG.toDefRq(defRq);
	}

	public static void setAuthByUserUuid(String header) {
		if (header.equals(AppZosConfig.SUPER_KEY_HEADER)) {
			WebUsr webUsr = RootZkosWebUsr.get(null);
			if (webUsr == null) {
				webUsr = RootZkosWebUsr.load();
			}
			ZAuth zAuth = webUsr.createZAuth(true);
			WebUsr usr = zAuth.webUsr();
			if (L.isInfoEnabled()) {
				L.info("Apply {} setAuthByUserUuid", usr.getFirst_name());
			}
//			return zAuth;
		}
//		return null;
//		throw new NotExistException(header);
	}

	public static ZAuth setAuthByUserSid(Long sid, String name) {
		WebUsr usr1 = new WebUsr();
		usr1.setSid(sid);
		usr1.setFirst_name(name);
		return setAuth(usr1.createZAuth());
	}

	public static <T extends Authentication> T setAuth(T auth) {
		SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
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

	public static Authentication getSpringAuth() {
		return getSecContext().getAuthentication();
	}

	public static boolean isAnonimUnsafeTrue(IaUser usr) {
		return isAnonimUnsafe(usr, true);
	}

	public static boolean isAnonimUnsafe(IaUser usr, boolean isNullThat) {
		return usr == null ? isNullThat : usr instanceof IAnonim;
	}

	public static boolean isAnonimSafe(IaUser usr) {
		return usr instanceof IAnonim;
	}

	public static boolean isNotAnonimUnsafe() {
		return !isAnonimUnsafe();
	}

	public static boolean isAnonimUnsafe() {
		return isAnonimUnsafeTrue(Sec.getUser(null));
	}

	public static boolean isAuthMode(Mode lpBear) {
		return lpBear == getSecMode();
	}

	public static void checkAndApplyAuthBySuperKey(QueryUrl queryUrl) {
		if (AppZosConfig.SUPER_KEY == null) {
			return;
		}
		String super_key = queryUrl.getFirstAs(NoteApi.SKA, String.class, null);
		Sec.checkAndApplyAuthBySuperKey(super_key);
	}

	public static boolean hasSkaAny(QueryUrl queryUrl) {
		if (AppZosConfig.SUPER_KEY == null) {
			return false;
		}
		String super_key = queryUrl.getFirstAs(NoteApi.SKA, String.class, null);
		return X.notEmpty(super_key);
	}

	public static boolean hasSkaStrict(QueryUrl queryUrl, boolean... any) {
		if (AppZosConfig.SUPER_KEY == null) {
			return false;
		}
		String super_key = queryUrl.getFirstAs(NoteApi.SKA, String.class, null);
		if (X.empty(super_key)) {
			return false;
		}
		return ARG.isDefEqTrue(any) ? true : AppZosConfig.SUPER_KEY.equals(super_key);
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

	public static boolean isAdminRole() {
		return isAdminRole(Sec.getUser(null));
	}

	public static boolean isAdminRole(WebUsr user) {
		return user != null && user.getRolesListGA().contains(ROLE.GA_ROLE_ADMIN);
	}

	public static boolean isOwnerRole() {
		return isOwnerRole(Sec.getUser(null));
	}

	public static boolean isOwnerRole(WebUsr user) {
		return user != null && user.getRolesListGA().contains(ROLE.GA_ROLE_OWNER);
	}

	public static boolean isAdminOrOwnerRole() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_ADMIN) || user.getRolesListGA().contains(ROLE.GA_ROLE_OWNER);
	}

	@Deprecated
	public static boolean isEditorAdminOwnerRole() {
		WebUsr user = Sec.getUser(null);
		if (user == null) {
			return false;
		} else if (isAdminOrOwnerRole()) {
			return true;
		}
		return user.getRolesListGA().contains(ROLE.GA_ROLE_EDITOR);
	}


	public static boolean isEditorRole() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_EDITOR);
	}

	public static boolean isRunnerRole() {
		WebUsr user = Sec.getUser(null);
		return user == null ? false : user.getRolesListGA().contains(ROLE.GA_ROLE_RUNNER);
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

				WebUsr webUsr;

				WebUsr webUsrExisted = webUsrService.loadUserByNetNid(nt.name(), nid);
				if (webUsrExisted == null) {
					//that new
					webUsr = new NetZkosWebUsr();
					webUsr.setLogin(nt.shortPfx() + nid);
					webUsr.setAlias(webUsr.getLogin());
					webUsr.setNet(nt.name());
					webUsr.setRoles(ROLE.ROLE_EDITOR.toString());
					webUsr.setFirst_name(null);
					webUsr.setNid(nid);
//					webUsr.alias = authRspContract.getAlias();
					webUsrService.save(webUsr);
					if (L.isInfoEnabled()) {
						L.info("Created new user:" + webUsr);
					}
				} else {
					webUsr = webUsrExisted;
				}

				ZAuth authentication = ZAuth.of(webUsr);
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
		if (!Sec.isOwnerRole()) {
			if (AppZos.isDebugEnable()) {
				throw StatusException.C404("access denied !o");
			} else {
				throw StatusException.C404();
			}
		}
	}

	public static void checkIsAdminOrOwnerOr404() {
		if (!isAdminOrOwnerRole()) {
			if (AppZos.isDebugEnable()) {
				throw StatusException.C404("access denied !ao");
			} else {
				throw StatusException.C404();
			}
		}
	}

	public static void checkIsEditorOrAoOr404() {
		if (!isEditorRole() && !isAdminOrOwnerRole()) {
			if (AppZos.isDebugEnable()) {
				throw StatusException.C404("access denied for !editor ");
			} else {
				throw StatusException.C404();
			}
		}
	}

	public static void checkIsNotAnonimOr404() {
		if (isAnonimUnsafe()) {
			if (AppZos.isDebugEnable()) {
				throw StatusException.C404("access denied for anonim");
			} else {
				throw StatusException.C404();
			}
		}
	}

	public static String getUserNameOrAnonim() {
		WebUsr user = getUser();
		return user == null ? SecApp.ANONIM : user.getUserName();
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
