package mpe.tpl_engine;

import mpc.fs.ext.EXT;
import mpu.X;

import java.nio.file.Paths;

public final class TplEngineTests {

	public static void main(String[] args) {
		CatTplCmds.main(null);
		CatCmds.main(null);
		RunnerLs.main(null);
		RunnerAllTpls.main(null);
		RunnerByIndex.main(null);
		RunnerWithArgs.main(null);
		RunnerWithArgs_ByNum.main(null);
	}

	public static class CatTplCmds extends Runner {
		public static void main(String[] args) {
			testRun("cat::1");
		}
	}

	public static class CatCmds extends Runner {
		public static void main(String[] args) {
			testRun("cat /home/dav/pjbf/ai-core/support-sql/ppo.sql");
		}
	}

	public static class RunnerLs extends Runner {
		public static void main(String[] args) {
			testRun("::?");
		}
	}

	public static class RunnerAllTpls extends Runner {
		public static void main(String[] args) {
			testRun("::*");
		}
	}

	public static class RunnerByIndex extends Runner {
		public static void main(String[] args) {
			testRun("::2");
		}
	}

	public static class RunnerWithArgs extends Runner {
		public static void main(String[] args) {
			testRun("-po 111111111");
		}
	}

	public static class RunnerWithArgs_ByNum extends Runner {
		public static void main(String[] args) {
			testRun("::1 -po 111111111");
		}
	}

	//
	//

	public static class Runner {
		static {
			initLocalDevEnv();
		}
	}

	//
	//

	private static void initLocalDevEnv() {
		TplEngineEnv.get().getSTORES().add(Paths.get("/home/dav/pjbf/ai-core/support-sql"));
		AppRun.RUNNERS.put(EXT.SQL, tplCnt -> {
			AppRun.StringIn in = (AppRun.StringIn) tplCnt;
			X.p("FAKE SQL RUNNER:\n" + in);
			return AppRun.Out.ofString(in.getIn());
		});
	}

	private static Object testRun(String cmd) {
		AppCmds.AppCmd cmdType = AppCmds.findCmd(cmd);
		Object out = cmdType.run(cmd);
		X.p("RESULT >> " + cmd);
		X.p(out);
		return out;
	}

}