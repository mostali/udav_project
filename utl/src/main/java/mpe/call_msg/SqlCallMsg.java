package mpe.call_msg;

import mpc.exception.RequiredRuntimeException;
import mpe.call_msg.core.INode;
import mpe.call_msg.injector.NodeData;
import mpe.call_msg.injector.TrackMap;
import mpe.db.IDbUrl;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpu.str.TKN;
import mpv.sql_morpheus.SQLPlatform;

import java.nio.file.Path;
import java.util.List;

public class SqlCallMsg extends CallMsg {

	public static void main(String[] args) {
		SqlCallMsg sqlCallMsg = SqlCallMsg.ofQk("jdbc:postgresql://172.23.5.2:5432/ufos?currentSchema=ai,ufos");

		X.exit(sqlCallMsg.getJdbcUrl());
	}

	public static final String KEY = "jdbc:";

	public final IDbUrl iJdbcUrl;
	public final SQLPlatform sqlPlatform;

	public SqlCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsgHeadersAndBody())) {
			iJdbcUrl = null;
			sqlPlatform = null;
			addError("Empty sql msg");
			return;
		}

		if (!STR.startsWith(line0, true, KEY)) {
			addError("Except first line with starts %s", KEY);
		}

		IDbUrl iJdbcUrl0;
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
			sqlPlatform0 = IDbUrl.getSqlPlatformFromJdbcUrl(line0);
		} catch (Exception ex) {
			sqlPlatform0 = null;
			addError(ex);
		}
		this.sqlPlatform = sqlPlatform0;

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

	public static SqlCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static SqlCallMsg of(INode node, TrackMap.TrackId... trackId) {

		NodeData inject = node.inject(ARG.toDefOr(null, trackId));

		SqlCallMsg sqlCallMsg = (SqlCallMsg) ofQk(inject.nodeDataStr()).throwIsErr();

		inject.setCallMsg(sqlCallMsg);

		return sqlCallMsg;
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
		return STR.startsWith(data, true, KEY);
	}

}
