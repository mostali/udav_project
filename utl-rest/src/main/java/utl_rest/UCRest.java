package utl_rest;

import mpu.X;
import mpu.core.ARG;
import mpu.IT;
import mpc.net.CON;
import org.springframework.http.HttpStatus;
import utl_web.UWeb;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UCRest {
	public static boolean checkHeaderAcceptJson(HttpServletRequest request, boolean... RETURN) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			return true;
		} else if (ARG.isDefNotEqTrue(RETURN)) {
			return false;
		}
		throw ResponseException.CODE(HttpStatus.BAD_REQUEST, "Endpoint not accept 'application/json'. This Request accept '%s'", accept);
	}

	public static String checkGetTokenAuthBearer(HttpServletRequest request, String datasrc_name) {
		String auth = request.getHeader(CON.AUTHORIZATION);
		if (X.empty(auth)) {
			throw ResponseException.CODE(HttpStatus.UNAUTHORIZED, "Set header '%s' with Bearer Token (%s)", CON.AUTHORIZATION, datasrc_name);
		} else if (!auth.startsWith("Bearer ")) {
			throw ResponseException.CODE(HttpStatus.UNAUTHORIZED, "Set CORRECT header '%s' with Bearer Token (%s)", CON.AUTHORIZATION, datasrc_name);
		}
		auth = auth.substring(7);
		if (X.empty(auth)) {
			throw ResponseException.CODE(HttpStatus.UNAUTHORIZED, "Set NOT EMPTY header '%s' with Bearer Token", CON.AUTHORIZATION);
		}
		return auth;
	}

	public static String checkGetBodyPayload(HttpServletRequest request) {
		String body;
		try {
			body = UWeb.getBodyFromRequest(request);
			if (X.empty(body)) {
				throw new IT.CheckException("Set payload");
			}
		} catch (IOException e) {
			throw new IT.CheckException("Error get payload (%s)", e.getMessage());
		}
		return body;
	}

	public static String checkGetQuery(HttpServletRequest request) {
		String query = request.getQueryString();
		if (X.empty(query)) {
			throw new IT.CheckException("Set query part in url");
		}
		return query;
	}
}
