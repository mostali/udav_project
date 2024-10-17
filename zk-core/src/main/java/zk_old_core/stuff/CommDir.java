//package zk_core.com.stuff;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class CommDir {
//	public final Path pathComDir;
//
//	public CommDir(Path pathUsrDir) {
//		this.pathComDir = pathUsrDir;
//	}
//
//	public static CommDir of(String pathUsrDir) {//, boolean... checkExistDir
//		return new CommDir(Paths.get(pathUsrDir));
//	}
//
//	public static CommDir of(File file) {//, boolean... checkExistDir
//		return new CommDir(Paths.get(file.getAbsolutePath()));
//	}
//
//	public List<File> getAllComponentFiles() {
//		List<File> list = new ArrayList<>();
//		for (File file : getAllComponentFiles("1.0")) {
//			list.add(file);
//		}
//		return list.stream().sorted().collect(Collectors.toList());
//	}
//
//	public File[] getAllComponentFiles(String version) {
//		return pathComDir.resolve(version).toFile().listFiles();
//	}
//
//	@Override
//	public String toString() {
//		return "ComDir{" +
//			   "pathComDir=" + pathComDir +
//			   '}';
//	}
//
//}
