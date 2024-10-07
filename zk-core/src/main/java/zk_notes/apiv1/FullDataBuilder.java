package zk_notes.apiv1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.json.GsonMap;
import mpc.json.UGson;
import mpc.map.UMap;
import mpe.core.P;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.Rt;
import mpv.byteunit.ByteUnit;
import org.apache.commons.io.DirectoryWalker;
import zk_page.index.qview.QView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FullDataBuilder {

	public static final int FIELD_MAX_MB = 1;

	public static void main(String[] args) {
//		P.exit(Plane.getAllSd3());
		Map map = buildMap_ROOT(null);
//		P.exit(map+"");
		P.p(Rt.buildReport(map));
	}

	static Map buildMap_ROOT(Path sd3Root) {
		List<Path> allSd3 = sd3Root != null ? EFT.DIR.ls(sd3Root, ARR.EMPTY_LIST) : QView.getAllSd3();
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
		long fMb = X.sizeOfHu(file, ByteUnit.MB);
		Map data = UMap.of("type", "file", "path", file.toString(), "name", file.getFileName().toString(), "size", X.sizeOfFile(file));
		if (fMb > FIELD_MAX_MB) {
			data.put("data.error", "file too large " + X.sizeOfHu(file, ByteUnit.MB) + "MB");
		} else if (skipFileData) {
			data.put("data.error", "skipFileData*" + X.sizeOfHu(file, ByteUnit.MB) + "MB");
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

	@RequiredArgsConstructor
	public static class RenamerWalker extends DirectoryWalker {

		final File startDirectory;

		public static void main(String[] args) {
			RenamerWalker.fromDir(Paths.get("/home/dav/.data/bea/.sd3/.index/notes/.coms")).renameAll();
		}

		public static RenamerWalker fromDir(Path startDirectory) {
			RenamerWalker RenamerWalker = new RenamerWalker(startDirectory.toFile());
			return RenamerWalker;
		}

		@SneakyThrows
		public List renameAll() {
			List results = new ArrayList();
			walk(startDirectory, results);
			X.p("Found form's");
			X.p(results);
			return results;
		}

		protected boolean handleDirectory(File directory, int depth, Collection results) {
			P.p("Walk dir:" + directory + ":" + depth + ":" + results.size());
			// delete svn directories and then skip
//			if (".svn".equals(directory.getName())) {
//				return false;
//			} else {
//				return true;
//			}
			return true;
		}

		protected void handleFile(File file, int depth, Collection results) {
			P.p("Walk file:" + file + ":" + depth + ":" + results.size());
			results.add(file);
			if (file.getName().equals("ZkPage.props")) {
//				QuestAnswer.CONTINUE_YN(file.getAbsolutePath());
				UFS_BASE.MV.move(file.toPath(), file.getParentFile().toPath().resolve("AppNotes.props"), null);
				P.p("moved:" + file);

			}
		}
	}

	@RequiredArgsConstructor
	public static class FormWalker extends DirectoryWalker {

		final File startDirectory;

		public static void main(String[] args) {
			FormWalker.fromDir(Paths.get("/tmp")).clean();
		}

		public static FormWalker fromDir(Path startDirectory) {
			FormWalker formWalker = new FormWalker(startDirectory.toFile());
			return formWalker;
		}

		@SneakyThrows
		public List clean() {
			List results = new ArrayList();
			walk(startDirectory, results);
			X.p("Found form's");
			X.p(results);
			return results;
		}

		protected boolean handleDirectory(File directory, int depth, Collection results) {
			P.p("Walk dir:" + directory + ":" + depth + ":" + results.size());
			// delete svn directories and then skip
			if (".svn".equals(directory.getName())) {
				return false;
			} else {
				return true;
			}

		}

		protected void handleFile(File file, int depth, Collection results) {
			P.p("Walk file:" + file + ":" + depth + ":" + results.size());
			results.add(file);
		}
	}

//	public static class FsWalker {
////		int levelMax,levelCur = 0;
//
//		public List<Component> fromDir(Predicate<Component> test) {
//			return findAll(ZKF.roots(), test);
//		}
//
//		public List<Component> findAll(Collection<Component> from, Predicate<Component> test) {
//			return from.stream().filter(test).collect(Collectors.toList());
//		}
//
//		public List<Component> findAllChilds(Collection<Component> from, Predicate<Component> test) {
//			Stream<Component> stream = from.stream().flatMap(c -> c.getChildren().stream());
//			if (test != null) {
//				stream = stream.filter(test);
//			}
//			return stream.collect(Collectors.toList());
//		}
//	}

//	static Map buildMap_ROOT(Path sd3Root) {
//		List<Path> allSd3 = sd3Root != null ? Plane.getAllSd3(sd3Root) : Plane.getAllSd3();
//		Map collect = allSd3.stream().filter(ANFS::isNotPageTechname).collect(Collectors.toMap(pSd3 -> pSd3.getFileName().toString(), pSd3 -> buildMap_Sd3(pSd3.getFileName().toString())));
//		return collect;
//	}
//
//	public static Map buildMap_Sd3(String sd3) {
//		List<Path> allPagenamesPaths = Plane.getAllPagenames(sd3);
//		Map collect = allPagenamesPaths.stream().filter(ANFS::isNotPageTechname).collect(Collectors.toMap(pDir -> pDir.getFileName().toString(), pDir -> buildMap_Page(Sdn.of(sd3, pDir.getFileName().toString()))));
//		return collect;
//	}
//
//	public static Map buildMap_Page(Pare sdn) {
//		List<Path> formsDirs = Plane.getAllForms(sdn);
//		Map collect = formsDirs.stream().map(formDir -> NodeDir.of(formDir, sdn)).collect(Collectors.toMap(n -> n.nodeName(), n -> n.formState().getPropsJson(GsonMap.EMPTYMAP)));
//		return collect;
//	}
}
