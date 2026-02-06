package mp.utl_ndb;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpc.types.abstype.AbsType;
import mpe.db.Db;
import mpe.db.IDbUrl;
import mpu.IT;
import mpu.core.ARG;

import java.util.List;
import java.util.Map;

public interface IJdbcUrlExt extends IDbUrl {

	static IJdbcUrlExt ofULP(List<String> url_login_pass) {
		IT.isLength(url_login_pass, 3, "except 3 elements [url,login,pass]");
		return new IJdbcUrlExt() {
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
		};
	}

	default boolean execute(String sql, Object... args) {
		return Db.execute(this, sql, args);
	}

	@SneakyThrows
	default List<List<AbsType>> queryList(String sql, Object... args) {
		return Db.queryList_(this, sql, args);
	}

	@SneakyThrows
	default List<Map<String, AbsType>> queryMap(String sql, Object... args) {
		return Db.queryMap_(this, sql, args);
	}
}
