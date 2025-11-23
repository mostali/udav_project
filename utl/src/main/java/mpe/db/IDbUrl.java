package mpe.db;

import mpc.env.APP;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpe.call_msg.SqlCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.str.SPLIT;
import mpu.str.TKN;
import mpv.sql_morpheus.SQLPlatform;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface IDbUrl {

	default String[] toULP() {
		return ARR.merge(getDbUrlString(), toULP());
	}

	default String url2db() {
		return toULP()[2];
	}

	default String port2db() {
		return toULP()[3];
	}

	default String getDbUrlFullAuth() {
		String dbUrlFullAuth = getDbUrlFullAuth(getJdbcPrefix(), toULP());
		return dbUrlFullAuth;
	}

	static String getDbUrlFullAuth(String jdbcUrlPrefix, String[] hlp) {
		String lpPart = hlp[0] + ":" + hlp[1];
		lpPart = ":".equals(lpPart) ? "" : lpPart + "@";
		lpPart = "-:-@".equals(lpPart) ? "" : lpPart;
		return jdbcUrlPrefix + "://" + lpPart + hlp[2] + ":" + hlp[3];
	}

	default String getJdbcPrefix() {
		switch (getSqlPlatform()) {
			case SQLITE:
			case POSTGRE:
				return "jdbc";
			default:
				if (getDbUrlString().startsWith("mongodb://")) {
					return "mongodb";
				}
				throw new NI("add");
		}
	}

	default SQLPlatform getSqlPlatform() {
		return SQLPlatform.getPlatform(getDbUrlString());
	}

	String getDbUrlString();

	static IDbUrl ofULP(List<String> url_login_pass) {
		IT.isLength(url_login_pass, 3, "except 3 elements [url,login,pass]");
		return new IDbUrl() {
			@Override
			public String[] toULP() {
				return url_login_pass.toArray(String[]::new);
			}

			@Override
			public String getDbUrlString() {
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

			@Override
			public String toString() {
				return toULP()[0];
			}
		};
	}

	static @NotNull IDbUrl ofLocalDbTsm() {
		return ofULP(ARR.as(APP.ULP_DEV_LOCAL));
	}

	static IDbUrl of(Path path) {
		return new IDbUrl() {
			@Override
			public String getDbUrlString() {
				return SqlCallMsg.KEY + "sqlite:" + path;
			}

			@Override
			public String[] toLoginPass(String[]... defRq) {
				try {
					return new String[]{"sa", ""};
				} catch (Exception ex) {
					return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "toLoginPass"), defRq);
				}
			}
		};
	}

	default Path toPath(Path... defRq) {
		if (getSqlPlatform() != SQLPlatform.SQLITE) {
			return ARG.toDefThrowMsg(() -> X.f("Only sqlite database support path"));
		}
		String jdbcUrlString = getDbUrlString();
		String file = TKN.lastGreedy(jdbcUrlString, "jdbc:sqlite:", null);
		return Paths.get(file);

	}

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


	static SQLPlatform getSqlPlatformFromJdbcUrl(String jdbcUrl, SQLPlatform... defRq) {
		String dbType = ARRi.item(SPLIT.argsBy(jdbcUrl, ":"), 1, "");
		if (X.notEmpty(dbType)) {
			SQLPlatform platform = IDbUrl.getPlatform(dbType, null);
			if (X.notNull(platform)) {
				return platform;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal jdbcUrl '%s' (without SqlPlatform)", jdbcUrl), defRq);
	}

	default String[] toLoginPass(String[]... defRq) {
		return ARG.toDefThrow(() -> new RequiredRuntimeException("except impl login pass"), defRq);
	}

	default Db toDb() {
		return Db.of(this);
	}

	default String login2db() {
		return toULP()[0];
	}
}
