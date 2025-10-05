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

//	private static Object invokeJarWith0(Object... kv) {
//		Object o = RFL.invokeJarStWith(getAndCheckJarLocation(), CALL_CTX[1], CALL_CTX[2], kv);
//		return o;
//	}

	@NotNull
	private static Path getAndCheckJarLocation() {
		return Env.getBinPath(CALL_CTX[0], true);
	}

//	public static void invokeArgs(RFL.JarCall jarCall, String[] args) {
//		rsltInvoke = RFL.invokeJarSt_(jarCall.jarPath(), jarCall.className(), jarCall.isMain() ? "main" : jarCall.classMethodName(), new Class[]{String[].class}, new Object[]{args});
//		String[] linesMultimapAsSeq = URuProps.toLinesMultimapAsSeq(getHeadersAsMap_All());
//	}

//	@Builder
//	public static class JarCall {
//
//		private final
//	}
}
