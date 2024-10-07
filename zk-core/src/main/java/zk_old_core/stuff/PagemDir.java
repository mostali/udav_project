//package zk_core.com.stuff;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.stream.Stream;
//
//import static java.util.stream.Collectors.toList;
//
//public class PagemDir {
//	public final Path pathUsrDir;
//
//	public PagemDir(Path pathUsrDir) {
//		this.pathUsrDir = pathUsrDir;
//	}
//
//	public static PagemDir of(String pathUsrDir) {//, boolean... checkExistDir
//		return new PagemDir(Paths.get(pathUsrDir));
//	}
//
//	public File[] getAllPageFiles() {
//		return pathUsrDir.toFile().listFiles();
//	}
//
//	public List<CommDir> getAllComDirs() {
//		return Stream.of(getAllPageFiles()).map(CommDir::of).collect(toList());
//	}
//
//	@Override
//	public String toString() {
//		return "PageDir{" +
//			   "pathUsrDir=" + pathUsrDir +
//			   '}';
//	}
//}
