package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.fs.fd.FILE;
import mpc.rfl.RFL;
import mpe.rt.core.ExecRq;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SendMailBin {

	public static final String[] CALL_CTX = RFL.JCP.JRC_MAIL_CTX;

	//
	//
	public static final String MPK_SUBJECT = "subject";
	public static final String MPK_MSG = "msg";

	public static void main(String[] args) throws ExecRq {

		String mailEnv = "/home/dav/pjm/apps/mod_sendmail/test-tus-nsi-autotest-TEST.args";

		sendMail("TestMailAutoTestNsi", "heelo", Paths.get(mailEnv));


	}

	public static Object sendMail(String subject, String html, Path mailConfigOpts) {
		Map<String, String> multiMap = FILE.of(mailConfigOpts).toSeqOpt().asMultiMap().asMap();
		Map map = new LinkedHashMap<>(multiMap);
		map.put(MPK_SUBJECT, subject);
		map.put(MPK_MSG, html);
		return invokeContext(null, map);
	}

	//
	//
	//

	public static Object invokeContext(Object auth, Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{auth, context});
	}

	private static Object invokeJar0(Class[] types, Object[] vls) {
		Object o = RFL.invokeJarSt(getAndCheckJarLocation(), CALL_CTX[1], CALL_CTX[2], types, vls);
		return o;
	}

	@NotNull
	private static Path getAndCheckJarLocation() {
		return Env.getNativeBinLibsPath(CALL_CTX[0], true);
	}

}
