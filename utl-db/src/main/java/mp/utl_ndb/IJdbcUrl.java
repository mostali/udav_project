package mp.utl_ndb;

import mpc.exception.RequiredRuntimeException;
import mpu.IT;
import mpu.core.ARG;
import mpv.sql_morpheus.SQLPlatform;

import java.util.List;

public interface IJdbcUrl {

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

	default SQLPlatform getSqlPlatform() {
		return SQLPlatform.getPlatform(toJdbcUrl());
	}

	String toJdbcUrl();

	default String[] toLoginPass(String[]... defRq) {
		return ARG.toDefThrow(() -> new RequiredRuntimeException("except impl login pass"), defRq);
	}

}
