package mpf.ns.space.core;

import mpc.Sys;
import mpc.args.ARG;
import mpc.X;
import mpc.fs.path.PathEntity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SsDir extends PathEntity {

//	public static void main(String[] args) {
//		List<Topic> topicFds = SsDir.of(Paths.get("/tmp/.space/")).getContent();
//		printIndexs(topicFds);
//		P.exit();
//		topicFds = SpaceFd.sortFiles(topicFds);
//		P.exit(topicFds);
//	}


	public final List<PathEntity> content = new ArrayList<>();

	public SsDir(Path path) {
		super(path);
	}

	public static SsDir of(Path path) {
		return new SsDir(path);
	}

	public void initFiles(boolean... fresh) {
		if (ARG.isDefEqTrue(fresh) || X.notEmptyAnyCollection(content)) {
			return;
		}
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
//		SpaceFd.sortFiles(content);
	}

	public static <S extends SpaceFd> void printIndexes(List<S> fdItems) {
		fdItems.forEach(fd -> Sys.p(fd.indexCached(null)));
	}

	private List<PathEntity> getContent() {
		initFiles();
		return content;
	}


}
