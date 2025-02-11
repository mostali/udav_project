package mpe.wthttp;

import mpc.exception.RequiredRuntimeException;
import mpe.db.IJdbcUrl;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
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
		return "SqlCallMsg{" + "msg='" + fullMsg + '\'' + ", line='" + line0 + '\'' + ", state=" + state + ", errs=" + X.sizeOf0(getErrors()) + '}';
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
			return IT.notEmpty(getBodyAsString(), "set sql body");
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal sql body", ex), defRq);
		}
	}


	public static SqlCallMsg of(Path file) {
		return of(RW.readContent(file));
	}

	public static SqlCallMsg of(String msg) {
		return (SqlCallMsg) ofQk(msg).throwIsErr();
	}

	public static SqlCallMsg ofQk(String msg) {
		return new SqlCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return SqlCallMsg.ofQk(data).isValid();
	}

	public static boolean isValidKey(String data) {
		return STR.startsWith(data, true, PFX_JDBC);
	}


}
