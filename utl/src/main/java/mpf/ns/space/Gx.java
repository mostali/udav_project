package mpf.ns.space;

import mpu.IT;
import mpu.X;
import mpc.env.Env;
import mpc.fs.Ns;
import mpf.ns.space.core.GxRootDir;
import mpf.ns.space.core.ISs;
import mpf.ns.space.core.SpaceFd;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Gx extends SpaceFd {

	//	public static void main(String[] args) {
	//		test();
	//	}
	public static Path getEnvLocation() {
		return Env.RPA.resolve(".gx");
	}

	public static void test() {
		Gx of = Gx.of("/tmp/.gx/");
		IT.state(of.srcType() == ST.SPACE);
		IT.state(X.sizeOf(of.getGxRootDir().mainDir.size()) == 1);
		//		P.p(UC.notEmpty(of.getGxRootDir().otherDir));
	}

	public static Gx of(String path) {
		return of(Paths.get(path));
	}

	public static Gx of(Path path) {
		return new Gx(Ns.ofSafeChild(path));
	}

	public static Gx of(ISs spaceSrc) {
		ST ST = Gx.of(spaceSrc.fPath()).srcType();
		return ST.buildType(spaceSrc.fPath());
	}

	public Gx(Ns ns) {
		super(ns);
	}

	@Override
	public ST srcType(ST... defRq) {
		return ST.SPACE;
	}

	public ST srcTypeInst(ST... defRq) {
		return ST.SPACE;
	}

	private GxRootDir gxRootDir;

	public GxRootDir getGxRootDir() {
		if (gxRootDir == null) {
			gxRootDir = GxRootDir.of(fPath());
			gxRootDir.initFiles();

			return gxRootDir;
		}
		return gxRootDir;
	}


}
