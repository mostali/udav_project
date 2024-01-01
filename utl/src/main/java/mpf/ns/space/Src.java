package mpf.ns.space;

import mpc.fs.Ns;
import mpf.ns.space.core.SpaceFd;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Src extends SpaceFd {

	public static Src of(String path) {
		return of(Paths.get(path));
	}

	public static Src of(Path path) {
		return new Src(Ns.ofSafeChild(path));
	}

	public Src(Ns ns) {
		this(ns, ST.SRC);
	}

	Src(Ns ns, ST srcType) {
		super(ns, srcType);
	}

	@Override
	public ST srcType(ST... defRq) {
		return ST.SRC;
	}
}
