package zk_old_core;

import mpc.fs.Ns;
import mpc.env.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppRs {

	public static final Logger L = LoggerFactory.getLogger(AppRs.class);
	public static final String INDEX = "_";
//	public static final Path SPACE_DIR = Env.SPACE;


	public static Ns rs_ns(String pagename, String rsKey) {
		return Ns.of(Env.PD_RS, pagename, rsKey);
	}

	public static Ns rs_ns(String pagename) {
		return Ns.of(Env.PD_RS, pagename);
	}

	public static String HOME() {
		return Env.PD_RS;
	}

	public static String HOME(String name) {
		return Env.PD_RS + name;
	}
}
