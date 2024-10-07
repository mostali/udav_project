package mpf.ns.space;

import mpe.core.P;
import mpu.IT;
import mpu.X;
import mpc.fs.Ns;
import mpf.ns.space.core.ISs;
import mpf.ns.space.core.SpaceFd;
import mpf.ns.space.core.SpaceRootDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class Space extends SpaceFd {
	public static final String EXT = "space";


	//	public static void main(String[] args) {
//		test();
//	}
//	public static Path getEnvLocation(String... child) {
//		Path space = Env.RPA.resolve(FIRST_DIR_NAME + ".space");
//		return child.length > 0 ? space.resolve(child[0]) : space;
//	}

	public static void test() {
		Space of = Space.of("tmp/.space");
		P.exit(of.srcType());
		IT.state(of.srcType() == ST.SPACE);
		IT.state(X.sizeOf(of.getSpaceRootDir().mainDir.size()) == 1);
	}

	public static Space of(String path) {
		return of(Paths.get(path));
	}

	public static Space of(Path path) {
		return new Space(Ns.ofSafeChild(path));
	}

	public static Space of(ISs spaceSrc) {
		ST ST = Space.of(spaceSrc.fPath()).srcType();
		return ST.buildType(spaceSrc.fPath());
	}

	public Space(Ns ns) {
		super(ns);
	}

	@Override
	public ST srcType(ST... defRq) {
		return ST.SPACE;
	}

	public ST srcTypeInst(ST... defRq) {
		return ST.SPACE;
	}

	private SpaceRootDir spaceRootDir;

	public SpaceRootDir getSpaceRootDir() {
		if (spaceRootDir == null) {
			spaceRootDir = SpaceRootDir.of(fPath());
			spaceRootDir.initFiles();

			return spaceRootDir;
		}
		return spaceRootDir;
	}


	public void walkTopic(Consumer<? extends SpaceFd> consumer, boolean mainTopic, boolean files) {
		SpaceRootDir spaceRootDir = getSpaceRootDir();
		if (mainTopic) {
			spaceRootDir.mainDir.forEach((Consumer<? super SpaceFd>) consumer);
		}
//		if (otherTopics) {
//			spaceRootDir.otherDir.forEach((Consumer<? super SpaceFd>) consumer);
//		}
		if (files) {
			spaceRootDir.otherFiles.forEach((Consumer<? super SpaceFd>) consumer);
		}
	}

	@Override
	public List<Topic> getChilds() {
		return getTopics();
	}

	public List<Topic> getTopics() {
		return getSpaceRootDir().mainDir;
	}


}
