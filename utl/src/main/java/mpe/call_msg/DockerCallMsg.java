package mpe.call_msg;

import mpc.exception.RequiredRuntimeException;
import mpe.db.IDbUrl;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.TKN;

import java.nio.file.Path;
import java.util.List;

public class DockerCallMsg extends CallMsg {

	public static void main(String[] args) {
		DockerCallMsg sqlCallMsg = DockerCallMsg.ofQk("");
		X.exit(sqlCallMsg.getJdbcUrl());
	}
	public static final String PFX_DOCKERFILE = "#Dockerfile";

	public DockerCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsgHeadersAndBody())) {
//			iJdbcUrl = null;
//			sqlPlatform = null;
			addError("Empty docker file");
			return;
		}

		if (!STR.startsWith(line0, true, PFX_DOCKERFILE)) {
			addError("Except first line with starts %s", PFX_DOCKERFILE);
		}

//		IDbUrl iJdbcUrl0;
//		try {
//			iJdbcUrl0 = getJdbcUrl();
//		} catch (Exception ex) {
//			iJdbcUrl0 = null;
//			addError(ex);
//		}
//
////		iJdbcUrl = iJdbcUrl0;
//
//		try {
//			getSql();
//		} catch (Exception ex) {
//			addError(ex);
//		}
//
//		SQLPlatform sqlPlatform0;
//		try {
//			sqlPlatform0 = IDbUrl.getSqlPlatformFromJdbcUrl(line0);
//		} catch (Exception ex) {
//			sqlPlatform0 = null;
//			addError(ex);
//		}
//		this.sqlPlatform = sqlPlatform0;

	}

	@Override
	public String toString() {
		return "SqlCallMsg{" + "msg='" + fileData + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", errs=" + X.sizeOf0(getErrors()) + '}';
	}


	public IDbUrl getJdbcUrl(IDbUrl... defRq) {
		try {

			String[] login = TKN.two(trimCommentPfx(linesMsgHeadersAndBody().get(0)), ":");
			IT.isEq(login[0], "login", "set line with login , e.g. --login:login");

			String[] pass = TKN.two(trimCommentPfx(linesMsgHeadersAndBody().get(1)), ":");
			IT.isEq(pass[0], "password", "set line with password, e.g. --password:password");

			return IDbUrl.ofULP(ARR.as(line0, login[1], pass[1]));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal JdbcUrl from sql '%s'", linesMsgHeadersAndBody()), defRq);
		}
	}

	public String getSql(String... defRq) {
		try {
			return IT.notEmpty(getBody_STRING(), "set sql body");
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal sql body", ex), defRq);
		}
	}

	public List<String> getSqls(List<String>... defRq) {
		String sql = getSql(null);
		return sql != null ? SPLIT.allBy(sql, "\n--\n") : ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal sql body"), defRq);
	}

	public static DockerCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static DockerCallMsg of(String msg) {
		return (DockerCallMsg) ofQk(msg).throwIsErr();
	}

	public static DockerCallMsg ofQk(String msg) {
		return new DockerCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return DockerCallMsg.ofQk(data).isValid();
	}

	public static boolean isValidKey(String data) {
		return STR.startsWith(data, true, PFX_DOCKERFILE);
	}

}
