package zk_notes.apiv1.old;

import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.json.GsonMap;
import mpc.json.UGson;
import mpc.map.MAP;
import mpe.core.P;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.Rt;
import mpv.byteunit.ByteUnit;
import zk_os.coms.AFC;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated
public class FullDataBuilder {

	public static final int FIELD_MAX_MB = 1;

	public static void main(String[] args) {
//		P.exit(Plane.getAllSd3());
		Map map = buildMap_ROOT(null);
//		P.exit(map+"");
		P.p(Rt.buildReport(map));
	}

	public static Map buildMap_ROOT(Path sd3Root) {
		List<Path> allSd3 = sd3Root != null ? EFT.DIR.ls(sd3Root, ARR.EMPTY_LIST) : new ArrayList<>(AFC.PLANES.DIR_PLANES_LS_CLEAN(true));
		Map sd3Map = allSd3.stream().collect(Collectors.toMap(pSd3 -> pSd3.getFileName().toString(), pSd3 -> buildMap_Sd3(pSd3)));
		return sd3Map;
	}

	public static Map buildMap_Sd3(Path sd3Dir) {
		List<Path> pages = EFT.DIR.ls(sd3Dir);
		Map pagesMap = pages.stream().collect(Collectors.toMap(pDir -> pDir.getFileName().toString(), pDir -> buildMap_Page(pDir)));
//		addFilesToMap(sd3Dir, pagesMap);
		return pagesMap;
	}

//	private static void addFilesToMap(Path dir, Map data) {
//		List<Path> files = EFT.FILE.ls(dir);
//		files.forEach(f -> data.put(f.getFileName(), buildMap_File(f, false)));
//	}


	public static Map buildMap_Page(Path pageDir) {
		List<Path> forms = EFT.DIR.ls(pageDir);
		Map pageMapData = forms.stream().collect(Collectors.toMap(fDir -> fDir.getFileName().toString(), fDir -> buildMap_Form(fDir)));
//		addFilesToMap(pageDir, pageMapData);
		return pageMapData;
	}

	public static Map buildMap_Form(Path pageDir) {
		List<Path> forms = EFT.FILE.ls(pageDir);
		Map collect = forms.stream().collect(Collectors.toMap(fDir -> fDir.getFileName().toString(), fDir -> buildMap_File(fDir, false)));
		return collect;
	}

	public static Map buildMap_File(Path file, boolean skipFileData, boolean... includeBytes) {
		IT.isFileExist(file);
		long fMb = X.sizeOf(file, ByteUnit.MB);
		Map data = MAP.of("type", "file", "path", file.toString(), "name", file.getFileName().toString(), "size", X.sizeOf(file));
		if (fMb > FIELD_MAX_MB) {
			data.put("data.error", "file too large " + X.sizeOf(file, ByteUnit.MB) + "MB");
		} else if (skipFileData) {
			data.put("data.error", "skipFileData*" + X.sizeOf(file, ByteUnit.MB) + "MB");
		} else {
//			boolean isMedia = GEXT.isMediaFile(file);
//			if (isMedia) {
//				data.put("data.error", "file is media " + GEXT.of(file));
//			} else {
			byte[] bytes = RW.readAs(file, byte[].class);
			String dataStr = new String(bytes);
			if (UGson.isGson(dataStr)) {
				data.put("data", GsonMap.of(dataStr));
			} else if (EXT.of(file) == EXT.PROPS) {
				data.put("data", dataStr);
			} else {
				if (ARG.isDefEqTrue(includeBytes)) {
					data.put("data", bytes);
				}
			}
//			}
		}
		return data;

	}

//	@RequiredArgsConstructor
//	public static class FormWalker extends DirectoryWalker {
//
//		final File startDirectory;
//
//		public static void main(String[] args) {
//			FormWalker.fromDir(Paths.get("/tmp")).clean();
//		}
//
//		public static FormWalker fromDir(Path startDirectory) {
//			FormWalker formWalker = new FormWalker(startDirectory.toFile());
//			return formWalker;
//		}
//
//		@SneakyThrows
//		public List clean() {
//			List results = new ArrayList();
//			walk(startDirectory, results);
//			X.p("Found form's");
//			X.p(results);
//			return results;
//		}
//
//		protected boolean handleDirectory(File directory, int depth, Collection results) {
//			P.p("Walk dir:" + directory + ":" + depth + ":" + results.size());
//			// delete svn directories and then skip
//			if (".svn".equals(directory.getName())) {
//				return false;
//			} else {
//				return true;
//			}
//
//		}
//
//		protected void handleFile(File file, int depth, Collection results) {
//			P.p("Walk file:" + file + ":" + depth + ":" + results.size());
//			results.add(file);
//		}
//	}

}
