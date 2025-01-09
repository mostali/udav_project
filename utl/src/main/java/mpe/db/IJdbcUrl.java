package mpe.db;

import mpc.exception.RequiredRuntimeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.str.SPLIT;
import mpv.sql_morpheus.SQLPlatform;

import java.util.List;

public interface IJdbcUrl {

	static SQLPlatform getPlatform(String jdbcTypeDb, SQLPlatform... defRq) {
		final String className = jdbcTypeDb.toLowerCase();
		if (className.equals("sqlite")) {
			return SQLPlatform.SQLITE;
		} else if (className.equals("postgresql")) {
			return SQLPlatform.POSTGRE;
		} else if (className.equals("h2")) {
			return SQLPlatform.H2;
		} else if (className.equals("hsql")) {
			return SQLPlatform.HSQL;
		} else if (className.equals("sqlserver")) {
			return SQLPlatform.MSSQL;
		} else if (className.equals("mysql")) {
			return SQLPlatform.MSSQL;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Unknown sql db", jdbcTypeDb), defRq);
	}

	static IJdbcUrl ofULP(List<String> url_login_pass) {
		IT.isLength(url_login_pass, 3, "except 3 elements [url,login,pass]");
		return new IJdbcUrl() {
			@Override
			public String toJdbcUrl() {
				return url_login_pass.get(0);
			}

			@Override
			public String[] toLoginPass(String[]... defRq) {
				try {
					return new String[]{url_login_pass.get(1), url_login_pass.get(2)};
				} catch (Exception ex) {
					return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "toLoginPass"), defRq);
				}
			}
		};
	}

	static SQLPlatform getSqlPlatformFromJdbcUrl(String jdbcUrl, SQLPlatform... defRq) {
		String dbType = ARRi.item(SPLIT.argsBy(jdbcUrl, ":"), 1, "");
		if (X.notEmpty(dbType)) {
			SQLPlatform platform = IJdbcUrl.getPlatform(dbType, null);
			if (X.notNull(platform)) {
				return platform;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal jdbcUrl '%s' (without SqlPlatform)", jdbcUrl), defRq);
	}

	default SQLPlatform getSqlPlatform() {
		return SQLPlatform.getPlatform(toJdbcUrl());
	}

	String toJdbcUrl();

	default String[] toLoginPass(String[]... defRq) {
		return ARG.toDefThrow(() -> new RequiredRuntimeException("except impl login pass"), defRq);
	}

}
