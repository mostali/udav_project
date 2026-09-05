package mpf.zbin;

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

	MAIL(new String[]{"sendmail-mod-v2.jar", "mod_sendmail.SendMailMod", "main"}),
	JIRA(new String[]{"jira-mod.jar", "mp.jira.JiraMod", "invokeContext0"}),
	GSV(new String[]{"gsv.jar", "netv5.app.mod.GsvMod", "invokeMsg"}),
	POI(new String[]{"poi.jar", "app_poi.PoiMod", "invokeMsg"}),
	XD(new String[]{"gdb2-mod.jar", "mp.utl_gdb.GdbMod", "invokeMsg"}),

//	JIRA(JiraBin.CALL_CTX),

	;

	private final String[] ctx_JAR_PACK_METHOD;

	public JarCall newJarCall() {
		return new JarCall(this);
	}

	public Path getPathLib() {
		return Env.getNativeBinLibsPath(ctx_JAR_PACK_METHOD[0]);
	}

	public Object invokeMsg(Object msg) {
		return newJarCall().invokeMsg(msg);
	}

	public Object invokeArgs(Object... args) {
		return newJarCall().invokeArgs(args);
	}


	@RequiredArgsConstructor
	public static class JarCall {

		private final ZBin jct;

		@SneakyThrows
		public Object invokeArgs(Object... msg) {
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx_JAR_PACK_METHOD[1], "invokeArgs", new Class[]{Object[].class}, new Object[]{msg});
		}

		@SneakyThrows
		public Object invokeMsg(Object msg) {
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx_JAR_PACK_METHOD[1], "invokeMsg", new Class[]{Object.class}, new Object[]{msg});
		}

		@SneakyThrows
		public Object invokeStringArgs(String[] args) {
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx_JAR_PACK_METHOD[1], jct.ctx_JAR_PACK_METHOD[2], new Class[]{String[].class}, new Object[]{args});
		}

		@SneakyThrows
		public Object invokeMethod_By_PareClassValue(String methodName, Object... pareClassValue) {
			Pare<Class[], Object[]> pare = RFL.RflArgs.toRflArgs_AsPareClassValue(pareClassValue);
			return RFL.invokeJarSt0(jct.getPathLib(), jct.ctx_JAR_PACK_METHOD[1], IT.NE(methodName, "set method"), pare.key(), pare.val());
		}


	}

}
