//package zk_os;
//
//import mpu.core.ARG;
//import mpc.exception.RequiredRuntimeException;
//import mpf.ns.space.Space;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//
//public class ApiPath {
//
//	public static final String FIRST = "-";
//
//	public static Path findSpacePath(String sd3, Path... defRq) {
//		List<Path> paths = AppZosConfig.SPACE_PATH();
//		for (Path spacePath : paths) {
//			Path specificSpacePath = spacePath.resolve(sd3 + "." + Space.EXT);
//			if (Files.exists(specificSpacePath)) {
//				return specificSpacePath;
//			}
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("SpacePath '%s' not found from..\n'%s'", sd3, paths), defRq);
//	}
//}
