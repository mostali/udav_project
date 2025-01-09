package mpe.wthttp;

import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpe.db.IJdbcUrl;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.USToken;
import mpv.sql_morpheus.SQLPlatform;

import java.nio.file.Path;

public class SqlCallMsg extends CallMsg {

	public static final String PFX_JDBC = "jdbc:";

	public final IJdbcUrl iJdbcUrl;
	public final SQLPlatform sqlPlatform;

	public SqlCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsg)) {
			iJdbcUrl = null;
			sqlPlatform = null;
			addError("Empty sql msg");
			return;
		}

		if (!STR.startsWith(line0, true, PFX_JDBC)) {
			addError("Except first line with starts %s", PFX_JDBC);
		}

		IJdbcUrl iJdbcUrl0;
		try {
			iJdbcUrl0 = getJdbcUrl();
		} catch (Exception ex) {
			iJdbcUrl0 = null;
			addError(ex);
		}

		iJdbcUrl = iJdbcUrl0;

		try {
			getSql();
		} catch (Exception ex) {
			addError(ex);
		}

		SQLPlatform sqlPlatform0;
		try {
			sqlPlatform0 = IJdbcUrl.getSqlPlatformFromJdbcUrl(line0);
		} catch (Exception ex) {
			sqlPlatform0 = null;
			addError(ex);
		}
		this.sqlPlatform = sqlPlatform0;

	}

	@Override
	public String toString() {
		return "SqlCallMsg{" +
				"msg='" + fullMsg + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}


	public IJdbcUrl getJdbcUrl(IJdbcUrl... defRq) {
		try {

			String[] login = USToken.two(trimCommentPfx(linesMsg.get(0)), ":");
			IT.isEq(login[0], "login", "set line with login , e.g. --login:login");

			String[] pass = USToken.two(trimCommentPfx(linesMsg.get(1)), ":");
			IT.isEq(pass[0], "password", "set line with password, e.g. --password:password");

			return IJdbcUrl.ofULP(ARR.as(line0, login[1], pass[1]));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal JdbcUrl from sql '%s'", linesMsg), defRq);
		}
	}

	public String getSql(String... defRq) {
		try {
			return IT.notEmpty(getBody(), "set sql body");
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal sql body", ex), defRq);
		}
	}


	public static SqlCallMsg of(Path file, boolean... silent) {
		return of(RW.readContent(file), silent);
	}

	public static SqlCallMsg of(String msg, boolean... silent) {
		SqlCallMsg httpCallMsg = new SqlCallMsg(msg);
		return (SqlCallMsg) httpCallMsg.throwIsErr(ARG.isDefEqTrue(silent));
	}


	public static boolean isValid(String data) {
//		if (true) {
		return SqlCallMsg.of(data, true).isValid();
//		}
//		List<String>[] headersAndBodyLines = getHeadersAndBodyLines(data);
//		if (X.empty(headersAndBodyLines) || X.empty(headersAndBodyLines[0]) || X.empty(headersAndBodyLines[1])) {
//			return false;
//		}
//		return STR.startsWith(headersAndBodyLines[0].get(0), true, PFX_JDBC);
	}


}
