//package zk_core.com.stuff;
//
//import lombok.Getter;
//import mp.core.U;
//import mp.core.fs.UDIR;
//import mp.core.fs.fd.EFT;
//import mp.core.fs.fd.UFD;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@Deprecated
//public class ComDir {
//	@Getter
//	private final Path pathComDir;
//
//	public ComDir(Path pathUsrDir) {
//		this.pathComDir = pathUsrDir;
//	}
//
//	public static ComDir of(String pathUsrDir) {
//		return of(Paths.get(pathUsrDir));
//	}
//
//	public static ComDir of(File file) {
//		return of(file.toPath());
//	}
//
//	public static ComDir of(Path file) {
//		return new ComDir(file);
//	}
//
//	public List<Path> getAllComsFiles(EFT fileType, String... child) {
//		Path path = UFD.createPathWithChild(getPathComDir(), child);
//		return UDIR.lsNativeSort(path, fileType, true);
//	}
//
////	public File[] getAllComponentFiles(String version) {
////		return pathComDir.resolve(version).toFile().listFiles();
////	}
//
//	@Override
//	public String toString() {
//		return "ComDir{" +
//			   "pathComDir=" + pathComDir +
//			   '}';
//	}
//
//	public static void main(String[] args) {
//		String page = "/home/dav/.repo.zkapp/test/body/10-part1";
//		ComDir pageDir = ComDir.of(page);
//		U.exit(pageDir.getAllComsFiles(null));
//	}
//}
