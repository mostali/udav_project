package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.Env;
import mpc.rfl.RFL;
import mpu.IT;
import mpu.pare.Pare;

import java.nio.file.Path;

//JarCallContext
@RequiredArgsConstructor
public enum ZBin {

	MAIL(SendMailBin.CALL_CTX),

	JIRA(JiraBin.CALL_CTX),

	;

	public final String[] ctx;

	public JarCall newJarCall() {
		return new JarCall(this);
	}

	public Path getPathLib() {
		return Env.getNativeBinLibsPath(ctx[0]);
	}

	@RequiredArgsConstructor
	public static class JarCall {

		private final ZBin jct;

		@SneakyThrows
		public Object invokeStringArgs(String[] args) {
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx[1], jct.ctx[2], new Class[]{String[].class}, new Object[]{args});
		}

		@SneakyThrows
		public Object invokeMethod_By_PareClassValue(String methodName, Object... pareClassValue) {
			Pare<Class[], Object[]> pare = RFL.RflArgs.toRflArgs_AsPareClassValue(pareClassValue);
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx[1], IT.NE(methodName,"set method"), pare.key(), pare.val());
		}

	}

}
