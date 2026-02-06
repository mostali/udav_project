package udav_jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mpu.IT;
import mpc.env.AppTime;
import mpu.str.ToString;
import mpc.time.EPOCH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

//https://github.com/jwtk/jjwt
//https://jwt.io/
public class UJwt {

//	public static void main(String[] args) {
//		String codedJwt = codeJwt(UMap.of("1=2"));
//		P.p(codedJwt);
//		P.p(decodeData(codedJwt));
//	}

	public static final Logger L = LoggerFactory.getLogger(UJwt.class);

	public static final String DEF_SECRET_KEY = "DSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSKDSK";

	public static Claims decodeData(String jwt) {
		return Jwts.parserBuilder().setSigningKey(getDefaultSecurityKey()).build().parseClaimsJws(jwt).getBody();
	}

	public static String codeJwt(Map<String, Object> data) {
		String jws = Jwts.builder().addClaims(data).signWith(getDefaultSecurityKey()).compact();
		if (L.isInfoEnabled()) {
			L.info("Convert JwtUser to jws [{}]", ToString.toStringSE(jws, 5));
		}
		return jws;
	}

	public static SecretKey getDefaultSecurityKey() {
		//SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		SecretKey key = Keys.hmacShaKeyFor(DEF_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		return key;
	}

	public static Boolean isExp(String jwt, int deltaSec) {
		return !isActual(jwt, deltaSec);
	}

	public static Boolean isActual(String jwt, int deltaSec) {
		try {
			Integer exp = UJwt.getExpEpoch(jwt);
			boolean isActual = exp - deltaSec > EPOCH.epoch();
			return isActual;
		} catch (ExpiredJwtException ex) {
			return false;
		}
	}

	public static Boolean isExp(String jwt) {
		try {
			decodeData(jwt);
			return true;
		} catch (ExpiredJwtException ex) {
			return false;
		}
	}

	public static int getExpEpoch(String jwt) {
		try {
			Integer exp = decodeData(jwt).get("exp", Integer.class);
			return IT.isPosNotZero(exp);
		} catch (ExpiredJwtException ex) {
			return ex.getClaims().get("exp", Integer.class);
		}
	}

	public static LocalDateTime getExpAppTime(String token) throws ExpiredJwtException {
		return AppTime.ldt(UJwt.getExpEpoch(token));
	}

	//	@RequiredArgsConstructor
	//	public static class JwtUser {
	//
	//		public static final String UUID = "uuid";
	//		public static final String UUIDD = "uuidd";
	//
	//		@Getter
	//		private final Map<String, Object> data;
	//
	//		public JwtUser(String uuid) {
	//			this.data = new HashMap<>();
	//			this.data.put(UUID, uuid);
	//			this.data.put(UUIDD, new QDate().mono13W_y4m2());
	//		}
	//
	//		public String getUuid() {
	//			return UC.notNull((String) data.get(UUID));
	//		}
	//
	//		@Override
	//		public String toString() {
	//			return "JwtUser{" +
	//					"data=" + data +
	//					", sizeKb=" + toCompactJws().getBytes(Charset.defaultCharset()).length +
	//
	//					'}';
	//		}
	//
	//		public String toCompactJws() {
	//			return UJwt.codeJwt(data);
	//		}
	//
	//		public static JwtUser ofCompactJws(String jws) {
	//			if (L.isInfoEnabled()) {
	//				L.info("Create JwtUser from jws [{}]", jws);
	//			}
	//			Map<String, Object> map = decodeData(jws);
	//			if (L.isInfoEnabled()) {
	//				L.info("Create JwtUser from jws successful, as map [{}]", map);
	//			}
	//			return new JwtUser(map);
	//		}
	//
	//		public static JwtUser ofCookie(String name) {
	//			Cookie usrCookie = UWeb.getCookie(UWeb.getRequest(), name);
	//			if (usrCookie == null) {
	//				return null;
	//			}
	//			return ofCompactJws(usrCookie.getValue());
	//		}
	//
	//		public static JwtUser getOrCreateOfCookie(String name) {
	//			JwtUser jwtUser = ofCookie(name);
	//			if (jwtUser != null) {
	//				return jwtUser;
	//			}
	//			return createNew();
	//		}
	//
	//		public static JwtUser createNew() {
	//			return new JwtUser(java.util.UUID.randomUUID().toString());
	//		}
	//
	//		public void sendCookie(String name) {
	//			String jws = toCompactJws();
	//			int jwsSize = jws.getBytes(Charset.defaultCharset()).length;
	//			UC.isNumber(jwsSize, 4096, UC.EQ.LT);
	//			UWeb.setCookie(UWeb.getResponse(), name, jws);
	//		}
	//	}
}
