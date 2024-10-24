package mpf.ns.space.core;

import mpu.Sys;
import mpu.core.ARG;
import mpu.X;
import mpc.fs.path.PathEntity;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpc.log.L;
import mpf.ns.space.Src;
import mpf.ns.space.Topic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpaceRootDir extends PathEntity {

//	public static void main(String[] args) {
//		List<SpaceTopic> topicFds = SpaceRootDir.of(Paths.get("/tmp/.space/")).getTopicFds();
//		printIndexs(topicFds);
//		P.exit();
//		topicFds = SpaceFd.sortFiles(topicFds);
//		P.exit(topicFds);
//	}

	public final List<Topic> mainDir = new ArrayList<>();
	//	public final List<SpaceFd> otherDir = new ArrayList<>();
	public final List<SpaceFd> otherFiles = new ArrayList<>();

	public SpaceRootDir(String path) {
		super(path);
	}

	public SpaceRootDir(Path path) {
		super(path);
	}

	public static SpaceRootDir of(Path path) {
		return new SpaceRootDir(path);
	}

	public void initFiles(boolean... fresh) {
		if (ARG.isDefEqTrue(fresh) || X.notEmptyAny_Cll(mainDir, otherFiles)) {
			return;
		}
		UDIR.ls(fPath(), null).stream().forEach((path) -> {
			EFT of = EFT.of(path);
			switch (of) {
				case DIR:
					mainDir.add(Topic.of(path));
					break;
				case FILE:
					otherFiles.add(Src.of(path));
					break;
				default:
					if (L.isErrorEnabled()) {
						L.error("Unknown space fd '{}' file://{}", of, path);
					}
					return;
			}
		});
		SpaceFd.sortFiles(mainDir);
	}

	public static <S extends SpaceFd> void printIndexs(List<S> fdItems) {
		fdItems.forEach(fd -> Sys.p(fd.indexCached(null)));
	}

	private List<Topic> getTopicFds() {
		initFiles();
		return mainDir;
	}


}
