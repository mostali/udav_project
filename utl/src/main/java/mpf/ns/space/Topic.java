package mpf.ns.space;

import mpc.fs.Ns;
import mpf.ns.space.core.SpaceFd;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Topic extends SpaceFd {

	public static Topic of(String path) {
		return of(Paths.get(path));
	}

	public static Topic of(Path path) {
		return new Topic(Ns.ofSafeChild(path));
	}

	public Topic(Ns ns) {
		super(ns, ST.TOPIC);
	}

	@Override
	public ST srcType(ST... defRq) {
		return ST.TOPIC;
	}
}
