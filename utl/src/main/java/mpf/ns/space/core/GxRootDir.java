package mpf.ns.space.core;

import mpc.core.P;
import mpc.Sys;
import mpc.fs.path.PathEntity;
import mpf.ns.space.Topic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GxRootDir extends PathEntity {

	public static void main(String[] args) {
		List<Topic> topicFds = GxRootDir.of(Paths.get("/tmp/.space/")).getTopicFds();
		printIndexes(topicFds);
		Sys.exit();
		topicFds = SpaceFd.sortFiles(topicFds);
		P.exit(topicFds);
	}


	public final List<Topic> mainDir = new ArrayList<>();
	public GxRootDir(String path) {
		super(path);
	}

	public GxRootDir(Path path) {
		super(path);
	}

	public static GxRootDir of(Path path) {
		return new GxRootDir(path);
	}

	public void initFiles(boolean... fresh) {
//		if (ARG.isDefEqTrue(fresh) || X.notEmptyAnyOne(mainDir, otherFiles)) {
//			return;
//		}
//		UDIR.ls(path(), null).stream().forEach((path) -> {
//			EFT of = EFT.of(path);
//			switch (of) {
//				case DIR:
////					if (path.getFileName().toString().equals("_")) {
////						otherDir.add(SpaceTopic.of(path));
////					} else {
//					mainDir.add(Topic.of(path));
////					}
//					break;
//				case FILE:
//					otherFiles.add(Src.of(path));
//					break;
//				default:
//					if (L.isErrorEnabled()) {
//						L.error("Unknown space fd '{}' file://{}", of, path);
//					}
//					return;
//			}
//		});
		SpaceFd.sortFiles(mainDir);
	}

	public static <S extends SpaceFd> void printIndexes(List<S> fdItems) {
		fdItems.forEach(fd -> Sys.p(fd.indexCached(null)));
	}

	private List<Topic> getTopicFds() {
		initFiles();
		return mainDir;
	}


}
